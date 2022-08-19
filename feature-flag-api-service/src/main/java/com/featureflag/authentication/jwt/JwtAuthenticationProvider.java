package com.featureflag.authentication.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtProperties securityJwtProperties;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String secretKey = securityJwtProperties.getSecret();

        BearerTokenAuthenticationToken bearerTokenAuthentication = (BearerTokenAuthenticationToken) authentication;
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Decoders.BASE64.decode(secretKey))
                    .build()
                    .parseClaimsJws(bearerTokenAuthentication.getToken());

            bearerTokenAuthentication.setAuthenticated(true);
            return bearerTokenAuthentication;
        } catch (SignatureException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);

            throw new BadCredentialsException("Unauthorized");
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);

            throw new BadCredentialsException("Unauthorized");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);

            throw new BadCredentialsException("Unauthorized");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);

            throw new BadCredentialsException("Unauthorized");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);

            throw new BadCredentialsException("Unauthorized");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);

            throw new BadCredentialsException("Unauthorized");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.getName().equals(BearerTokenAuthenticationToken.class.getName());
    }
}
