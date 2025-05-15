package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.FormPredictionDto;
import com.test.demo.services.implementations.PredictionService;

import reactor.core.publisher.Mono;

import java.util.Map;

public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    public Mono<ServerResponse> getUserCourseRecomended(ServerRequest serverRequest) {
        String userId = serverRequest.queryParam("userId").orElse("");
        String courseId = serverRequest.queryParam("courseId").orElse("");

        return predictionService.getUserCourseRecomended(userId, courseId)
            .flatMap(userCourseRecomended -> ServerResponse.ok().bodyValue(userCourseRecomended))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getRecomendedCoursesByUser(ServerRequest serverRequest) {
        String userId = serverRequest.queryParam("userId").orElse("");
    
        return predictionService.getRecomendedCoursesByUser(userId)
            .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> predictCourseRecommendation(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(FormPredictionDto.class)
            .flatMap(formData -> predictionService.predictCourseRecommendation(formData))
            .flatMap(recommendation -> ServerResponse.ok().bodyValue(recommendation))
            .onErrorResume(error -> {
                error.printStackTrace();
                return ServerResponse.badRequest()
                    .bodyValue("Error al procesar la predicción: " + error.getMessage());
            });
    }

    public Mono<ServerResponse> getTotalCoursesRecomended(ServerRequest serverRequest) {
        // Extract principal from the request
        return serverRequest.principal()
            .flatMap(principal -> predictionService.getTotalCoursesRecomended(principal)
                .flatMap(totalCourses -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("totalCourses", totalCourses))
                )
            );
    }

    public Mono<ServerResponse> getRecomendedCoursesByAuth(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> predictionService.getRecomendedCoursesByAuth(
                principal, 
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            ))
                .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getRecomendedCoursesByHistoryAndAuth(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> predictionService.getRecomendedCoursesByHistoryAndAuth(
                principal, 
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            ))
                .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getRecomendedUsersByCourse(ServerRequest serverRequest) {
        String courseId = serverRequest.pathVariable("courseId");
        int page = Integer.parseInt(serverRequest.queryParam("page").orElse("0"));
        int size = Integer.parseInt(serverRequest.queryParam("size").orElse("10"));

        return predictionService.getRecomendedUsersByCourse(courseId, page, size)
            .flatMap(users -> ServerResponse.ok().bodyValue(users))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAllCoursesWithAvgConfidence(ServerRequest serverRequest) {
        return predictionService.getAllCoursesWithAvgConfidence(
            serverRequest.queryParam("search").orElse(""),
            Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
            Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
        )
            .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
}
