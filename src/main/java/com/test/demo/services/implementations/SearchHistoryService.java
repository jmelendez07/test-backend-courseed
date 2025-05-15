package com.test.demo.services.implementations;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.documents.SearchHistory;
import com.test.demo.persistence.repositories.SearchHistoryRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.projections.dtos.SaveSearchHistoryDto;
import com.test.demo.projections.dtos.SearchHistoryDto;
import com.test.demo.projections.mappers.SearchHistoryMapper;
import com.test.demo.projections.mappers.UserMapper;
import com.test.demo.services.interfaces.InterfaceSearchHistoryService;

import reactor.core.publisher.Mono;

@Service
public class SearchHistoryService implements InterfaceSearchHistoryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private SearchHistoryMapper searchHistoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseService courseService;

    @Override
    public Mono<Page<SearchHistoryDto>> findByAuthUser(Principal principal, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> searchHistoryRepository.findByUserIdAndSearchContainingIgnoreCaseOrderByCreatedAtDesc(user.getId(), search, pageable)
                .flatMapSequential(searchHistory -> {
                    SearchHistoryDto searchHistoryDto = searchHistoryMapper.toSearchHistoryDto(searchHistory);
                    searchHistoryDto.setUser(userMapper.toUserDto(user));
                    
                    return courseService.getAllCourses(searchHistory.getSearch(), null, null, 0, 4)
                        .map(coursePage -> {
                            searchHistoryDto.setCourses(coursePage.getContent());
                            return searchHistoryDto;
                        });
                })
                .collectList()
                .zipWith(searchHistoryRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()))
            );
    }

    @Override
    public Mono<SearchHistoryDto> createSearchHistory(Principal principal, SaveSearchHistoryDto saveSearchHistoryDto) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> {
                SearchHistory searchHistory = searchHistoryMapper.toSearchHistory(saveSearchHistoryDto);
                searchHistory.setUserId(user.getId());

                return searchHistoryRepository.save(searchHistory)
                    .map(savedSearchHistory -> {
                        SearchHistoryDto searchHistoryDto = searchHistoryMapper.toSearchHistoryDto(savedSearchHistory);
                        searchHistoryDto.setUser(userMapper.toUserDto(user));

                        return searchHistoryDto;
                    });
            });
    }

    @Override
    public Mono<Boolean> deleteSearchHistory(Principal principal, String id) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> searchHistoryRepository.findByIdAndUserId(id, user.getId())
                .flatMap(searchHistory -> searchHistoryRepository.delete(searchHistory).thenReturn(true))
                .defaultIfEmpty(false)
            );
    }

    @Override
    public Mono<Boolean> deleteSearchHistories(Principal principal, List<String> ids) {
        return userRepository.findByEmail(principal.getName())
        .flatMap(user -> searchHistoryRepository.findAllById(ids)
            .filter(searchHistory -> searchHistory.getUserId().equals(user.getId()))
            .collectList()
            .flatMap(searchHistories -> {
                if (searchHistories.size() != ids.size()) {
                    return Mono.just(false);
                }
                return searchHistoryRepository.deleteAll(searchHistories)
                    .thenReturn(true);
            })
        )
        .defaultIfEmpty(false);
    }
}
