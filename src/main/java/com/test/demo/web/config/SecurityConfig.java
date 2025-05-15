package com.test.demo.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import com.test.demo.services.interfaces.Roles;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean 
    SecurityWebFilterChain securityWebFilterChain(
        ServerHttpSecurity serverHttpSecurity,
        ReactiveAuthenticationManager authenticationManager,
        ServerAuthenticationConverter authenticationConverter,
        CorsConfigurationSource configurationSource
    ) throws Exception {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(authenticationConverter);

        serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .cors(cors -> cors.configurationSource(configurationSource))
                .authorizeExchange(exchange -> exchange
                    .pathMatchers("/auth/login", "/auth/register", "/auth/register/subscriptor").permitAll()
                    .pathMatchers(HttpMethod.GET, "/categories", "/categories/*", "/categories/name/*").permitAll()
                    .pathMatchers(HttpMethod.POST, "/categories").hasAnyRole(Roles.ADMIN, Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.PUT, "/categories/*").hasAnyRole(Roles.ADMIN, Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.DELETE, "/categories/*").hasAnyRole(Roles.ADMIN, Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.GET, "/contents/*", "/contents/course/*").permitAll()
                    .pathMatchers(HttpMethod.POST, "/contents").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.PUT, "/contents/*").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.DELETE, "/contents/*").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.GET, "/courses", "/courses/*", "/courses/search", "/courses/type", "/courses/category/*", "/courses/institution/*").permitAll()
                    .pathMatchers(HttpMethod.GET, "/courses/reviews-reactions/count", "/courses/reviews/avg").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.POST, "/courses").hasAnyRole(Roles.ADMIN, Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.PUT, "/courses/*").hasAnyRole(Roles.ADMIN, Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.DELETE, "/courses/*").hasAnyRole(Roles.ADMIN, Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.GET, "/institutions", "/institutions/*", "/institutions/name/*", "/institutions/courses/count").permitAll()
                    .pathMatchers(HttpMethod.POST, "/institutions").hasAnyRole(Roles.ADMIN, Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.POST, "/institutions").permitAll()
                    .pathMatchers(HttpMethod.PUT, "/institutions/*").hasAnyRole(Roles.ADMIN, Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.DELETE, "/institutions/*").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.GET, "/reactions/course/*").permitAll()
                    .pathMatchers(HttpMethod.GET, "/reviews", "/reviews/months/count").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.GET, "/reviews/course/*").permitAll()
                    // .pathMatchers(HttpMethod.GET, "/users", "/users/*", "users/email/*", "/users/months/count").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.GET, "/users", "/users/*", "users/email/*", "/users/months/count").permitAll()
                    .pathMatchers(HttpMethod.POST, "/users/create").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.PUT, "/users/*", "/users/email/*", "/users/password/*", "/users/roles/*").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.DELETE, "/users/*").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.GET, "/roles", "/roles/users/count").hasRole(Roles.ADMIN)
                    .pathMatchers(HttpMethod.GET, "/views/course/*").permitAll()
                    .pathMatchers(HttpMethod.GET, "/subscriptions/auth").hasRole(Roles.SUBSCRIBER)
                    .pathMatchers(HttpMethod.POST, "/subscriptions/confirm").permitAll()
                    .pathMatchers(HttpMethod.GET, "/predictions/user-course-recomended", "/predictions/courses-recomended-for-user").permitAll()
                    .pathMatchers(HttpMethod.POST, "/predictions/form-prediction").permitAll()
                    .pathMatchers(HttpMethod.GET, "/predictions/user-course-recomended", "/predictions/courses-recomended-for-user", "/predictions/form-prediction").permitAll()
                    .pathMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/").permitAll()
                    .pathMatchers(HttpMethod.GET, "/health").permitAll()
                    .anyExchange().authenticated())
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return serverHttpSecurity.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
