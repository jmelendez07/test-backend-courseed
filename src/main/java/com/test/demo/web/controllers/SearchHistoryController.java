package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.DeleteSearchHistoriesDto;
import com.test.demo.projections.dtos.SaveSearchHistoryDto;
import com.test.demo.services.implementations.SearchHistoryService;
import com.test.demo.services.implementations.ValidationService;

import reactor.core.publisher.Mono;

public class SearchHistoryController {
    
    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private ValidationService validationService;

    public Mono<ServerResponse> findByAuthUser(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> searchHistoryService.findByAuthUser(
                principal,
                serverRequest.queryParam("search").orElse(""),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            ))
                .flatMap(searchHistories -> ServerResponse.ok().bodyValue(searchHistories))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> createSearchHistory(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> serverRequest.bodyToMono(SaveSearchHistoryDto.class)
                .doOnNext(validationService::validate)
                .flatMap(saveSearchHistoryDto -> searchHistoryService.createSearchHistory(principal, saveSearchHistoryDto)
                    .flatMap(searchHistory -> ServerResponse.ok().bodyValue(searchHistory))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    }

    public Mono<ServerResponse> deleteSearchHistory(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> searchHistoryService.deleteSearchHistory(
                principal, 
                serverRequest.pathVariable("id")
            ))
                .flatMap(searchHistories -> ServerResponse.ok().bodyValue(searchHistories))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteSearchHistories(ServerRequest serverRequest) {
        return serverRequest.principal()
        .flatMap(principal -> serverRequest.bodyToMono(DeleteSearchHistoriesDto.class)
            .doOnNext(validationService::validate)
            .flatMap(deleteSearchHistoriesDto -> searchHistoryService.deleteSearchHistories(principal, deleteSearchHistoriesDto.getSearchHistories())
                .flatMap(deleted -> ServerResponse.ok().bodyValue(deleted))
                .switchIfEmpty(ServerResponse.notFound().build())
            )
        );
    }
}
