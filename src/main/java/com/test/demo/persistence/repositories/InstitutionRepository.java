package com.test.demo.persistence.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.Institution;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface InstitutionRepository extends ReactiveMongoRepository<Institution, String> {
    Flux<Institution> findAllBy(Pageable pageable);
    Mono<Institution> findByName(String name);
    Mono<Institution> findByUserId(String userId);
}
