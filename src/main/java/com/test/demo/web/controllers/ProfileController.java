package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.SaveProfileDto;
import com.test.demo.services.implementations.ProfileService;
import com.test.demo.services.implementations.ValidationService;

import reactor.core.publisher.Mono;

public class ProfileController {
    
    @Autowired
    private ProfileService profileService;

    @Autowired
    private ValidationService validationService;

    public Mono<ServerResponse> getProfileById(ServerRequest serverRequest) {
        return profileService.getProfileById(serverRequest.pathVariable("id"))
            .flatMap(profile -> ServerResponse.ok().bodyValue(profile))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getProfileByAuth(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> profileService.getProfileByAuth(principal)
                .flatMap(profile -> ServerResponse.ok().bodyValue(profile))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> createProfile(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SaveProfileDto.class)
            .doOnNext(validationService::validate)
            .flatMap(saveProfile -> serverRequest.principal()
                .flatMap(principal -> profileService.createProfile(principal, saveProfile)
                    .flatMap(profile -> ServerResponse.ok().bodyValue(profile))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    }

    public Mono<ServerResponse> updateProfile(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SaveProfileDto.class)
            .doOnNext(validationService::validate)
            .flatMap(saveProfile -> serverRequest.principal()
                .flatMap(principal -> profileService.updateProfile(principal, saveProfile)
                    .flatMap(profile -> ServerResponse.ok().bodyValue(profile))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    }
}
