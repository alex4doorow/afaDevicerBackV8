package com.afa.devicer.back.security;

import com.afa.devicer.back.controllers.internal.ControllerConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@SuppressWarnings({"PMD.SignatureDeclareThrowsException"})
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(configurer -> configurer.configure(http));
        http.sessionManagement(configurer -> configurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(ControllerConstants.ACTUATOR + SecurityConstants.MASK).permitAll()
                .requestMatchers(ControllerConstants.API_DOCS + SecurityConstants.MASK).permitAll()
                .requestMatchers(ControllerConstants.SWAGGER + SecurityConstants.MASK).permitAll()
                .requestMatchers(ControllerConstants.SWAGGER + SecurityConstants.MASK_HTML).permitAll()
                .anyRequest().authenticated()
        );
        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
        return authorities -> {
            final Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            final GrantedAuthority authority = authorities.iterator().next();
            final boolean isOidc = authority instanceof OidcUserAuthority;

            if (isOidc) {
                final OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                final OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                // Tokens can be configured to return roles under
                // Groups or REALM ACCESS hence have to check both
                if (userInfo.hasClaim(SecurityConstants.REALM_ACCESS_CLAIM)) {
                    final Map<String, Object> realmAccess = userInfo.getClaimAsMap(SecurityConstants.REALM_ACCESS_CLAIM);
                    final Collection<String> roles = (Collection<String>) realmAccess.get(SecurityConstants.ROLES_CLAIM);
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
                else if (userInfo.hasClaim(SecurityConstants.GROUPS)) {
                    final Collection<String> roles = userInfo.getClaim(SecurityConstants.GROUPS);
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            }
            else {
                final OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
                final Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

                if (userAttributes.containsKey(SecurityConstants.REALM_ACCESS_CLAIM)) {
                    final Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get(SecurityConstants.REALM_ACCESS_CLAIM);
                    final Collection<String> roles = (Collection<String>) realmAccess.get(SecurityConstants.ROLES_CLAIM);
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            }
            return mappedAuthorities;
        };
    }

    private Collection<GrantedAuthority> generateAuthoritiesFromClaim(final Collection<String> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(
                Collectors.toList());
    }
}
