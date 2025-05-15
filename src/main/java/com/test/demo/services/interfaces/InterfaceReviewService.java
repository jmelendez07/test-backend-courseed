package com.test.demo.services.interfaces;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.test.demo.projections.dtos.CourseAverageRating;
import com.test.demo.projections.dtos.CreateReviewDto;
import com.test.demo.projections.dtos.ReviewCountByMonth;
import com.test.demo.projections.dtos.ReviewDto;
import com.test.demo.projections.dtos.TotalReviewsDto;
import com.test.demo.projections.dtos.UpdateReviewDto;
import reactor.core.publisher.Mono;

public interface InterfaceReviewService {
    Mono<TotalReviewsDto> getTotalReviews();
    Mono<List<CourseAverageRating>> getTotalNegativeReviews();
    Mono<Page<ReviewDto>> getAllReviews(int page, int size, String search, String userId);
    Mono<Page<ReviewDto>> getReviewsByCourseId(String courseId, int page, int size);
    Mono<Page<ReviewDto>> getReviewsByAuthUser(Principal principal, int page, int size, String search);
    Mono<List<ReviewCountByMonth>> getReviewCountsForLastSixMonths();
    Mono<Object> createReview(Principal principal, CreateReviewDto createReviewDto);
    Mono<ReviewDto> updateReview(Principal principal, String id, UpdateReviewDto saveReviewDto);
    Mono<Boolean> deleteReview(Principal principal, String id);
    Mono<Integer> getTotalReviewsBySuscriptor(Principal principal);
}
