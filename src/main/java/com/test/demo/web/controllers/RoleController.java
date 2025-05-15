package com.test.demo.web.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.persistence.documents.Role;
import com.test.demo.services.implementations.RoleService;

import reactor.core.publisher.Mono;

public class RoleController {

    @Autowired
    private RoleService roleService;

    public Mono<ServerResponse> getAllRoles(ServerRequest serverRequest) {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleService.findAll(), Role.class);
    }

    public Mono<ServerResponse> getRolesWithUserCount(ServerRequest serverRequest) {
        return roleService.getRolesWithUserCount()
            .flatMap(reviews-> ServerResponse.ok().bodyValue(reviews))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
}
