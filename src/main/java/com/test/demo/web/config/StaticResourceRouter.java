package com.test.demo.web.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Configuration
public class StaticResourceRouter {
    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Bean
    RouterFunction<ServerResponse> imageRouter() {
        return RouterFunctions.route()
            .GET("/uploads/{folder}/{filename}", this::serveImage)
            .build();
    }

    public Mono<ServerResponse> serveImage(ServerRequest request) {
        String folder = request.pathVariable("folder");
        String filename = request.pathVariable("filename");
        Path path = Paths.get(uploadPath).resolve(folder).resolve(filename).normalize();

        if (!Files.exists(path)) {
            return ServerResponse.notFound().build();
        }

        return ServerResponse.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(BodyInserters.fromResource(new FileSystemResource(path)));
    }
}
