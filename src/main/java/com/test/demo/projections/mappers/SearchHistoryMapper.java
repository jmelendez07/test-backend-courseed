package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.SearchHistory;
import com.test.demo.projections.dtos.SaveSearchHistoryDto;
import com.test.demo.projections.dtos.SearchHistoryDto;

@Mapper(componentModel = "spring")
public interface SearchHistoryMapper {
    @Mapping(source = "userId", target = "user", ignore = true)
    @Mapping(target = "courses", ignore = true)
    SearchHistoryDto toSearchHistoryDto(SearchHistory searchHistory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SearchHistory toSearchHistory(SaveSearchHistoryDto saveSearchHistoryDto);
}
