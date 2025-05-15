package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import com.test.demo.services.implementations.CategoryService;

import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

public class CategoryController {

    @Autowired 
    private CategoryService categoryService;

    public Mono<ServerResponse> getAllCategories(ServerRequest serverRequest) {
        return categoryService
            .getAllCategories(
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10")) 
            )
            .flatMap(categories -> ServerResponse.ok().bodyValue(categories))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getCategoryById(ServerRequest serverRequest) {
        return categoryService.getCategoryById(serverRequest.pathVariable("id"))
            .flatMap(category -> ServerResponse.ok().bodyValue(category))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getCategoryByName(ServerRequest serverRequest) {
        return categoryService.getCategoryByName(serverRequest.pathVariable("name"))
            .flatMap(category -> ServerResponse.ok().bodyValue(category))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteCategory(ServerRequest serverRequest) {
        return categoryService.deleteCategory(serverRequest.pathVariable("id"))
            .flatMap(c -> ServerResponse.ok().bodyValue(c))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
}
