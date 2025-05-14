package com.test.demo.persistence.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.Reaction;
import com.test.demo.projections.dtos.MostCommonReactionDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReactionRepository extends ReactiveMongoRepository<Reaction, String> {
    Mono<Long> countByCourseId(String courseId);
    Flux<Reaction> findByCourseId(String courseId);
    Flux<Reaction> findByCourseId(String courseId, Pageable pageable);
    Flux<Reaction> findByTypeContaining(String type, Pageable pageable);
    Flux<Reaction> findByUserIdAndTypeContaining(String userId, String type, Pageable pageable);
    Mono<Reaction> findByCourseIdAndUserId(String courseId, String userId);
    Flux<Reaction> findByUserId(String userId);
    Flux<Reaction> findByType(String type);
    Mono<Long> countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
        "{ $match: { courseId: ?0 } }",
        "{ $group: { _id: '$type', count: { $sum: 1 } } }",
        "{ $sort: { count: -1 } }",
        "{ $limit: 1 }",
        "{ $project: { _id: 0, type: '$_id', count: 1 } }"
    })
    Mono<MostCommonReactionDto> findMostCommonReactionByCourseId(String courseId);
}
