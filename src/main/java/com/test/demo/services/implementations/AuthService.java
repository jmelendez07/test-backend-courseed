package com.test.demo.services.implementations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.test.demo.persistence.documents.Reaction;
import com.test.demo.persistence.documents.Review;
import com.test.demo.persistence.documents.User;
import com.test.demo.persistence.documents.View;
import com.test.demo.persistence.repositories.ProfileRepository;
import com.test.demo.persistence.repositories.ReactionRepository;
import com.test.demo.persistence.repositories.ReviewRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.persistence.repositories.ViewRepository;
import com.test.demo.projections.dtos.LoginUserDto;
import com.test.demo.projections.dtos.ProfileDto;
import com.test.demo.projections.dtos.RegisterSubscriptorDto;
import com.test.demo.projections.dtos.RegisterUserDto;
import com.test.demo.projections.dtos.TokenDto;
import com.test.demo.projections.dtos.UpdateAuthPasswordDto;
import com.test.demo.projections.dtos.UpdateProfileDto;
import com.test.demo.projections.dtos.UserDto;
import com.test.demo.projections.mappers.ProfileMapper;
import com.test.demo.projections.mappers.UserMapper;
import com.test.demo.services.interfaces.InterfaceAuthService;
import com.test.demo.services.interfaces.Roles;
import com.test.demo.web.config.JwtUtil;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AuthService implements InterfaceAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired    
    private PasswordEncoder passwordEncoder;

    private String uploadPath = "uploads/avatars";

    @Override
    public Mono<UserDto> getAuthUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> {
                Flux<Review> reviewFlux = reviewRepository.findByUserId(user.getId());
                Flux<Reaction> reactionFlux = reactionRepository.findByUserId(user.getId());
                Flux<View> viewFlux = viewRepository.findByUserId(user.getId());
                Mono<ProfileDto> profileMono = profileRepository.findByUserId(user.getId())
                    .map(profileMapper::toProfileDto)
                    .defaultIfEmpty(new ProfileDto());

                return Mono.zip(reviewFlux.collectList(), reactionFlux.collectList(), viewFlux.collectList(), profileMono)
                    .map(tuple -> {
                        UserDto userDto = userMapper.toUserDto(user);
                        userDto.setReviews(tuple.getT1().size());
                        userDto.setReactions(tuple.getT2().size());
                        userDto.setViews(tuple.getT3().size());
                        userDto.setProfile(tuple.getT4());

                        return userDto;
                    });
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "auth", 
                    "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<TokenDto> login(LoginUserDto loginUserDto) {
        return userRepository.findByEmail(loginUserDto.getEmail())
            .filter(user -> passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword()))
            .flatMap(user -> {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), 
                    user.getPassword(), 
                    user.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
                );
                return reactiveAuthenticationManager.authenticate(authenticationToken)
                    .map(auth -> new TokenDto(jwtUtil.create(auth)));
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    loginUserDto, 
                    "auth", 
                    "El correo electrónico o la contraseña proporcionados son incorrectos. Por favor, verifica tus credenciales e intenta nuevamente."
                ).getWebExchangeBindException())
            ); 
            
    }

    @Override
    public Mono<Object> register(RegisterUserDto registerUserDto) {
        if (!registerUserDto.getPassword().equals(registerUserDto.getConfirmPassword())) {
            return Mono.error(new CustomWebExchangeBindException(
                registerUserDto, 
                "confirmPassword", 
                "La confirmación de la contraseña no coincide con la contraseña original. Por favor, revísalo e inténtalo de nuevo."
            ).getWebExchangeBindException());
        }

        return userRepository.findByEmail(registerUserDto.getEmail())
            .flatMap(existingUser -> Mono.error(
                new CustomWebExchangeBindException(
                    registerUserDto.getEmail(), 
                    "email", 
                    "El email que intentas registrar ya está asociado a otra cuenta. Por favor, intenta con un correo electrónico diferente."
                ).getWebExchangeBindException()
            ))
            .switchIfEmpty(Mono.defer(() -> {
                User user = userMapper.toUser(registerUserDto);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRoles(Arrays.asList(Roles.PREFIX + Roles.USER));

                return userRepository.save(user)
                    .map(savedUser -> userMapper.toUserDto(savedUser));

            }));
    }

    @Override
    public Mono<TokenDto> updatePassword(Principal principal, UpdateAuthPasswordDto updateAuthPasswordDto) {
        return userRepository.findByEmail(principal.getName())
            .filter(user -> passwordEncoder.matches(updateAuthPasswordDto.getCurrentPassword(), user.getPassword()))
            .flatMap(user -> {
                if (!updateAuthPasswordDto.getNewPassword().equals(updateAuthPasswordDto.getConfirmNewPassword())) {
                    return Mono.error(new CustomWebExchangeBindException(
                        updateAuthPasswordDto.getConfirmNewPassword(), 
                        "confirmNewPassword", 
                        "La confirmación de la contraseña no coincide con la contraseña nueva. Por favor, revísalo e inténtalo de nuevo."
                    ).getWebExchangeBindException());   
                }

                user.setPassword(passwordEncoder.encode(updateAuthPasswordDto.getNewPassword()));
                return userRepository.save(user)
                    .flatMap(savedUser -> {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            savedUser.getEmail(), 
                            savedUser.getPassword(), 
                            savedUser.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
                        );
                        return reactiveAuthenticationManager.authenticate(authenticationToken)
                        .map(auth -> new TokenDto(jwtUtil.create(auth)));
                    });
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    updateAuthPasswordDto,
                    "auth", 
                    "El correo electrónico o la contraseña proporcionados son incorrectos. Por favor, verifica tus credenciales e intenta nuevamente."
                ).getWebExchangeBindException())
            );
    }

    public Mono<UserDto> updateProfile(Principal principal, UpdateProfileDto updateProfileDto) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> {
                user.setAcademicLevel(updateProfileDto.getAcademicLevel());
                user.setSex(updateProfileDto.getSex());
                user.setBirthdate(updateProfileDto.getBirthdate());

                return userRepository.save(user)
                    .map(userMapper::toUserDto);
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    updateProfileDto,
                    "auth", 
                    "El correo electrónico o la contraseña proporcionados son incorrectos. Por favor, verifica tus credenciales e intenta nuevamente."
                ).getWebExchangeBindException())
            );
    }

    @Override
    public Mono<TokenDto> subscribe(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> {
                user.setRoles(Arrays.asList(Roles.PREFIX + Roles.SUBSCRIBER));

                return userRepository.save(user)
                    .flatMap(savedUser -> {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            savedUser.getEmail(), 
                            savedUser.getPassword(), 
                            savedUser.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
                        );
                        return reactiveAuthenticationManager.authenticate(authenticationToken)
                        .map(auth -> new TokenDto(jwtUtil.create(auth)));
                    });
            });
    }

    @Override
    public Mono<Object> registerSubscriptor(RegisterSubscriptorDto registerSubscriptorDto) {
        if (!registerSubscriptorDto.getPassword().equals(registerSubscriptorDto.getConfirmPassword())) {
            return Mono.error(new CustomWebExchangeBindException(
                registerSubscriptorDto, 
                "confirmPassword", 
                "La confirmación de la contraseña no coincide con la contraseña original."
            ).getWebExchangeBindException());
        }

        return userRepository.findByEmail(registerSubscriptorDto.getEmail())
            .flatMap(existingUser -> Mono.error(
                new CustomWebExchangeBindException(
                    registerSubscriptorDto.getEmail(), 
                    "email", 
                    "El email que intentas registrar ya está asociado a otra cuenta."
                ).getWebExchangeBindException()
            ))
            .switchIfEmpty(Mono.defer(() -> {
                User user = userMapper.toUser(registerSubscriptorDto);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRoles(Arrays.asList(Roles.PREFIX + Roles.SUBSCRIBER));

                return userRepository.save(user)
                    .flatMap(savedUser -> {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            savedUser.getEmail(), 
                            savedUser.getPassword(), 
                            savedUser.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
                        );

                        return reactiveAuthenticationManager.authenticate(authenticationToken)
                            .map(auth -> new TokenDto(jwtUtil.create(auth)));
                    });
            }));
    }

    @Override
    public Mono<UserDto> updloadAvatar(Principal principal, FilePart image, String baseUrl) {
       return userRepository.findByEmail(principal.getName())
            .flatMap(user -> {
                String filename = UUID.randomUUID() + "-" + image.filename();
                Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
                Path filePath = uploadDir.resolve(filename);

                if (user.getImage() != null) {
                    Path oldFilePath = Paths.get(uploadDir.toString(), user.getImage().substring(user.getImage().lastIndexOf("/") + 1));
                    try {
                        Files.deleteIfExists(oldFilePath);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Error al eliminar la imagen antigua: " + e.getMessage()));
                    }
                }

                return Mono.fromCallable(() -> {
                    Files.createDirectories(uploadDir);
                    return filePath;
                })
                    .flatMap(path -> image.transferTo(path))
                    .then(Mono.defer(() -> {
                        user.setImage(baseUrl + "/" + uploadPath + "/" + filename);
                        return userRepository.save(user)
                            .map(userMapper::toUserDto);
                    }));
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "auth", 
                    "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<TokenDto> getToken(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), 
                    user.getPassword(), 
                    user.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
                );
                System.out.println("Token: " + authenticationToken);
                return reactiveAuthenticationManager.authenticate(authenticationToken)
                    .map(auth -> new TokenDto(jwtUtil.create(auth)));
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal, 
                    "auth", 
                    "El correo electrónico o la contraseña proporcionados son incorrectos. Por favor, verifica tus credenciales e intenta nuevamente."
                ).getWebExchangeBindException())
            );
    }
}
