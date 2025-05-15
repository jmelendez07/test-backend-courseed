package com.test.demo.services.interfaces;

import java.security.Principal;

import com.test.demo.projections.dtos.ProfileDto;
import com.test.demo.projections.dtos.SaveProfileDto;

import reactor.core.publisher.Mono;

public interface InterfaceProfileService {
    Mono<ProfileDto> getProfileById(String id);
    Mono<ProfileDto> getProfileByAuth(Principal principal);
    Mono<ProfileDto> createProfile(Principal principal, SaveProfileDto saveProfileDto);
    Mono<ProfileDto> updateProfile(Principal principal, SaveProfileDto saveProfileDto);
}
