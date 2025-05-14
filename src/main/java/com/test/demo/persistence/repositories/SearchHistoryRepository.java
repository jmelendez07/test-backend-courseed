package com.test.demo.persistence.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.SearchHistory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SearchHistoryRepository extends ReactiveMongoRepository<SearchHistory, String> {
    Flux<SearchHistory> findByUserIdOrderByCreatedAtDesc(String userId);
    Flux<SearchHistory> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    Flux<SearchHistory> findByUserIdAndSearchContainingIgnoreCaseOrderByCreatedAtDesc(String userId, String search, Pageable pageable);
    Mono<SearchHistory> findByIdAndUserId(String id, String userId);
}
