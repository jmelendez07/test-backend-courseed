package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.Content;
import com.test.demo.projections.dtos.ContentDto;
import com.test.demo.projections.dtos.SaveContentDto;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    ContentDto toContentDto(Content content);

    @Mapping(target = "id", ignore = true)
    Content toContent(SaveContentDto saveContentDto);
}
