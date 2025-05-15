package com.test.demo.web.config;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final String TOKEN_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtServerAuthenticationConverter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith(TOKEN_PREFIX)) {
            return Mono.empty();
        }

        String jwt = authHeader.substring(TOKEN_PREFIX.length());

        if (!jwtUtil.isValid(jwt)) {
            return Mono.empty();
        }

        String email = jwtUtil.getEmail(jwt);
        String password = jwtUtil.getPassword(jwt);
        List<SimpleGrantedAuthority> authorities = jwtUtil.getAuthorities(jwt);

        return Mono.just(new UsernamePasswordAuthenticationToken(email, password, authorities));
        
    }
    
}
