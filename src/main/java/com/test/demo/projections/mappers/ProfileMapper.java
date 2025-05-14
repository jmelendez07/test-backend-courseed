package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.Profile;
import com.test.demo.projections.dtos.ProfileDto;
import com.test.demo.projections.dtos.SaveProfileDto;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileDto toProfileDto(Profile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "platformPreference", ignore = true)
    Profile toProfile(SaveProfileDto saveProfileDto);
}
