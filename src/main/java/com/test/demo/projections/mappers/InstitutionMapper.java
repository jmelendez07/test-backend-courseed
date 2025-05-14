package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.Institution;
import com.test.demo.projections.dtos.InstitutionDto;
import com.test.demo.projections.dtos.SaveInstitutionDto;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {

    @Mapping(target = "user", ignore = true)
    InstitutionDto toInstitutionDto(Institution institution);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Institution toInstitution(SaveInstitutionDto saveInstitutionDto);
}
