package com.afa.devicer.back.services;

import com.afa.core.components.CacheConstants;
import com.afa.core.dto.UserInfoDbModel;
import com.afa.core.dto.UserInfoDto;
import com.afa.devicer.back.entities.people.IPerson;
import com.afa.devicer.back.entities.people.Person;
import com.afa.devicer.back.entities.people.Person_;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.core.utils.DefaultConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String ROLE_ADMIN = "admin";

    private final IPerson iPerson;

    @Cacheable(value = CacheConstants.CACHE_USERS, key = "#principal.subject")
    public UserInfoDto fillUserInfo(@AuthenticationPrincipal final Jwt principal) {
        validatePrincipal(principal);

        // Проверка на существование пользователей в принципе.
        // Если БД пуста (например, после полного обновления схемы),
        // то будут невозможны любые действия, в том числе - добавление новых пользователей.
        // В этом случае мы пропускаем только запросы от роли ADMIN, и возвращаем подменный объект с непустыми полями
        // (чтобы не возникло исключений там, где эти поля могут применяться)
        if (iPerson.count() == 0 && isAdminPrincipal(principal)) {
            return fillMockUser();
        }

        // Проверка на существование пользователя с таким keycloakUuid в БД
        final UUID keycloakUuid = UUID.fromString(principal.getSubject());
        final UserInfoDbModel userModel = iPerson.fillUserInfo(keycloakUuid)
                .orElseThrow(() -> new DevicerException(DevicerErrors.KEYCLOAK_UUID_UNKNOWN, keycloakUuid));

        // Проверка на деактивацию пользователя, применяется в цепочке фильтрации запросов
        if (userModel.getRecStatus().equals(DefaultConstants.DELETED)) {
            throw new DevicerException(DevicerErrors.USER_IS_DEACTIVATED, userModel.getFullName());
        }

        // Пользователь может быть сотрудником
        final UserInfoDto user = fillUserInfo(keycloakUuid, userModel);
        if (userModel.getEmployeeId() == null) {
            throw new DevicerException(DevicerErrors.WRONG_USER, keycloakUuid);
        }

        user.setEmployeeInfo(fillEmployeeInfo(userModel));
        return user;
    }

    public Person findByIdOrThrow(final Long id) {
        return findByIdOptional(id).orElseThrow(() ->
                new DevicerException(DevicerErrors.DB_ENTITY_NOT_FOUND, Person_.class_, id)
        );
    }

    private Optional<Person> findByIdOptional(final Long id) {
        return iPerson.findById(id);
    }

    @SuppressWarnings({"PMD.PreserveStackTrace"})
    private void validatePrincipal(final Jwt principal) {
        if (principal == null || principal.getSubject() == null) {
            throw new DevicerException(DevicerErrors.KEYCLOAK_PRINCIPAL_IS_NULL);
        }
        try {
            UUID.fromString(principal.getSubject());
        }
        catch (IllegalArgumentException e) {
            throw new DevicerException(DevicerErrors.KEYCLOAK_PRINCIPAL_INVALID, principal.getSubject());
        }
    }

    private boolean isAdminPrincipal(final Jwt principal) {
        if (principal.hasClaim(REALM_ACCESS)) {
            final Map<String, Object> realmAccess = principal.getClaimAsMap(REALM_ACCESS);
            final Object roles = realmAccess.get(ROLES_CLAIM);
            return roles instanceof List && ((List<?>) roles).contains(ROLE_ADMIN);
        }
        return false;
    }

    private UserInfoDto fillMockUser() {
        final UUID mockUuid = UUID.randomUUID();
        return UserInfoDto.builder()
                .keycloakUuid(mockUuid)
                .personId(0L)
                .firstName("Mock")
                .lastName("User")
                .recStatus(DefaultConstants.ACTIVE)
                .build();
    }

    private UserInfoDto fillUserInfo(final UUID keycloakUuid, final UserInfoDbModel user) {
        return UserInfoDto.builder()
                .keycloakUuid(keycloakUuid)
                .personId(user.getPersonId())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .recStatus(user.getRecStatus())
                .build();
    }

    private UserInfoDto.EmployeeInfoDto fillEmployeeInfo(final UserInfoDbModel user) {
        return user.getEmployeeId() == null
            ? null
            : UserInfoDto.EmployeeInfoDto.builder()
                .id(user.getEmployeeId())
                .recStatus(user.getRecStatus())
                .build();
    }
}
