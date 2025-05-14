package com.test.demo.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.UserInterest;

import reactor.core.publisher.Mono;

@Repository
public interface UserInterestRepository extends ReactiveMongoRepository<UserInterest, String> {
    Mono<UserInterest> findByUserProfileId(String userProfileId);
}
