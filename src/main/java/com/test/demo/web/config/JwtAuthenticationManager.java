package com.test.demo.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.test.demo.persistence.repositories.UserRepository;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        return userRepository.findByEmail(email)
            .map(user -> {
                if (password.equals(user.getPassword())) {
                    return authentication;
                } else {
                    throw new AuthenticationException("Email o contraseña de usuario incorrectos, intente nuevamente.") {};
                }
            });
    }
}
