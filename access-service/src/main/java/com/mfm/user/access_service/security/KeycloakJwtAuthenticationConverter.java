package com.mfm.user.access_service.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final ObjectMapper objectMapper;
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> grantedAuthorities = defaultGrantedAuthoritiesConverter.convert(jwt);

        Collection<GrantedAuthority> authorities = Stream.concat(
                Optional.of(grantedAuthorities).stream().flatMap(Collection::stream),
                extractAuthorities(jwt).stream()).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<String> rolesWithPrefix = new HashSet<>(getRealmRoles(jwt));
        return AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));
    }

    private Set<String> getRealmRoles(Jwt jwt) {
        Set<String> rolesWithPrefix = new HashSet<>();
        JsonNode json = objectMapper.convertValue(jwt.getClaim("realm_access"), JsonNode.class);
        json.elements().forEachRemaining(
                e -> e.elements().forEachRemaining(r -> rolesWithPrefix.add(createRole(r.asText()))));
        return rolesWithPrefix;
    }

    private String createRole(String... values) {
        StringBuilder role = new StringBuilder("ROLE");
        for (String value : values) {
            role.append("_").append(value.toUpperCase());
        }
        return role.toString();
    }
}
