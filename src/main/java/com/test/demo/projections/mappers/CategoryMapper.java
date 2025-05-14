package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.Category;
import com.test.demo.projections.dtos.CategoryDto;
import com.test.demo.projections.dtos.SaveCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toCategory(SaveCategoryDto saveCategoryDto);
}
