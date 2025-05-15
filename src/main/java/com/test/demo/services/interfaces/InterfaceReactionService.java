package com.test.demo.services.interfaces;

import java.security.Principal;

import org.springframework.data.domain.Page;

import com.test.demo.projections.dtos.ReactionDto;
import com.test.demo.projections.dtos.SaveReactionDto;
import com.test.demo.projections.dtos.TotalReactionsDto;

import reactor.core.publisher.Mono;

public interface InterfaceReactionService {
    Mono<TotalReactionsDto> getTotalReactions();
    Mono<Page<ReactionDto>> findReactionsByCourseId(String courseId, int page, int size);
    Mono<Page<ReactionDto>> findReactionsByAuthUser(Principal principal, String type, int page, int size);
    Mono<Object> createReaction(Principal principal, SaveReactionDto saveReactionDto);
    Mono<ReactionDto> updateReaction(Principal principal, SaveReactionDto saveReactionDto); 
    Mono<Boolean> deleteReaction(Principal principal, String courseId);
    Mono<Integer> getTotalReactionsBySuscriptor(Principal principal);
}
