package com.mfm.user.user_service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;
import java.util.Map;

public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {

    @Override
    public SecurityContext createSecurityContext(WithMockJwt annotation) {

        var jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "9a9c7936-bfc3-4d11-ae58-1ab61d78e0f7")
                .claim("name", "User Test")
                .claim("realm_access", Map.of("roles", List.of(annotation.roles())))
                .build();

        var authorities = AuthorityUtils.createAuthorityList(annotation.roles());
        var token = new JwtAuthenticationToken(jwt, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        return context;
    }
}
