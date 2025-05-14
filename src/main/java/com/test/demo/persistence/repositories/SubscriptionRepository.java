package com.test.demo.persistence.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SubscriptionRepository extends ReactiveMongoRepository<Subscription, String> {
    Mono<Subscription> findFirstByUserIdAndStateOrderByEndDateDesc(String userId, String state);
    Flux<Subscription> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Mono<Long> countByUserId(String userId);
}
