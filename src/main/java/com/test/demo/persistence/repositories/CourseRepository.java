package com.test.demo.persistence.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.test.demo.persistence.documents.Course;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CourseRepository extends ReactiveMongoRepository<Course, String> {
    Flux<Course> findAllBy(Pageable pageable);
    Flux<Course> findByTitleRegexIgnoreCaseOrUrlRegexIgnoreCaseOrDurationRegexIgnoreCaseOrModalityRegexIgnoreCase(
            String search1, String search2, String search3, String search4, Pageable pageable);

    Flux<Course> findByInstitutionIdAndTitleRegexIgnoreCaseOrInstitutionIdAndUrlRegexIgnoreCaseOrInstitutionIdAndDurationRegexIgnoreCaseOrInstitutionIdAndModalityRegexIgnoreCase(
            String institutionId1, String search1, 
            String institutionId2, String search2, 
            String institutionId3, String search3, 
            String institutionId4, String search4,
            Pageable pageable
    );

    Flux<Course> findByCategoryIdAndTitleRegexIgnoreCaseOrCategoryIdAndUrlRegexIgnoreCaseOrCategoryIdAndDurationRegexIgnoreCaseOrCategoryIdAndModalityRegexIgnoreCase(
            String categoryId1, String search1,
            String categoryId2, String search2,
            String categoryId3, String search3,
            String categoryId4, String search4,
            Pageable pageable
    );

    Flux<Course> findByCategoryIdAndInstitutionIdAndTitleRegexIgnoreCaseOrCategoryIdAndInstitutionIdAndUrlRegexIgnoreCaseOrCategoryIdAndInstitutionIdAndDurationRegexIgnoreCaseOrCategoryIdAndInstitutionIdAndModalityRegexIgnoreCase(
            String categoryId1, String institutionId1, String search1,
            String categoryId2, String institutionId2, String search2,
            String categoryId3, String institutionId3, String search3,
            String categoryId4, String institutionId4, String search4,
            Pageable pageable
    );
    @Query("{ '$or': [ { 'title': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } }, { 'duration': { '$regex': ?0, '$options': 'i' } } ] }")
    Flux<Course> searchCourses(String text, Pageable pageable);
    Flux<Course> findByCategoryId(String categoryId, Pageable pageable);
    Flux<Course> findByInstitutionId(String institutionId, Pageable pageable);
    Flux<Course> findByType(String type, Pageable pageable);
    Flux<Course> findByUserIdAndTitleContainingOrderByCreatedAtDesc(String userId, String title, Pageable pageable);
    Mono<Long> countByInstitutionId(String institutionId);
    Flux<Course> findByTitleContainingIgnoreCase(String title);
    Flux<Course> findByUserId(String userId);
    Mono<Long> countByUserId(String userId);
}
