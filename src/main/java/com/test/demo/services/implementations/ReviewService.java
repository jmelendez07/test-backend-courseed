package com.test.demo.services.implementations;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.documents.Category;
import com.test.demo.persistence.documents.Content;
import com.test.demo.persistence.documents.Institution;
import com.test.demo.persistence.documents.Reaction;
import com.test.demo.persistence.documents.Review;
import com.test.demo.persistence.documents.User;
import com.test.demo.persistence.repositories.CategoryRepository;
import com.test.demo.persistence.repositories.ContentRepository;
import com.test.demo.persistence.repositories.CourseRepository;
import com.test.demo.persistence.repositories.InstitutionRepository;
import com.test.demo.persistence.repositories.ReactionRepository;
import com.test.demo.persistence.repositories.ReviewRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.projections.dtos.CourseAverageRating;
import com.test.demo.projections.dtos.CourseDto;
import com.test.demo.projections.dtos.CreateReviewDto;
import com.test.demo.projections.dtos.ReviewCountByMonth;
import com.test.demo.projections.dtos.ReviewDto;
import com.test.demo.projections.dtos.TotalReviewsDto;
import com.test.demo.projections.dtos.UpdateReviewDto;
import com.test.demo.projections.dtos.UserDto;
import com.test.demo.projections.mappers.CategoryMapper;
import com.test.demo.projections.mappers.ContentMapper;
import com.test.demo.projections.mappers.CourseMapper;
import com.test.demo.projections.mappers.InstitutionMapper;
import com.test.demo.projections.mappers.ReactionMapper;
import com.test.demo.projections.mappers.ReviewMapper;
import com.test.demo.services.interfaces.InterfaceReviewService;
import com.test.demo.services.interfaces.Roles;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReviewService implements InterfaceReviewService {

    private ReviewRepository reviewRepository;
    private UserRepository userRepository;
    private CourseRepository courseRepository;
    private CategoryRepository categoryRepository;
    private InstitutionRepository institutionRepository;
    private ContentRepository contentRepository;
    private ReactionRepository reactionRepository;
    private ReviewMapper reviewMapper;
    private CourseMapper courseMapper;
    private CategoryMapper categoryMapper;
    private InstitutionMapper institutionMapper;
    private ContentMapper contentMapper;
    private ReactionMapper reactionMapper;

    private final Map<String, String> MONTH_TRANSLATIONS = Map.ofEntries(
            Map.entry("January", "Enero"), Map.entry("February", "Febrero"),
            Map.entry("March", "Marzo"), Map.entry("April", "Abril"),
            Map.entry("May", "Mayo"), Map.entry("June", "Junio"),
            Map.entry("July", "Julio"), Map.entry("August", "Agosto"),
            Map.entry("September", "Septiembre"), Map.entry("October", "Octubre"),
            Map.entry("November", "Noviembre"), Map.entry("December", "Diciembre"));

    public ReviewService(
            ReviewRepository reviewRepository, UserRepository userRepository,
            CourseRepository courseRepository, CategoryRepository categoryRepository, ReactionRepository reactionRepository,
            InstitutionRepository institutionRepository, ContentRepository contentRepository,
            ReviewMapper reviewMapper, CourseMapper courseMapper,
            CategoryMapper categoryMapper, InstitutionMapper institutionMapper,
            ContentMapper contentMapper, ReactionMapper reactionMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.institutionRepository = institutionRepository;
        this.contentRepository = contentRepository;
        this.reactionRepository = reactionRepository;
        this.reviewMapper = reviewMapper;
        this.courseMapper = courseMapper;
        this.categoryMapper = categoryMapper;
        this.institutionMapper = institutionMapper;
        this.reactionMapper = reactionMapper;
        this.contentMapper = contentMapper;
    }

    @Override
    public Mono<List<CourseAverageRating>> getTotalNegativeReviews() {
        return reviewRepository.findLowRatedCourses()
            .collectList();
    }

    @Override
    public Mono<TotalReviewsDto> getTotalReviews() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return reviewRepository.count()
            .flatMap(total -> reviewRepository.countByCreatedAtBetween(startOfMonth, endOfMonth)
                .map(lastMonth -> new TotalReviewsDto(total, lastMonth))
            );
    }

    @Override
    public Mono<Page<ReviewDto>> getAllReviews(int page, int size, String search, String userId) {
        Pageable pageable = PageRequest.of(page, size);
        Flux<Review> reviewFlux = null;

        if (isNumeric(search)) {
            int rating = Integer.parseInt(search);
            reviewFlux = reviewRepository.findByRatingAndUserIdContaining(pageable, rating, userId);
        } else {
            reviewFlux = reviewRepository.findByContentContainingAndUserIdContaining(pageable, search, userId);
        }

        return reviewFlux
                .flatMap(review -> courseRepository.findById(review.getCourseId())
                        .flatMap(course -> userRepository.findById(review.getUserId())
                                .flatMap(user -> {
                                    Mono<Category> categoryMono = categoryRepository.findById(course.getCategoryId());
                                    Mono<Institution> institutionMono = institutionRepository
                                            .findById(course.getInstitutionId());
                                    Flux<Content> contentFlux = contentRepository.findByCourseId(course.getId());
                                    Flux<Review> reviewsFlux = reviewRepository.findByCourseId(course.getId());
                                    Flux<Reaction> reactionFlux = reactionRepository.findByCourseId(course.getId());

                                    return Mono
                                            .zip(categoryMono, institutionMono, contentFlux.collectList(),
                                                    reactionFlux.collectList(), reviewsFlux.collectList())
                                            .map(tuple -> {
                                                ReviewDto reviewDto = reviewMapper.toReviewDto(review);
                                                CourseDto courseDto = courseMapper.toCourseDto(course);
                                                courseDto.setCategory(categoryMapper.toCategoryDto(tuple.getT1()));
                                                courseDto.setInstitution(
                                                        institutionMapper.toInstitutionDto(tuple.getT2()));
                                                courseDto.setContents(tuple.getT3().stream()
                                                        .map(contentMapper::toContentDto)
                                                        .toList());
                                                courseDto.setReactions(tuple.getT4().stream()
                                                        .map(reactionMapper::toReactionDto)
                                                        .toList());
                                                courseDto.setReviews(tuple.getT5().stream()
                                                        .map(reviewMapper::toReviewDto)
                                                        .toList());

                                                reviewDto.setCourse(courseMapper.toCourseDto(course));
                                                reviewDto.setUser(new UserDto(user.getId(), user.getEmail()));

                                                return reviewDto;
                                            });
                                })))
                .collectList()
                .zipWith(reviewRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<Page<ReviewDto>> getReviewsByCourseId(String courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findByCourseId(courseId, pageable)
                .flatMap(review -> userRepository.findById(review.getUserId())
                        .flatMap(user -> {
                            ReviewDto reviewDto = reviewMapper.toReviewDto(review);
                            UserDto userDto = new UserDto(user.getId(), user.getEmail());
                            reviewDto.setUser(userDto);
                            return Mono.just(reviewDto);
                        }))
                .switchIfEmpty(Mono.error(
                        new CustomWebExchangeBindException(
                                courseId,
                                "courseId",
                                "No se encontraron reseñas del curso indicado. Te sugerimos que verifiques la información y lo intentes de nuevo.")
                                .getWebExchangeBindException()))
                .collectList()
                .zipWith(reviewRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<Page<ReviewDto>> getReviewsByAuthUser(Principal principal, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findByEmail(principal.getName())
                .flatMapMany(user -> {
                    Flux<Review> reviewSearchFlux = null;
                    if (isNumeric(search)) {
                        int rating = Integer.parseInt(search);
                        reviewSearchFlux = reviewRepository.findByRatingAndUserIdContaining(pageable, rating,
                                user.getId());
                    } else {
                        reviewSearchFlux = reviewRepository.findByContentContainingAndUserIdContaining(pageable, search,
                                user.getId());
                    }

                    return reviewSearchFlux.flatMap(review -> courseRepository.findById(review.getCourseId())
                        .flatMap(course -> {
                            Mono<Category> categoryMono = categoryRepository.findById(course.getCategoryId());
                            Mono<Institution> institutionMono = institutionRepository
                                    .findById(course.getInstitutionId());
                            Flux<Content> contentFlux = contentRepository.findByCourseId(course.getId());
                            Flux<Reaction> reactionFlux = reactionRepository.findByCourseId(course.getId());
                            Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());
                            
                            return Mono
                                    .zip(categoryMono, institutionMono, contentFlux.collectList(),
                                            reactionFlux.collectList(), reviewFlux.collectList())
                                    .map(tuple -> {
                                        ReviewDto reviewDto = reviewMapper.toReviewDto(review);
                                        CourseDto courseDto = courseMapper.toCourseDto(course);
                                        courseDto.setCategory(categoryMapper.toCategoryDto(tuple.getT1()));
                                        courseDto.setInstitution(institutionMapper.toInstitutionDto(tuple.getT2()));
                                        courseDto.setContents(tuple.getT3().stream()
                                                .map(contentMapper::toContentDto)
                                                .toList());
                                        courseDto.setReactions(tuple.getT4().stream()
                                                .map(reactionMapper::toReactionDto)
                                                .toList());
                                        courseDto.setReviews(tuple.getT5().stream()
                                                .map(reviewMapper::toReviewDto)
                                                .toList());

                                        reviewDto.setCourse(courseMapper.toCourseDto(course));
                                        reviewDto.setUser(new UserDto(user.getId(), user.getEmail()));

                                        return reviewDto;
                                    });
                        }));
                })
                .switchIfEmpty(Mono.error(
                        new CustomWebExchangeBindException(
                                principal.getName(),
                                "auth",
                                "No se encontraron reseñas del usuario autenticado. Te sugerimos que verifiques la información y lo intentes de nuevo.")
                                .getWebExchangeBindException()))
                .collectList()
                .zipWith(reviewRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<List<ReviewCountByMonth>> getReviewCountsForLastSixMonths() {
        LocalDate today = LocalDate.now();
        List<ReviewCountByMonth> lastSixMonths = generateLastSixMonths(today);

        return reviewRepository.countReviewsLastSixMonths(today.minusMonths(5).withDayOfMonth(1))
                .collectList()
                .map(reviewCounts -> mergeWithMissingMonths(lastSixMonths, reviewCounts));
    }

    @Override
    public Mono<Object> createReview(Principal principal, CreateReviewDto createReviewDto) {
        return userRepository.findByEmail(principal.getName())
                .flatMap(user -> courseRepository.findById(createReviewDto.getCourseId())
                        .flatMap(course -> reviewRepository.findByUserIdAndCourseId(user.getId(), course.getId())
                                .flatMap(review -> Mono.error(
                                        new CustomWebExchangeBindException(
                                                createReviewDto.getCourseId(),
                                                "courseId",
                                                "¡Tu reseña es importante para nosotros! Sin embargo, solo se permite una reseña por usuario para cada curso.")
                                                .getWebExchangeBindException()))
                                .switchIfEmpty(Mono.defer(() -> {
                                    Review review = reviewMapper.toReview(createReviewDto);
                                    review.setUserId(user.getId());
                                    return reviewRepository.save(review)
                                            .flatMap(savedReview -> {
                                                Mono<Category> categoryMono = categoryRepository
                                                        .findById(course.getCategoryId());
                                                Mono<Institution> institutionMono = institutionRepository
                                                        .findById(course.getInstitutionId());
                                                Flux<Content> contentFlux = contentRepository
                                                        .findByCourseId(course.getId());
                                                Flux<Reaction> reactionFlux = reactionRepository.findByCourseId(course.getId());
                                                Flux<Review> reviewFlux = reviewRepository
                                                        .findByCourseId(course.getId());

                                                return Mono
                                                        .zip(categoryMono, institutionMono, contentFlux.collectList(),
                                                                reactionFlux.collectList(), reviewFlux.collectList())
                                                        .map(tuple -> {
                                                            ReviewDto reviewDto = reviewMapper.toReviewDto(review);
                                                            CourseDto courseDto = courseMapper.toCourseDto(course);
                                                            courseDto.setCategory(
                                                                    categoryMapper.toCategoryDto(tuple.getT1()));
                                                            courseDto.setInstitution(
                                                                    institutionMapper.toInstitutionDto(tuple.getT2()));
                                                            courseDto.setContents(tuple.getT3().stream()
                                                                    .map(contentMapper::toContentDto)
                                                                    .toList());
                                                            courseDto.setReactions(tuple.getT4().stream()
                                                                    .map(reactionMapper::toReactionDto)
                                                                    .toList());
                                                            courseDto.setReviews(tuple.getT5().stream()
                                                                    .map(reviewMapper::toReviewDto)
                                                                    .toList());

                                                            reviewDto.setCourse(courseMapper.toCourseDto(course));
                                                            reviewDto.setUser(
                                                                    new UserDto(user.getId(), user.getEmail()));

                                                            return reviewDto;
                                                        });
                                            });
                                })))
                        .switchIfEmpty(Mono.error(
                                new CustomWebExchangeBindException(
                                        createReviewDto.getCourseId(),
                                        "courseId",
                                        "No hemos podido encontrar el curso indicado. Te sugerimos que verifiques la información y lo intentes de nuevo.")
                                        .getWebExchangeBindException())))
                .switchIfEmpty(Mono.error(
                        new CustomWebExchangeBindException(
                                principal.getName(),
                                "auth",
                                "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar.")
                                .getWebExchangeBindException()));
    }

    @Override
    public Mono<ReviewDto> updateReview(Principal principal, String id, UpdateReviewDto updateReviewDto) {
        return userRepository.findByEmail(principal.getName())
                .flatMap(user -> reviewRepository.findById(id)
                        .flatMap(review -> {
                            if (review.getUserId().equals(user.getId())
                                    || user.getRoles().contains(Roles.PREFIX + Roles.ADMIN)) {
                                review.setContent(updateReviewDto.getContent());
                                review.setRating(updateReviewDto.getRating());
                                return reviewRepository.save(review)
                                        .flatMap(savedReview -> courseRepository.findById(savedReview.getCourseId())
                                                .flatMap(course -> {
                                                    Mono<Category> categoryMono = categoryRepository
                                                            .findById(course.getCategoryId());
                                                    Mono<Institution> institutionMono = institutionRepository
                                                            .findById(course.getInstitutionId());
                                                    Flux<Content> contentFlux = contentRepository
                                                            .findByCourseId(course.getId());
                                                    Flux<Reaction> reactionFlux = reactionRepository.findByCourseId(course.getId());
                                                    Flux<Review> reviewFlux = reviewRepository
                                                            .findByCourseId(course.getId());
                                                    Mono<User> userMono = userRepository
                                                            .findById(savedReview.getUserId());

                                                    return Mono
                                                            .zip(categoryMono, institutionMono,
                                                                    contentFlux.collectList(), reactionFlux.collectList(),
                                                                    reviewFlux.collectList(), userMono)
                                                            .map(tuple -> {
                                                                ReviewDto reviewDto = reviewMapper.toReviewDto(review);
                                                                CourseDto courseDto = courseMapper.toCourseDto(course);
                                                                courseDto.setCategory(
                                                                        categoryMapper.toCategoryDto(tuple.getT1()));
                                                                courseDto.setInstitution(institutionMapper
                                                                        .toInstitutionDto(tuple.getT2()));
                                                                courseDto.setContents(tuple.getT3().stream()
                                                                        .map(contentMapper::toContentDto)
                                                                        .toList());
                                                                courseDto.setReactions(tuple.getT4().stream()
                                                                        .map(reactionMapper::toReactionDto)
                                                                        .toList());
                                                                courseDto.setReviews(tuple.getT5().stream()
                                                                        .map(reviewMapper::toReviewDto)
                                                                        .toList());

                                                                reviewDto.setCourse(courseMapper.toCourseDto(course));
                                                                reviewDto.setUser(new UserDto(tuple.getT6().getId(),
                                                                        tuple.getT6().getEmail()));

                                                                return reviewDto;
                                                            });
                                                }));
                            } else {
                                return Mono.error(
                                        new CustomWebExchangeBindException(
                                                principal.getName(),
                                                "auth",
                                                "No tiene la autorización necesaria para actualizar una reseña que corresponde a otro usuario.")
                                                .getWebExchangeBindException());
                            }
                        })
                        .switchIfEmpty(Mono.error(
                                new CustomWebExchangeBindException(
                                        id,
                                        "reviewId",
                                        "No hemos podido encontrar la reseña indicada. Te sugerimos que verifiques la información y lo intentes de nuevo.")
                                        .getWebExchangeBindException())))
                .switchIfEmpty(Mono.error(
                        new CustomWebExchangeBindException(
                                principal.getName(),
                                "auth",
                                "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar.")
                                .getWebExchangeBindException()));
    }

    @Override
    public Mono<Boolean> deleteReview(Principal principal, String id) {
        return userRepository.findByEmail(principal.getName())
                .flatMap(user -> reviewRepository.findById(id)
                        .flatMap(review -> {
                            if (review.getUserId().equals(user.getId())
                                    || user.getRoles().contains(Roles.PREFIX + Roles.ADMIN)) {
                                return reviewRepository.deleteById(review.getId())
                                        .then(Mono.just(true));
                            } else {
                                return Mono.error(
                                        new CustomWebExchangeBindException(
                                                principal.getName(),
                                                "auth",
                                                "No tiene la autorización necesaria para eliminar una reseña que corresponde a otro usuario.")
                                                .getWebExchangeBindException());
                            }
                        })
                        .switchIfEmpty(Mono.error(
                                new CustomWebExchangeBindException(
                                        id,
                                        "reviewId",
                                        "No hemos podido encontrar la reseña indicada. Te sugerimos que verifiques la información y lo intentes de nuevo.")
                                        .getWebExchangeBindException())))
                .switchIfEmpty(Mono.error(
                        new CustomWebExchangeBindException(
                                principal.getName(),
                                "auth",
                                "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar.")
                                .getWebExchangeBindException()));
    }

    @Override
    public Mono<Integer> getTotalReviewsBySuscriptor(Principal principal) {
        return userRepository.findByEmail(principal.getName())
    		.flatMap(user -> courseRepository.findByUserId(user.getId())
        		.collectList()
        		.flatMapMany(Flux::fromIterable) 
        		.flatMap(course -> reviewRepository.findByCourseId(course.getId())) 
        		.count()
        		.map(Long::intValue)
    		);
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<ReviewCountByMonth> generateLastSixMonths(LocalDate date) {
        return IntStream.range(0, 6)
                .mapToObj(i -> date.minusMonths(i))
                .map(d -> new ReviewCountByMonth(d.getYear(),
                        d.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("en")), 0))
                .sorted(Comparator.comparingInt(ReviewCountByMonth::getYear)
                        .thenComparing(Comparator.comparingInt(this::getMonthIndex)))
                .collect(Collectors.toList());
    }

    private List<ReviewCountByMonth> mergeWithMissingMonths(List<ReviewCountByMonth> predefined,
            List<ReviewCountByMonth> fromDb) {
        Map<String, Integer> reviewCountsMap = fromDb
                .stream()
                .collect(Collectors.toMap(r -> r.getYear() + "-" + r.getMonth(), r -> Math.toIntExact(r.getCount())));

        return predefined.stream()
                .map(r -> new ReviewCountByMonth(r.getYear(),
                        MONTH_TRANSLATIONS.getOrDefault(r.getMonth(), r.getMonth()),
                        reviewCountsMap.getOrDefault(r.getYear() + "-" + r.getMonth(), 0)))
                .collect(Collectors.toList());
    }

    private int getMonthIndex(ReviewCountByMonth review) {
        return LocalDate.of(review.getYear(), 1, 1)
                .withMonth(Arrays
                        .asList("january", "february", "march", "april", "may", "june", "july", "august", "september",
                                "october", "november", "december")
                        .indexOf(review.getMonth().toLowerCase()) + 1)
                .getMonthValue();
    }
}
