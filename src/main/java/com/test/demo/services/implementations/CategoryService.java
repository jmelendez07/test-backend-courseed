package com.test.demo.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.repositories.CategoryRepository;
import com.test.demo.projections.dtos.CategoryDto;
import com.test.demo.projections.mappers.CategoryMapper;
import com.test.demo.services.interfaces.InterfaceCategoryService;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Mono;

@Service
public class CategoryService implements InterfaceCategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Mono<Page<CategoryDto>> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        return categoryRepository.findAllBy(pageable)
            .map(categoryMapper::toCategoryDto)
            .collectList()
            .zipWith(categoryRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<CategoryDto> getCategoryById(String id) {
        return categoryRepository.findById(id)
            .map(categoryMapper::toCategoryDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "categoryId", 
                    "No hemos podido encontrar la categoría indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }

    @Override
    public Mono<CategoryDto> getCategoryByName(String name) {
        return categoryRepository.findByName(name)
            .map(categoryMapper::toCategoryDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    name, 
                    "name", 
                    "No hemos podido encontrar la categoría indicada por su nombre. Te sugerimos que verifiques y lo intentes nuevamente."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<Boolean> deleteCategory(String id) {
        return categoryRepository.findById(id)
            .flatMap(category -> 
                categoryRepository.deleteById(id)
                    .then(Mono.just(true))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "categoryId", 
                    "No hemos podido encontrar la categoría indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }
}
