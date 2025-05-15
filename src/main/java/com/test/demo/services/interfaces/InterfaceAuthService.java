package com.test.demo.services.interfaces;

import java.security.Principal;

import org.springframework.http.codec.multipart.FilePart;

import com.test.demo.projections.dtos.LoginUserDto;
import com.test.demo.projections.dtos.RegisterSubscriptorDto;
import com.test.demo.projections.dtos.RegisterUserDto;
import com.test.demo.projections.dtos.TokenDto;
import com.test.demo.projections.dtos.UpdateAuthPasswordDto;
import com.test.demo.projections.dtos.UpdateProfileDto;
import com.test.demo.projections.dtos.UserDto;

import reactor.core.publisher.Mono;

public interface InterfaceAuthService {
    // Mono<UserDto> getAuthUser(Principal principal);
    Mono<TokenDto> login(LoginUserDto loginUserDto);
    // Mono<Object> register(RegisterUserDto registerUserDto);
    // Mono<Object> registerSubscriptor(RegisterSubscriptorDto registerSubscriptorDto);
    Mono<TokenDto> subscribe(Principal principal);
    Mono<TokenDto> updatePassword(Principal principal, UpdateAuthPasswordDto updateAuthPasswordDto);
    // Mono<UserDto> updateProfile(Principal principal, UpdateProfileDto updateProfiledto);
    // Mono<UserDto> updloadAvatar(Principal principal, FilePart file, String baseUrl);
}
