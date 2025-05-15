package com.test.demo.services.implementations;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.documents.Reaction;
import com.test.demo.persistence.documents.Review;
import com.test.demo.persistence.documents.User;
import com.test.demo.persistence.repositories.ProfileRepository;
import com.test.demo.persistence.repositories.ReactionRepository;
import com.test.demo.persistence.repositories.ReviewRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.projections.dtos.CreateUserDto;
import com.test.demo.projections.dtos.TotalUsersDto;
import com.test.demo.projections.dtos.UpdateUserEmailDto;
import com.test.demo.projections.dtos.UpdateUserPasswordDto;
import com.test.demo.projections.dtos.UpdateUserRolesDto;
import com.test.demo.projections.dtos.UserCountByMonth;
import com.test.demo.projections.dtos.UserDto;
import com.test.demo.projections.mappers.UserMapper;
import com.test.demo.services.interfaces.InterfaceUserService;
import com.test.demo.services.interfaces.Roles;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService implements InterfaceUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired    
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CourseService courseService;

    @Override
    public Mono<TotalUsersDto> getTotalUsers() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return userRepository.count()
            .flatMap(total -> userRepository.countByCreatedAtBetween(startOfMonth, endOfMonth)
                .map(lastMonth -> new TotalUsersDto(total, lastMonth))
            );
    }

    @Override
    public Mono<Integer> getAllUsersCountByInterestOrModality(String interest, String modality) {
        return userRepository.findAll()
            .flatMap(user -> profileRepository.findByUserId(user.getId())
                .flatMap(profile -> {
                    String interestStandarized = categoryService.standarizeCategory(interest);
                    String modalityStandarized = courseService.standarizeModality(modality);
                    String profileInterestStandarized = categoryService.standarizeCategory(profile.getInterest());
                    String profileModalityStandarized = courseService.standarizeModality(profile.getPlatformPreference());

                    if (interestStandarized.equals(profileInterestStandarized) || modalityStandarized.equals(profileModalityStandarized)) {
                        return Mono.just(user.getId());
                    } else {
                        return Mono.empty();
                    }
                })
            )
            .collectList()
            .map(List::size);
    }

    @Override
    public Mono<Page<UserDto>> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAllBy(pageable)
            .flatMap(user -> {
                Flux<Reaction> reactionFlux = reactionRepository.findByUserId(user.getId());
                Flux<Review> reviewFlux = reviewRepository.findByUserId(user.getId());

                return Mono.zip(reactionFlux.collectList(), reviewFlux.collectList())
                    .map(tuple -> {
                        UserDto userDto = userMapper.toUserDto(user);
                        userDto.setReactions(tuple.getT1().size());
                        userDto.setReviews(tuple.getT2().size());
                        return userDto;
                    });
            })
            .collectList()
            .zipWith(userRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<Integer> getAllUsersCount() {
        return userRepository.count()
            .map(Long::intValue);
    }

    @Override
    public Mono<UserDto> getUserById(String id) {
        return userRepository.findById(id)
            .map(userMapper::toUserDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "userId", 
                    "No hemos podido encontrar al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(userMapper::toUserDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    email, 
                    "email", 
                    "No hemos podido encontrar al usuario indicado por su email. Te sugerimos que verifiques y lo intentes nuevamente."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<List<UserCountByMonth>> getUserCountForLastSixMonths() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.with(LocalTime.MAX);
        LocalDateTime startDate = endDate.minusMonths(6);

        return userRepository.findByCreatedAtBetween(startDate, endDate)
            .collectList()
            .flatMapMany(users -> {
                List<String> months = Arrays.asList(
                    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
                );

                Map<String, Long> monthCounts = new LinkedHashMap<>();
                for (int i = 0; i < 6; i++) {
                    LocalDateTime monthStart = startDate.plusMonths(i + 1);
                    String monthName = months.get(monthStart.getMonthValue() - 1);
                    int yearForMonth = monthStart.getYear();
                    String key = monthName + "-" + yearForMonth;

                    monthCounts.put(key, 0L);
                }

                users.forEach(user -> {
                    LocalDateTime createdAt = user.getCreatedAt();
                    String monthName = months.get(createdAt.getMonthValue() - 1);
                    int yearForMonth = createdAt.getYear();
                    String key = monthName + "-" + yearForMonth;
                    monthCounts.put(key, monthCounts.getOrDefault(key, 0L) + 1);
                });

                return Flux.fromIterable(monthCounts.entrySet())
                    .map(entry -> {
                        String[] keyParts = entry.getKey().split("-");
                        String monthName = keyParts[0];
                        int yearForMonth = Integer.parseInt(keyParts[1]);
                        return new UserCountByMonth(yearForMonth, monthName, entry.getValue());
                    });
            })
            .collectList();
    }

    @Override
    public Mono<Object> createUser(CreateUserDto createUserDto) {
        if (!createUserDto.getPassword().equals(createUserDto.getConfirmPassword())) {
            return Mono.error(new CustomWebExchangeBindException(
                createUserDto, 
                "confirmPassword", 
                "La confirmación de la contraseña no coincide con la contraseña original. Por favor, revísalo e inténtalo de nuevo."
            ).getWebExchangeBindException());
        }

        List<String> roles = createUserDto.getRoles().stream()
            .filter(role -> role.equals(Roles.PREFIX + Roles.ADMIN) || role.equals(Roles.PREFIX + Roles.USER))
            .toList();

        if (roles.isEmpty()) {
            return Mono.error(
                new CustomWebExchangeBindException(
                    createUserDto.getRoles(), 
                    "roles", 
                    "No se pueden asignar roles que no están registrados a este usuario. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            );
        }

        return userRepository.findByEmail(createUserDto.getEmail())
            .flatMap(existingUser -> Mono.error(
                new CustomWebExchangeBindException(
                    createUserDto.getEmail(), 
                    "email", 
                    "El email que intentas registrar ya está asociado a otra cuenta. Por favor, intenta con un correo electrónico diferente."
                ).getWebExchangeBindException()
            ))
            .switchIfEmpty(Mono.defer(() -> {
                User user = userMapper.toUser(createUserDto);
                user.setPassword(passwordEncoder.encode(user.getPassword()));

                return userRepository.save(user)
                    .map(savedUser -> userMapper.toUserDto(savedUser));
            }));
    }

    @Override
    public Mono<Object> updateUserEmail(String id, UpdateUserEmailDto updateUserEmailDto) {
        return userRepository.findById(id)
            .flatMap(user -> userRepository.findByEmailAndIdNot(updateUserEmailDto.getEmail(), id)
                .flatMap(existingUserWithEmail -> Mono.error(
                    new CustomWebExchangeBindException(
                        updateUserEmailDto.getEmail(), 
                        "email", 
                        "El email que intentas registrar ya está asociado a otra cuenta. Por favor, intenta con un correo electrónico diferente."
                    ).getWebExchangeBindException()
                ))
                .switchIfEmpty(Mono.defer(() -> {
                    user.setEmail(updateUserEmailDto.getEmail());

                    return userRepository.save(user)
                        .map(userMapper::toUserDto);
                }))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "userId", 
                    "No hemos podido encontrar al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<UserDto> updateUserPassword(String id, UpdateUserPasswordDto updateUserPasswordDto) {
        return userRepository.findById(id)
            .flatMap(user -> {
                user.setPassword(passwordEncoder.encode(updateUserPasswordDto.getPassword()));
                return userRepository.save(user)
                    .map(userMapper::toUserDto);
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "userId", 
                    "No hemos podido encontrar al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<UserDto> updateUserRoles(String id, UpdateUserRolesDto updateUserRolesDto) {
        return userRepository.findById(id)
            .flatMap(user -> {
                List<String> newRoles = updateUserRolesDto.getRoles().stream()
                    .filter(role -> role.equals(Roles.PREFIX + Roles.ADMIN) || role.equals(Roles.PREFIX + Roles.USER) || role.equals(Roles.PREFIX + Roles.SUBSCRIBER))
                    .toList();

                if (!newRoles.isEmpty()) {
                    user.setRoles(newRoles);
                    return userRepository.save(user)
                        .map(userMapper::toUserDto);
                } else {
                    return Mono.error(
                        new CustomWebExchangeBindException(
                            updateUserRolesDto.getRoles(), 
                            "roles", 
                            "No se pueden asignar roles que no están registrados a este usuario. Te sugerimos que verifiques la información y lo intentes de nuevo."
                        ).getWebExchangeBindException()
                    );
                }
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "userId", 
                    "No hemos podido encontrar al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<Boolean> deleteUser(String id) {
        return userRepository.findById(id)
            .flatMap(user -> 
                userRepository.deleteById(id)
                    .then(Mono.just(true))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "userId", 
                    "No hemos podido encontrar al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }
    
}
