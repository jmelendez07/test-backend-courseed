package com.test.demo.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.test.demo.persistence.documents.UserCourseRecomended;

@Repository
public interface UserCourseRecomendedRepository extends ReactiveMongoRepository<UserCourseRecomended, String> {
    Mono<UserCourseRecomended> findByCourseIdAndUserProfileId(String courseId, String userProfileId);
    Flux<UserCourseRecomended> findByUserProfileIdAndRecomended(String userProfileId, boolean recomended);
}
