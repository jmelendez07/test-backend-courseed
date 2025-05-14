package com.test.demo.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.Content;

import reactor.core.publisher.Flux;

@Repository
public interface ContentRepository extends ReactiveMongoRepository<Content, String> {
    Flux<Content> findByCourseId(String courseId);
}
