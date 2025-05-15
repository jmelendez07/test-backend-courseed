package com.test.demo.services.interfaces;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.test.demo.projections.dtos.CourseDto;
import com.test.demo.projections.dtos.CourseWithRatingAvg;
import com.test.demo.projections.dtos.CourseWithReviewsCountAndReactionsCount;
import com.test.demo.projections.dtos.SaveCourseDto;

import reactor.core.publisher.Mono;

public interface InterfaceCourseService {
    Mono<List<CourseWithReviewsCountAndReactionsCount>> getTopCoursesWithReviewsAndReactions(int page, int size);
    Mono<List<CourseWithRatingAvg>> getTopCoursesWithRatingAvg(int size);
    Mono<Page<CourseDto>> getCoursesByAuthUser(Principal principal, String search, int page, int size);
    Mono<Page<CourseDto>> getAllCourses(String search, String categoryId, String institutionId, int page, int size);
    Mono<Page<CourseDto>> getCoursesByCategoryId(String categoryId, int page, int size);
    Mono<Page<CourseDto>> getCoursesByInstitutionId(String institutionId, int page, int size);
    Mono<Page<CourseDto>> getCoursesByType(String type, int page, int size);
    Mono<Page<CourseDto>> searchCoursesByText(String text, int page, int size);
    Mono<CourseDto> getCourseById(String id);
    Mono<CourseDto> createCourse(Principal principal, SaveCourseDto saveCourseDto, String baseUrl);
    Mono<CourseDto> updateCourse(Principal principal, String id, SaveCourseDto saveCourseDto, String baseUrl);
    Mono<Object> deleteCourse(Principal principal, String id);
    Mono<Long> getTotalCoursesBySuscriptor(Principal principal);
}
