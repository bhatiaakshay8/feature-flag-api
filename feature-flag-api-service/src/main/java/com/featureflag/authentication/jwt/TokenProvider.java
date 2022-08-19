package com.featureflag.authentication.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private final JwtProperties securityJwtProperties;

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String secretKey = securityJwtProperties.getSecret();
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        long now = (new Date()).getTime();
        Date validity = new Date(now + 1000 * securityJwtProperties.getTokenValidityInSeconds());
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(authentication.getName()).claim(AUTHORITIES_KEY, authorities)
                .signWith(key).setExpiration(validity).compact();
    }
}
