package com.mfm.user.access_service.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static String getUserNameAuthenticated() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (jwtAuthenticationToken == null) {
            return null;
        }
        Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim("name");
    }
}
