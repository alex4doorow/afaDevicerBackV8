package com.afa.devicer.back.components;

import com.afa.devicer.back.controllers.internal.ControllerConstants;
import com.afa.devicer.back.dto.BaseResponse;
import com.afa.devicer.back.entities.people.IPerson;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.enums.DevicerErrors;
import com.afa.devicer.back.exceptions.DevicerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
@Order(FilterOrderConstants.DEACTIVATED_USER_FILTER)
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.PreserveStackTrace"})
public class DeactivatedUserFilter implements Filter {

    private final IPerson iPerson;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain)
            throws IOException, ServletException {
        // Get rid of logging some unnecessary urls & content types
        final String requestUri = ((HttpServletRequest) request).getRequestURI();
        if (requestUri.startsWith(ControllerConstants.ACTUATOR) ||
                requestUri.startsWith(ControllerConstants.API_DOCS) ||
                requestUri.startsWith(ControllerConstants.SWAGGER)) {
            chain.doFilter(request, response);
        }
        else {
            // Проверка наличия пользователя, и его признака деактивации
            final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            try {
                final String subject = getSubject(principal);
                final UUID keycloakUuid = getKeycloakUuid(subject);

                if (iPerson.count() > 0) {
                    final Person person = getPerson(keycloakUuid);
                    validatePersonDeactivated(person);
                }
                chain.doFilter(request, response);
            }
            catch (DevicerException ex) {
                final byte[] errorContent = objectMapper.writeValueAsBytes(
                        new BaseResponse(ex.getErrorCode(), ex.getErrorMessage()));
                final HttpServletResponse errorResponse = (HttpServletResponse) response;
                errorResponse.setContentType(MediaType.APPLICATION_JSON.toString());
                errorResponse.setContentLength(errorContent.length);
                errorResponse.getOutputStream().write(errorContent);
                errorResponse.setStatus(HttpStatus.OK.value());
                log.warn("DeactivatedUserFilter: {}", ex.getErrorMessage());
            }
        }
    }

    private String getSubject(final Object principal) {
        if (principal instanceof Jwt jwtPrincipal) {
            return jwtPrincipal.getSubject();
        }
        throw new DevicerException(DevicerErrors.KEYCLOAK_PRINCIPAL_IS_NULL);
    }

    private UUID getKeycloakUuid(final String subject) {
        try {
            return UUID.fromString(subject);
        }
        catch (IllegalArgumentException e) {
            throw new DevicerException(DevicerErrors.KEYCLOAK_PRINCIPAL_INVALID, subject);
        }
    }

    private Person getPerson(final UUID keycloakUuid) {
        return iPerson.findByKeycloakUuid(keycloakUuid)
                .orElseThrow(() ->
                        new DevicerException(DevicerErrors.KEYCLOAK_UUID_UNKNOWN, keycloakUuid));
    }

    private void validatePersonDeactivated(final Person person) {
        if (Boolean.TRUE.equals(person.getDeactivated())) {
            throw new DevicerException(DevicerErrors.PERSON_IS_DEACTIVATED, person.getFullName());
        }
    }
}
