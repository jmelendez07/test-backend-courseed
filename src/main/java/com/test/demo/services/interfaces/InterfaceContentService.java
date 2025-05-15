package com.test.demo.services.interfaces;

import com.test.demo.projections.dtos.ContentDto;
import com.test.demo.projections.dtos.SaveContentDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InterfaceContentService {
    public Flux<ContentDto> getContentsByCourseId(String courseId);
    public Mono<ContentDto> getContentById(String id);
    public Mono<ContentDto> createContent(SaveContentDto content);
    public Mono<ContentDto> updateContent(String id, SaveContentDto content);
    public Mono<Boolean> deleteContent(String id);
}
