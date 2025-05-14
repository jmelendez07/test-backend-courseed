package com.test.demo.persistence.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.test.demo.persistence.documents.Review;
import com.test.demo.projections.dtos.CourseAverageRating;
import com.test.demo.projections.dtos.ReviewAvg;
import com.test.demo.projections.dtos.ReviewCountByMonth;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {
    Flux<Review> findAllBy(Pageable pageable);
    Flux<Review> findByRatingAndUserIdContaining(Pageable pageable, int rating, String userId);
    Flux<Review> findByContentContainingAndUserIdContaining(Pageable pageable, String content, String userId);
    Flux<Review> findByCourseId(String courseId);
    Flux<Review> findByUserId(String userId);
    Flux<Review> findByCourseId(String courseId, Pageable pageable);
    Flux<Review> findByUserId(String userId, Pageable pageable);
    Mono<Review> findByUserIdAndCourseId(String userId, String courseId);
    Mono<Long> countByCourseId(String courseId);

    @Aggregation({
        "{ '$group': { '_id': '$courseId', 'rating': { '$avg': '$rating' } } }",
        "{ '$sort': { 'rating': -1 } }",
        "{ '$limit': ?0 }",
        "{ '$project': { 'courseId': '$_id', 'rating': 1, '_id': 0 } }"
    })
    Flux<ReviewAvg> findTopRatedCourses(int size);

    @Aggregation(pipeline = {
        "{ $match: { updatedAt: { $gte: ?0 } } }",
        "{ $group: { _id: { year: { $year: '$updatedAt' }, monthName: { $dateToString: { format: '%B', date: '$updatedAt', timezone: 'America/New_York' } } }, count: { $sum: 1 } } }",
        "{ $sort: { '_id.year': 1, '_id.monthName': 1 } }",
        "{ '$project': { 'year': '$_id.year', 'month': '$_id.monthName', 'count': 1 } }"
    })
    Flux<ReviewCountByMonth> countReviewsLastSixMonths(LocalDate fromDate);
    Mono<Long> countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
        "{ $group: { _id: '$courseId', avgRating: { $avg: '$rating' } } }",
        "{ $match: { avgRating: { $lt: 3 } } }",
        "{ $project: { _id: 0, courseId: '$_id', avgRating: 1 } }"
    })
    Flux<CourseAverageRating> findLowRatedCourses();

    @Aggregation(pipeline = {
        "{ $match: { courseId: ?0 } }",
        "{ $group: { _id: null, avgRating: { $avg: '$rating' } } }",
        "{ $project: { _id: 0, avgRating: 1 } }"
    })
    Mono<Double> getAverageRatingByCourseId(String courseId);

    Mono<Void> deleteByUserId(String userId);
}
