package com.test.demo.services.interfaces;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.test.demo.projections.dtos.SaveSearchHistoryDto;
import com.test.demo.projections.dtos.SearchHistoryDto;

import reactor.core.publisher.Mono;

public interface InterfaceSearchHistoryService {
    Mono<Page<SearchHistoryDto>> findByAuthUser(Principal principal, String search, int page, int size);
    Mono<SearchHistoryDto> createSearchHistory(Principal principal, SaveSearchHistoryDto saveSearchHistoryDto);
    Mono<Boolean> deleteSearchHistory(Principal principal, String id);
    Mono<Boolean> deleteSearchHistories(Principal principal, List<String> ids);
}
