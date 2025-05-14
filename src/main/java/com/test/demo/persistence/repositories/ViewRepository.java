package com.test.demo.persistence.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.View;
import com.test.demo.projections.dtos.CourseViewsStatsDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ViewRepository extends ReactiveMongoRepository<View, String> {
    Mono<Long> countByCourseId(String courseId);
    Flux<View> findByCourseId(String courseId);
    Mono<Long> countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Flux<View> findByCourseId(String courseId, Pageable pageable);
    Mono<View> findByCourseIdAndUserId(String courseId, String userId);
    Flux<View> findByUserId(String userId);
    Flux<View> findByUserId(String userId, Pageable pageable);
    Mono<Long> countByUserId(String userId);
    Flux<View> findByUserIdOrderByCreatedAtDesc(String userId);

    @Aggregation(pipeline = {
        "{ $match: { createdAt: { $gte: { $dateFromParts: { 'epochSecond': ?0 } } } } }",
        "{ $project: { courseId: 1, month: { $dateToString: { format: '%Y-%m', date: '$createdAt' } } } }",
        "{ $group: { _id: { courseId: '$courseId', month: '$month' }, views: { $sum: 1 } } }",
        "{ $group: { _id: '$_id.courseId', views: { $push: { month: '$_id.month', count: '$views' } } } }",
        "{ $project: { courseId: '$_id', lastMonthViews: { $arrayElemAt: ['$views.count', 0] }, currentMonthViews: { $arrayElemAt: ['$views.count', 1] } } }",
        "{ $match: { $expr: { $lt: ['$currentMonthViews', '$lastMonthViews'] } } }"
    })
    Flux<CourseViewsStatsDto> findCoursesWithDecreasingViews(long lastMonthEpoch);
    Flux<Void> deleteByUserId(String userId);
}
