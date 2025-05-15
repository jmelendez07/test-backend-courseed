package com.test.demo.web.config;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Component
public class JwtUtil {
    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiry-time-in-seconds}")
    private Long expiryTimeInSeconds;

    public String create(Authentication authentication) {
        return JWT.create()
            .withSubject(authentication.getName())
            .withIssuer(issuer)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiryTimeInSeconds)))
            .withClaim("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
            .withClaim("password", authentication.getCredentials().toString())
            .sign(getAlgorithm());
    }

    public boolean isValid(String jwt) {
        try {
            JWT.require(getAlgorithm())
                .build()
                .verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getEmail(String jwt) {
        return JWT.require(getAlgorithm())
            .build()
            .verify(jwt)
            .getSubject();
    }

    public Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String getPassword(String token) {
        return JWT.require(getAlgorithm())
            .build()
            .verify(token)
            .getClaim("password").asString();
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        return JWT.require(getAlgorithm())
            .build()
            .verify(token)
            .getClaim("authorities")
            .asList(SimpleGrantedAuthority.class);
    }
}
