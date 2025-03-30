package com.mfm.user.access_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    @Bean
    SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwtDecoder -> jwtDecoder.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter)));

        http.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
