package com.test.demo.web.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.SaveCourseDto;
import com.test.demo.services.implementations.CourseService;
import com.test.demo.services.implementations.ValidationService;

import reactor.core.publisher.Mono;

public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ValidationService validationService;

    @Value("${spring.webflux.base-path:}")
    private String basePath;

    public Mono<ServerResponse> getAllCourses(ServerRequest serverRequest) {
        return courseService
            .getAllCourses(
                serverRequest.queryParam("search").orElse(""),
                serverRequest.queryParam("categoryId").orElse(""),
                serverRequest.queryParam("institutionId").orElse(""),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            )
            .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getCourseById(ServerRequest serverRequest) {
        return courseService.getCourseById(serverRequest.pathVariable("id"))
            .flatMap(course -> ServerResponse.ok().bodyValue(course))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getCoursesByAuthUser(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> courseService.getCoursesByAuthUser(
                principal,
                serverRequest.queryParam("search").orElse(""),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            ))
            .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> searchCoursesByText(ServerRequest serverRequest) {
        return courseService
            .searchCoursesByText(
                serverRequest.queryParam("text").orElse(""),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            )
            .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
            .switchIfEmpty(ServerResponse.notFound().build()); 
    }

    public Mono<ServerResponse> getCoursesByCategoryId(ServerRequest serverRequest) {
        return courseService
            .getCoursesByCategoryId(
                serverRequest.pathVariable("categoryId"),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            )
            .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getCoursesByInstitutionId(ServerRequest serverRequest) {
        return courseService
            .getCoursesByInstitutionId(
                serverRequest.pathVariable("institutionId"),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            )
            .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getCoursesByType(ServerRequest serverRequest) {
        return courseService
            .getCoursesByType(
                serverRequest.queryParam("type").orElse(""),
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            )
                .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getTopCoursesWithReviewsAndReactions(ServerRequest serverRequest) {
        return courseService.getTopCoursesWithReviewsAndReactions(
            Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
            Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
        )
        .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getTopCoursesWithRatingAvg(ServerRequest serverRequest) {
        return courseService.getTopCoursesWithRatingAvg(
            Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
        )
        .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createCourse(ServerRequest serverRequest) {
        return serverRequest.multipartData()
            .flatMap(parts -> {
                FilePart image = (FilePart) parts.getFirst("image");
                SaveCourseDto saveCourseDto = new SaveCourseDto();

                saveCourseDto.setUrl(getFormFieldValue(parts, "url"));
                saveCourseDto.setTitle(getFormFieldValue(parts, "title"));
                saveCourseDto.setDescription(getFormFieldValue(parts, "description"));
                saveCourseDto.setModality(getFormFieldValue(parts, "modality"));
                saveCourseDto.setPrice(parseDoubleOrDefault(getFormFieldValue(parts, "price"), 0.0));
                saveCourseDto.setDuration(getFormFieldValue(parts, "duration"));
                saveCourseDto.setCategoryId(getFormFieldValue(parts, "categoryId"));
                saveCourseDto.setInstitutionId(getFormFieldValue(parts, "institutionId"));

                if (image != null && image.filename() != null && !image.filename().isBlank()) {
                    saveCourseDto.setImage(image);
    
                    if (!saveCourseDto.isValidImage()) {
                        Map<String, String> errors = new HashMap<>();
                        errors.put("image", "La imagen debe ser de tipo válido (jpg, png, jpeg) y menor a 2 MB.");
                        
                        return ServerResponse.badRequest().bodyValue(errors);
                    }
                }

                String baseUrl = serverRequest.uri().getScheme() + "://" + serverRequest.uri().getHost() + 
                    ((serverRequest.uri().getPort() != -1 ? ":" + serverRequest.uri().getPort() : "") +
                    (basePath != null && !basePath.isBlank() ? basePath : ""));

                return Mono.just(saveCourseDto)
                    .doOnNext(validationService::validate)
                    .flatMap(validatedDto -> serverRequest.principal()
                        .flatMap(principal -> courseService.createCourse(principal, saveCourseDto, baseUrl)
                            .flatMap(courseDto -> ServerResponse.ok().bodyValue(courseDto))
                            .switchIfEmpty(ServerResponse.notFound().build())
                        )
                    );
            });
    }
    
    public Mono<ServerResponse> updateCourse(ServerRequest serverRequest) {
        return serverRequest.multipartData()
            .flatMap(parts -> {
                FilePart image = (FilePart) parts.getFirst("image");
                SaveCourseDto saveCourseDto = new SaveCourseDto();

                saveCourseDto.setUrl(getFormFieldValue(parts, "url"));
                saveCourseDto.setTitle(getFormFieldValue(parts, "title"));
                saveCourseDto.setDescription(getFormFieldValue(parts, "description"));
                saveCourseDto.setModality(getFormFieldValue(parts, "modality"));
                saveCourseDto.setPrice(parseDoubleOrDefault(getFormFieldValue(parts, "price"), 0.0));
                saveCourseDto.setDuration(getFormFieldValue(parts, "duration"));
                saveCourseDto.setCategoryId(getFormFieldValue(parts, "categoryId"));
                saveCourseDto.setInstitutionId(getFormFieldValue(parts, "institutionId"));

                if (image != null && image.filename() != null && !image.filename().isBlank()) {
                    saveCourseDto.setImage(image);
    
                    if (!saveCourseDto.isValidImage()) {
                        Map<String, String> errors = new HashMap<>();
                        errors.put("image", "La imagen debe ser de tipo válido (jpg, png, jpeg) y menor a 2 MB.");
                        
                        return ServerResponse.badRequest().bodyValue(errors);
                    }
                }

                String baseUrl = serverRequest.uri().getScheme() + "://" + serverRequest.uri().getHost() + 
                    ((serverRequest.uri().getPort() != -1 ? ":" + serverRequest.uri().getPort() : "") +
                    (basePath != null && !basePath.isBlank() ? basePath : ""));

                return Mono.just(saveCourseDto)
                    .doOnNext(validationService::validate)
                    .flatMap(validatedDto -> serverRequest.principal()
                        .flatMap(principal -> courseService.updateCourse(principal, serverRequest.pathVariable("id"), validatedDto, baseUrl)
                            .flatMap(courseDto -> ServerResponse.ok().bodyValue(courseDto))
                            .switchIfEmpty(ServerResponse.notFound().build())
                        ));
            });
    }

    public Mono<ServerResponse> deleteCourse(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> courseService.deleteCourse(
                principal, 
                serverRequest.pathVariable("id")
            )
                .flatMap(c -> ServerResponse.ok().bodyValue(c))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }
    
    public Mono<ServerResponse> getTotalCoursesBySuscriptor(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> courseService.getTotalCoursesBySuscriptor(principal)
                .flatMap(courses -> ServerResponse.ok().bodyValue(courses))
                .switchIfEmpty(ServerResponse.notFound().build())
            );    
    }
    
    private String getFormFieldValue(MultiValueMap<String, Part> parts, String fieldName) {
        Part part = parts.getFirst(fieldName);
        if (part instanceof FormFieldPart) {
            return ((FormFieldPart) part).value();
        }
        return null;
    }
    
    private double parseDoubleOrDefault(String value, double defaultValue) {
        try {
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
