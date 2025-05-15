package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.SaveViewDto;
import com.test.demo.services.implementations.ValidationService;
import com.test.demo.services.implementations.ViewService;

import reactor.core.publisher.Mono;

public class ViewController {
    
    @Autowired
    private ViewService viewService;

    @Autowired
    private ValidationService validationService;

    public Mono<ServerResponse> getTotalViews(ServerRequest serverRequest) {
        return viewService.getTotalViews()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    } 

    public Mono<ServerResponse> findCoursesWithDecreasingViews(ServerRequest serverRequest) {
        return viewService.findCoursesWithDecreasingViews()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findViewsByCourseId(ServerRequest serverRequest) {
        return viewService.findViewsByCourseId(
            serverRequest.pathVariable("courseId"),
            Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
            Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
        )
            .flatMap(views -> ServerResponse.ok().bodyValue(views))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findViewsByAuthUser(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> viewService.findViewsByAuthUser(
                principal, 
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10")),
                serverRequest.queryParam("search").orElse("")
            ))
                .flatMap(views -> ServerResponse.ok().bodyValue(views))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createView(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> serverRequest.bodyToMono(SaveViewDto.class)
                .doOnNext(validationService::validate)
                .flatMap(SaveViewDto -> viewService.createView(principal, SaveViewDto)
                    .flatMap(view -> ServerResponse.ok().bodyValue(view))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    }

    public Mono<ServerResponse> getTotalViewsBySuscriptor(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> viewService.getTotalViewsBySuscriptor(principal)
                .flatMap(views -> ServerResponse.ok().bodyValue(views))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    } 
}
