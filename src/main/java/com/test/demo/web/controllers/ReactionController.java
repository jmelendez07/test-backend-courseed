package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.SaveReactionDto;
import com.test.demo.services.implementations.ReactionService;
import com.test.demo.services.implementations.ValidationService;

import reactor.core.publisher.Mono;

public class ReactionController {
    @Autowired
    private ReactionService reactionService;
    
    @Autowired
    private ValidationService validationService;

    public Mono<ServerResponse> getTotalReactions(ServerRequest serverRequest) {
        return reactionService.getTotalReactions()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    } 

    public Mono<ServerResponse> findReactionsByCourseId(ServerRequest serverRequest) {
        return reactionService.findReactionsByCourseId(
            serverRequest.pathVariable("courseId"),
            Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
            Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
        )
            .flatMap(reactions -> ServerResponse.ok().bodyValue(reactions))
            .switchIfEmpty(ServerResponse.notFound().build());
    } 

    public Mono<ServerResponse> findReactionsByAuthUser(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> reactionService.findReactionsByAuthUser(
                principal, 
                serverRequest.queryParam("type").orElse(""),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))   
            ))
                .flatMap(reactions -> ServerResponse.ok().bodyValue(reactions))
                .switchIfEmpty(ServerResponse.notFound().build());
    } 

    public Mono<ServerResponse> createReaction(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> serverRequest.bodyToMono(SaveReactionDto.class)
                .doOnNext(validationService::validate)
                .flatMap(saveReactionDto -> reactionService.createReaction(principal, saveReactionDto)
                    .flatMap(reaction -> ServerResponse.ok().bodyValue(reaction))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    } 

    public Mono<ServerResponse> updateReaction(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> serverRequest.bodyToMono(SaveReactionDto.class)
                .doOnNext(validationService::validate)
                .flatMap(saveReactionDto -> reactionService.updateReaction(principal, saveReactionDto)
                    .flatMap(reaction -> ServerResponse.ok().bodyValue(reaction))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    } 

    public Mono<ServerResponse> deleteReaction(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> reactionService.deleteReaction(principal, serverRequest.pathVariable("id"))
                .flatMap(reaction -> ServerResponse.ok().bodyValue(reaction))
                .switchIfEmpty(ServerResponse.notFound().build())
            ); 
    }

    public Mono<ServerResponse> getTotalReactionsBySuscriptor(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> reactionService.getTotalReactionsBySuscriptor(principal)
                .flatMap(reactions -> ServerResponse.ok().bodyValue(reactions))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }
}
