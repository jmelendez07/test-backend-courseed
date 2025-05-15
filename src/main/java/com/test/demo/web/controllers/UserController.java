package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.test.demo.projections.dtos.CreateUserDto;
import com.test.demo.projections.dtos.UpdateUserEmailDto;
import com.test.demo.projections.dtos.UpdateUserPasswordDto;
import com.test.demo.projections.dtos.UpdateUserRolesDto;
import com.test.demo.services.implementations.UserService;
import com.test.demo.services.implementations.ValidationService;

import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ValidationService validationService;

    public Mono<ServerResponse> getTotalUsers(ServerRequest serverRequest) {
        return userService.getTotalUsers()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return userService
            .getAllUsers(
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10")) 
            )
            .flatMap(users -> ServerResponse.ok().bodyValue(users))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAllUsersCountByInterestOrModality(ServerRequest serverRequest) {
        return userService.getAllUsersCountByInterestOrModality(
                serverRequest.queryParam("interest").orElse(""),
                serverRequest.queryParam("modality").orElse("")
            )
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAllUsersCount(ServerRequest serverRequest) {
        return userService.getAllUsersCount()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        return userService.getUserById(serverRequest.pathVariable("id"))
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        return userService.getUserByEmail(serverRequest.pathVariable("email"))
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getUserCountForLastSixMonths(ServerRequest serverRequest) {
        return userService.getUserCountForLastSixMonths()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserDto.class)
        .doOnNext(validationService::validate)
        .flatMap(createUserDto -> userService.createUser(createUserDto)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build())   
        );
    }

    public Mono<ServerResponse> updateUserEmail(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateUserEmailDto.class)
            .doOnNext(validationService::validate)
            .flatMap(updateUserEmailDto -> userService.updateUserEmail(serverRequest.pathVariable("id"), updateUserEmailDto)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> updateUserPassword(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateUserPasswordDto.class)
            .doOnNext(validationService::validate)
            .flatMap(updateUserPasswordDto -> userService.updateUserPassword(serverRequest.pathVariable("id"), updateUserPasswordDto)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> updateUserRoles(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateUserRolesDto.class)
            .doOnNext(validationService::validate)
            .flatMap(updateUserRolesDto -> userService.updateUserRoles(serverRequest.pathVariable("id"), updateUserRolesDto)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }
    
    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        return userService.deleteUser(serverRequest.pathVariable("id"))
            .flatMap(u -> ServerResponse.ok().bodyValue(u))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
    
}
