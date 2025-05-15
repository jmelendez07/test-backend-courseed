package com.test.demo.services.implementations;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.documents.Profile;
import com.test.demo.persistence.repositories.ProfileRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.projections.dtos.ProfileDto;
import com.test.demo.projections.dtos.SaveProfileDto;
import com.test.demo.projections.mappers.ProfileMapper;
import com.test.demo.services.interfaces.InterfaceProfileService;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Mono;

@Service
public class ProfileService implements InterfaceProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public Mono<ProfileDto> getProfileById(String id) {
        return profileRepository.findById(id)
            .map(profileMapper::toProfileDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "id", 
                    "No hemos podido encontrar el perfil indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<ProfileDto> getProfileByAuth(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> profileRepository.findByUserId(user.getId()))
            .map(profileMapper::toProfileDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "auth", 
                    "No hemos podido encontrar al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<ProfileDto> createProfile(Principal principal, SaveProfileDto saveProfileDto) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> {
                Profile profile = profileMapper.toProfile(saveProfileDto);
                profile.setUserId(user.getId());

                return profileRepository.save(profile)
                    .map(profileMapper::toProfileDto);
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "auth", 
                    "No hemos podido encontrar al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }

    @Override 
    public Mono<ProfileDto> updateProfile(Principal principal, SaveProfileDto saveProfileDto) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> profileRepository.findByUserId(user.getId())
                .flatMap(profile -> {
                    profile.setKnowledgeLevel(saveProfileDto.getKnowledgeLevel());
                    profile.setAvailableHoursTime(saveProfileDto.getAvailableHoursTime());
                    profile.setPlatformPrefered(saveProfileDto.getPlatformPrefered());
                    profile.setBudget(saveProfileDto.getBudget());
                    profile.setInterest(saveProfileDto.getInterest());

                    return profileRepository.save(profile)
                        .map(profileMapper::toProfileDto);
                })
                .switchIfEmpty(Mono.error(
                    new CustomWebExchangeBindException(
                        principal.getName(), 
                        "profile", 
                        "No hemos podido encontrar al perfil indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                    ).getWebExchangeBindException()
                ))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "auth", 
                    "No hemos podido encontrar al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }
    
}
