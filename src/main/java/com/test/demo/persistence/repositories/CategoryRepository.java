package com.test.demo.persistence.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.Category;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
    Flux<Category> findAllBy(Pageable pageable);
    Flux<Category> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    Mono<Category> findByName(String name);
}
