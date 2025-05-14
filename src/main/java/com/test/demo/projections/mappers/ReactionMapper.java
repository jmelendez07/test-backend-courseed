package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.Reaction;
import com.test.demo.projections.dtos.ReactionDto;
import com.test.demo.projections.dtos.SaveReactionDto;

@Mapper(componentModel = "spring")
public interface ReactionMapper {

    @Mapping(source = "userId", target = "user", ignore = true)
    @Mapping(source = "courseId", target = "course", ignore = true)
    ReactionDto toReactionDto(Reaction reaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Reaction toReaction(SaveReactionDto saveReactionDto);
}
