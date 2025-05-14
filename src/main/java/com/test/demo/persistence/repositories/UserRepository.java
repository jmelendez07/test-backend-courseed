package com.test.demo.persistence.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Flux<User> findAllBy(Pageable pageable);
    Mono<User> findByEmail(String email);
    Mono<User> findByEmailAndIdNot(String email, String id);
    Flux<User> findByRolesContaining(String role);
    Flux<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    Mono<Long> countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
