package com.test.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route()
            .GET("/hello", request -> ServerResponse.ok().bodyValue("Hola desde Spring WebFlux!"))
            .GET("/health", request -> ServerResponse.ok().bodyValue("OK"))
            .build();
    }
}