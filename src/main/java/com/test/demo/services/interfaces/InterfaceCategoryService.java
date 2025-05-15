package com.test.demo.services.interfaces;

import org.springframework.data.domain.Page;

import com.test.demo.projections.dtos.CategoryDto;

import reactor.core.publisher.Mono;

public interface InterfaceCategoryService {
    Mono<Page<CategoryDto>> getAllCategories(int page, int size);
    Mono<CategoryDto> getCategoryById(String id);
    Mono<CategoryDto> getCategoryByName(String name);
    Mono<Boolean> deleteCategory(String id);
}
