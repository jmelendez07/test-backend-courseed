package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.CreateReviewDto;
import com.test.demo.projections.dtos.UpdateReviewDto;
import com.test.demo.services.implementations.ReviewService;
import com.test.demo.services.implementations.ValidationService;

import reactor.core.publisher.Mono;

public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ValidationService validationService;

    public Mono<ServerResponse> getTotalReviews(ServerRequest serverRequest) {
        return reviewService.getTotalReviews()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    } 

    public Mono<ServerResponse> getTotalNegativeReviews(ServerRequest serverRequest) {
        return reviewService.getTotalNegativeReviews()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    } 

    public Mono<ServerResponse> getAllReviews(ServerRequest serverRequest) {
        return reviewService
            .getAllReviews(
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10")),
                serverRequest.queryParam("search").orElse(""),
                serverRequest.queryParam("userId").orElse("")
            )
            .flatMap(reviews-> ServerResponse.ok().bodyValue(reviews))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getReviewsByCourseId(ServerRequest serverRequest) {
        return reviewService
            .getReviewsByCourseId(
                serverRequest.pathVariable("courseId"),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10")) 
            )
            .flatMap(reviews-> ServerResponse.ok().bodyValue(reviews))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getReviewsByAuthUser(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> reviewService
                .getReviewsByAuthUser(
                    principal,
                    Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                    Integer.parseInt(serverRequest.queryParam("size").orElse("10")),
                    serverRequest.queryParam("search").orElse("")
                )
                .flatMap(reviews-> ServerResponse.ok().bodyValue(reviews))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> getReviewCountsForLastSixMonths(ServerRequest serverRequest) {
        return reviewService.getReviewCountsForLastSixMonths()
            .flatMap(reviews-> ServerResponse.ok().bodyValue(reviews))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createReview(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateReviewDto.class)
            .doOnNext(validationService::validate)
            .flatMap(createReviewDto -> serverRequest.principal()
                .flatMap(principal -> reviewService.createReview(principal, createReviewDto)
                    .flatMap(review -> ServerResponse.ok().bodyValue(review))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateReviewDto.class)
            .doOnNext(validationService::validate)
            .flatMap(updateReviewDto -> serverRequest.principal()
                .flatMap(principal -> reviewService.updateReview(principal, serverRequest.pathVariable("id"), updateReviewDto)
                    .flatMap(review -> ServerResponse.ok().bodyValue(review))
                    .switchIfEmpty(ServerResponse.notFound().build())
                )
            );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> reviewService.deleteReview(principal, serverRequest.pathVariable("id"))
                .flatMap(review -> ServerResponse.ok().bodyValue(review))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> getTotalReviewsBySuscriptor(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> reviewService.getTotalReviewsBySuscriptor(principal)
                .flatMap(review -> ServerResponse.ok().bodyValue(review))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }
}
