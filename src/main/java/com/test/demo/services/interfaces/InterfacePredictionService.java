package com.test.demo.services.interfaces;

import com.test.demo.persistence.documents.UserCourseRecomended;

import reactor.core.publisher.Mono;

public interface InterfacePredictionService {
    Mono<UserCourseRecomended> getUserCourseRecomended(String userId, String courseId);
}
