package com.test.demo.services.interfaces;

import java.security.Principal;

import org.springframework.data.domain.Page;

import com.test.demo.projections.dtos.LikeDto;
import com.test.demo.projections.dtos.SaveLikeDto;
import com.test.demo.projections.dtos.TotalLikesDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InterfaceLikeService {
    Mono<TotalLikesDto> getTotalLikes();
    Flux<LikeDto> getLikesByCourseId(String courseId);
    Mono<Page<LikeDto>> getLikesByAuthUser(Principal principal, int size, int page);
    Mono<Object> createLike(Principal principal, SaveLikeDto saveLikeDto);
    Mono<Boolean> deleteLike(Principal principal, String id);
}
