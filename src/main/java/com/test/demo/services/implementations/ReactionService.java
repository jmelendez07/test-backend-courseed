package com.test.demo.services.implementations;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.documents.Category;
import com.test.demo.persistence.documents.Institution;
import com.test.demo.persistence.documents.Reaction;
import com.test.demo.persistence.documents.Review;
import com.test.demo.persistence.repositories.CategoryRepository;
import com.test.demo.persistence.repositories.CourseRepository;
import com.test.demo.persistence.repositories.InstitutionRepository;
import com.test.demo.persistence.repositories.ReactionRepository;
import com.test.demo.persistence.repositories.ReviewRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.projections.dtos.CourseDto;
import com.test.demo.projections.dtos.ReactionDto;
import com.test.demo.projections.dtos.SaveReactionDto;
import com.test.demo.projections.dtos.TotalReactionsDto;
import com.test.demo.projections.mappers.CategoryMapper;
import com.test.demo.projections.mappers.CourseMapper;
import com.test.demo.projections.mappers.InstitutionMapper;
import com.test.demo.projections.mappers.ReactionMapper;
import com.test.demo.projections.mappers.ReviewMapper;
import com.test.demo.projections.mappers.UserMapper;
import com.test.demo.services.interfaces.InterfaceReactionService;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReactionService implements InterfaceReactionService {

    @Autowired
    private ReactionRepository reactionRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReactionMapper reactionMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Mono<Page<ReactionDto>> findReactionsByCourseId(String courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return reactionRepository.findByCourseId(courseId, pageable)
            .map(reactionMapper::toReactionDto)
            .collectList()
            .zipWith(reactionRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<Object> createReaction(Principal principal, SaveReactionDto saveReactionDto) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> courseRepository.findById(saveReactionDto.getCourseId())
                .flatMap(course -> reactionRepository.findByCourseIdAndUserId(user.getId(), course.getId())
                    .flatMap(reaction -> Mono.error(
                        new CustomWebExchangeBindException(
                            saveReactionDto.getCourseId(), 
                            "courseId",
                            "¡Genial que reacciones al programa! Ten en cuenta que solo se permite una reacción por usuario."
                        ).getWebExchangeBindException()
                    ))
                    .switchIfEmpty(Mono.defer(() -> {
                        Reaction reaction = reactionMapper.toReaction(saveReactionDto);
                        reaction.setUserId(user.getId());
                        return reactionRepository.save(reaction)
                            .flatMap(createdReaction -> {
                                ReactionDto reactionDto = reactionMapper.toReactionDto(reaction);
                                reactionDto.setCourse(courseMapper.toCourseDto(course));
                                reactionDto.setUser(userMapper.toUserDto(user));

                                return Mono.just(reactionDto);
                            });
                    }))
                )
                .switchIfEmpty(Mono.error(
                    new CustomWebExchangeBindException(
                        saveReactionDto.getCourseId(), 
                        "courseId", 
                        "No hemos podido encontrar el curso indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                    ).getWebExchangeBindException())
                )
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "auth", 
                    "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<ReactionDto> updateReaction(Principal principal, SaveReactionDto saveReactionDto) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> courseRepository.findById(saveReactionDto.getCourseId())
                .flatMap(course -> reactionRepository.findByCourseIdAndUserId(course.getId(), user.getId())
                    .flatMap(reaction -> {
                        reaction.setType(saveReactionDto.getType());
                        return reactionRepository.save(reaction)
                            .map(reactionMapper::toReactionDto)
                            .map(updatedReaction -> {
                                updatedReaction.setCourse(courseMapper.toCourseDto(course));
                                updatedReaction.setUser(userMapper.toUserDto(user));
                                return updatedReaction;
                            });
                    })
                    .switchIfEmpty(Mono.error(
                        new CustomWebExchangeBindException(
                            saveReactionDto.getCourseId(), 
                            "courseId",
                            "No hemos podido encontrar la reacción indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                        ).getWebExchangeBindException()
                    ))
                )
                .switchIfEmpty(Mono.error(
                    new CustomWebExchangeBindException(
                        saveReactionDto.getCourseId(), 
                        "courseId", 
                        "No hemos podido encontrar el curso indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                    ).getWebExchangeBindException())
                )
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "auth", 
                    "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<Boolean> deleteReaction(Principal principal, String reactionId) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> reactionRepository.findById(reactionId)
                .flatMap(reaction -> {
                    if (reaction.getUserId().equals(user.getId())) {
                        return reactionRepository.deleteById(reaction.getId())
                            .then(Mono.just(true));
                    } else {
                        return Mono.error(
                            new CustomWebExchangeBindException(
                                principal.getName(), 
                                "auth", 
                                "No tiene la autorización necesaria para eliminar una reaccion que corresponde a otro usuario."
                            ).getWebExchangeBindException()
                        );
                    }
                })
                .switchIfEmpty(Mono.error(
                    new CustomWebExchangeBindException(
                        reactionId, 
                        "reactionId", 
                        "No hemos podido encontrar la reacción indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                    ).getWebExchangeBindException()
                ))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "auth", 
                    "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<Page<ReactionDto>> findReactionsByAuthUser(Principal principal, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> reactionRepository.findByUserIdAndTypeContaining(user.getId(), type, pageable)
                .flatMap(reaction -> {
                    Mono<CourseDto> courseMono = courseRepository.findById(reaction.getCourseId())
                        .flatMap(course -> {
                            Mono<Category> categoryMono = categoryRepository.findById(course.getCategoryId());
                            Mono<Institution> institutionMono = institutionRepository.findById(course.getInstitutionId());
                            Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());

                            return Mono.zip(categoryMono, institutionMono, reviewFlux.collectList())
                                .map(tuple -> {
                                    CourseDto courseDto = courseMapper.toCourseDto(course);
                                    courseDto.setCategory(categoryMapper.toCategoryDto(tuple.getT1()));
                                    courseDto.setInstitution(institutionMapper.toInstitutionDto(tuple.getT2()));
                                    courseDto.setReviews(tuple.getT3().stream()
                                        .map(reviewMapper::toReviewDto)
                                        .toList()
                                    );

                                    return courseDto;
                                });
                        });

                    return courseMono.map(courseDto -> {
                            ReactionDto reactionDto = reactionMapper.toReactionDto(reaction);
                            reactionDto.setCourse(courseDto);
                            return reactionDto;
                        });
                })
                .collectList()
                .zipWith(reactionRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2())));
    }

    @Override
    public Mono<TotalReactionsDto> getTotalReactions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return reactionRepository.count()
            .flatMap(total -> reactionRepository.countByCreatedAtBetween(startOfMonth, endOfMonth)
                .map(lastMonth -> new TotalReactionsDto(total, lastMonth))
            );
    }
  
    @Override
    public Mono<Integer> getTotalReactionsBySuscriptor(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> courseRepository.findByUserId(user.getId())
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .flatMap(course -> reactionRepository.findByCourseId(course.getId()))
                .count()
                .map(Long::intValue)
            );
    }
}
