package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.View;
import com.test.demo.projections.dtos.SaveViewDto;
import com.test.demo.projections.dtos.ViewDto;

@Mapper(componentModel = "spring")
public interface ViewMapper {

    @Mapping(source = "userId", target = "user", ignore = true)
    @Mapping(source = "courseId", target = "course", ignore = true)
    ViewDto toViewDto(View view);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    View toView(SaveViewDto saveViewDto);
}
