package com.test.demo.services.implementations;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import com.test.demo.persistence.documents.Category;
import com.test.demo.persistence.documents.Content;
import com.test.demo.persistence.documents.Course;
import com.test.demo.persistence.documents.Institution;
import com.test.demo.persistence.documents.Reaction;
import com.test.demo.persistence.documents.Review;
import com.test.demo.persistence.documents.UserCourseRecomended;
import com.test.demo.persistence.repositories.CategoryRepository;
import com.test.demo.persistence.repositories.ContentRepository;
import com.test.demo.persistence.repositories.CourseRepository;
import com.test.demo.persistence.repositories.InstitutionRepository;
import com.test.demo.persistence.repositories.ProfileRepository;
import com.test.demo.persistence.repositories.ReactionRepository;
import com.test.demo.persistence.repositories.ReviewRepository;
import com.test.demo.persistence.repositories.UserCourseRecomendedRepository;
import com.test.demo.persistence.repositories.UserInterestRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.persistence.repositories.ViewRepository;
import com.test.demo.projections.dtos.CourseDto;
import com.test.demo.projections.dtos.FormPredictionDto;
import com.test.demo.projections.dtos.MostCommonReactionDto;
import com.test.demo.projections.dtos.PredictionDataDto;
import com.test.demo.projections.dtos.RecomendeCourseDto;
import com.test.demo.projections.dtos.UserDto;
import com.test.demo.projections.dtos.ViewDto;
import com.test.demo.projections.mappers.CategoryMapper;
import com.test.demo.projections.mappers.ContentMapper;
import com.test.demo.projections.mappers.CourseMapper;
import com.test.demo.projections.mappers.InstitutionMapper;
import com.test.demo.projections.mappers.ProfileMapper;
import com.test.demo.projections.mappers.ReactionMapper;
import com.test.demo.projections.mappers.ReviewMapper;
import com.test.demo.projections.mappers.UserMapper;
import com.test.demo.projections.mappers.ViewMapper;
import com.test.demo.services.interfaces.InterfacePredictionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;

@Service
public class PredictionService implements InterfacePredictionService {

    private CourseRepository courseRepository;
    private CategoryRepository categoryRepository;
    private InstitutionRepository institutionRepository;
    private UserRepository userRepository;
    private UserCourseRecomendedRepository userCourseRecomendedRepository;
    private UserInterestRepository userInterestRepository;
    private ProfileRepository profileRepository;
    private ReviewRepository reviewRepository;
    private ReactionRepository reactionRepository;
    private ViewRepository viewRepository;
    private CategoryMapper categoryMapper;
    private InstitutionMapper institutionMapper;
    private CourseMapper courseMapper;
    private ReviewMapper reviewMapper;
    private UserMapper userMapper;
    private ProfileMapper profileMapper;
    private ContentRepository contentRepository;
    private ViewMapper viewMapper;
    private ContentMapper contentMapper;
    private ReactionMapper reactionMapper;
    private Instances dataStructure;
    private Classifier classifier;

    public PredictionService(
        CourseRepository courseRepository,
        CategoryRepository categoryRepository,
        InstitutionRepository institutionRepository,
        UserRepository userRepository,
        UserCourseRecomendedRepository userCourseRecomendedRepository,
        UserInterestRepository userInterestRepository,
        ProfileRepository profileRepository,
        ReviewRepository reviewRepository,
        ReactionRepository reactionRepository,
        ViewRepository viewRepository,
        CategoryMapper categoryMapper,
        InstitutionMapper institutionMapper,
        CourseMapper courseMapper,
        ReviewMapper reviewMapper,
        UserMapper userMapper,
        ProfileMapper profileMapper,
        CourseService courseService,
        CategoryService categoryService,
        ContentRepository contentRepository,
        ViewMapper viewMapper,
        ContentMapper contentMapper,
        ReactionMapper reactionMapper
    ) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
        this.userCourseRecomendedRepository = userCourseRecomendedRepository;
        this.userInterestRepository = userInterestRepository;
        this.profileRepository = profileRepository;
        this.reviewRepository = reviewRepository;
        this.reactionRepository = reactionRepository;
        this.viewRepository = viewRepository;
        this.categoryMapper = categoryMapper;
        this.institutionMapper = institutionMapper;
        this.courseMapper = courseMapper;
        this.reviewMapper = reviewMapper;
        this.userMapper = userMapper;
        this.profileMapper = profileMapper;
        this.contentRepository = contentRepository;
        this.viewMapper = viewMapper;
        this.contentMapper = contentMapper;
        this.reactionMapper = reactionMapper;

        try {
            ClassPathResource modelResource = new ClassPathResource("ramdomForestInterest.model");
            classifier = (Classifier) weka.core.SerializationHelper.read(modelResource.getInputStream());

            ClassPathResource arffResource = new ClassPathResource("courseedStructuredInterest.dataset.arff");
            DataSource source = new DataSource(arffResource.getInputStream());
            dataStructure = source.getDataSet();
            dataStructure.setClassIndex(dataStructure.numAttributes() - 1);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar los recursos necesarios para PredictionService", e);
        }   
    }

    public Mono<UserCourseRecomended> getUserCourseRecomended(String userId, String courseId) {
        return profileRepository.findByUserId(userId)
            .flatMap(profile -> courseRepository.findById(courseId)
                .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                    .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                        .flatMap(institution -> userCourseRecomendedRepository.findByCourseIdAndUserProfileId(courseId, profile.getId())
                            .flatMap(userCourseRecomended -> userInterestRepository.findByUserProfileId(profile.getId())
                                .flatMap(userInterest -> categoryRepository.findById(userInterest.getCategoryId())
                                    .flatMap(interest -> {
                                        Instance instance = new DenseInstance(5);
                                        instance.setDataset(dataStructure);
                                        instance.setValue(0, profile.getInterest());
                                        instance.setValue(1, profile.getPlatformPrefered());
                                        instance.setValue(2, course.getModality());
                                        instance.setValue(3, category.getName());
        
                                        double predictionValue;
                                        try {
                                            predictionValue = classifier.classifyInstance(instance);
                                            System.out.println("Prediction Value: " + predictionValue);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.error(new RuntimeException("Error during classification", e));
                                        }
                                        String prediction = dataStructure.classAttribute().value((int) predictionValue);
                                        double[] probabilities;
                                        String confidencePercentage = null;
                                        try {
                                            probabilities = classifier.distributionForInstance(instance);
                                            double confidence = probabilities[(int) predictionValue];
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            confidencePercentage = df.format(confidence * 100) + "%";
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.error(new RuntimeException("Error during prediction", e));
                                        }
        
                                        userCourseRecomended.setRecomended(prediction.equals("true"));
                                        userCourseRecomended.setConfidence(confidencePercentage);
        
                                        return Mono.just(userCourseRecomended);
                                    })
                                )
                            )
                    ))
                )
            );
    }

    public Mono<RecomendeCourseDto> predictCourseRecommendation(FormPredictionDto formData) {
        try {
            Instance instance = new DenseInstance(5);
            instance.setDataset(dataStructure);
            
            instance.setValue(0, formData.getUser_interest());
            instance.setValue(1, formData.getPlatform_preference());
            instance.setValue(2, formData.getCourse_modality());
            instance.setValue(3, formData.getCourse_category());

            double predictionValue = classifier.classifyInstance(instance);
            String prediction = dataStructure.classAttribute().value((int) predictionValue);            

            double[] probabilities = classifier.distributionForInstance(instance);
            double confidence = probabilities[(int) predictionValue];
            DecimalFormat df = new DecimalFormat("#.#");
            String confidencePercentage = df.format(confidence * 100) + "%";

            RecomendeCourseDto result = new RecomendeCourseDto();
            result.setId(formData.getCourse_id());
            result.setTitle("Curso específico");
            result.setCategory(formData.getCourse_category());
            result.setInstitution(formData.getCourse_institution());
            result.setPrice(formData.getCourse_price() != null ? formData.getCourse_price().toString() : "0");
            result.setRecommended("true".equals(prediction));
            result.setConfidence(confidencePercentage);
            
            return Mono.just(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(new RuntimeException("Error al realizar la predicción", e));
        }
    }

    public Mono<List<RecomendeCourseDto>> getRecomendedCoursesByUser(String userId) {
    return profileRepository.findByUserId(userId)
        .flatMap(profile -> courseRepository.findAll()
            .flatMap(course -> userCourseRecomendedRepository.findByCourseIdAndUserProfileId(course.getId(), profile.getId())
                .flatMap(userCourseRecomended -> userInterestRepository.findByUserProfileId(profile.getId())
                    .flatMap(userInterest -> categoryRepository.findById(userInterest.getCategoryId())
                        .flatMap(insterest -> {

                            return categoryRepository.findById(course.getCategoryId())
                                .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                                    .flatMap(institution -> {
                                        Instance instance = new DenseInstance(5);
                                        instance.setDataset(dataStructure);
                                        instance.setValue(0, profile.getInterest());
                                        instance.setValue(1, profile.getPlatformPrefered());
                                        instance.setValue(2, course.getModality());
                                        instance.setValue(3, category.getName());
        
                                        try {
                                            double predictionValue = classifier.classifyInstance(instance);
                                            String prediction = dataStructure.classAttribute().value((int) predictionValue);
                                            
                                            if (!"true".equals(prediction)) {
                                                return Mono.empty();
                                            }
                                            
                                            double[] probabilities = classifier.distributionForInstance(instance);
                                            double confidence = probabilities[(int) predictionValue];
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            String confidencePercentage = df.format(confidence * 100) + "%";
        
                                            userCourseRecomended.setRecomended(true);
                                            userCourseRecomended.setConfidence(confidencePercentage);
        
                                            RecomendeCourseDto recomendedCourse = new RecomendeCourseDto();
                                            recomendedCourse.setId(course.getId());
                                            recomendedCourse.setTitle(course.getTitle());
                                            recomendedCourse.setCategory(category.getName());
                                            recomendedCourse.setInstitution(institution.getName());
                                            recomendedCourse.setPrice(course.getPrice() != null ? course.getPrice().toString() : "0");
                                            recomendedCourse.setRecommended(true);
                                            recomendedCourse.setConfidence(confidencePercentage);
                                            
                                            return Mono.just(recomendedCourse);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.empty();
                                        }
                                    })
                                );
                        })
                    )
                )
            )
            .collectList()
        );
    }

    public Mono<Integer> getTotalCoursesRecomended(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> profileRepository.findByUserId(user.getId())
                .flatMap(profile -> courseRepository.findAll()
                    .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                        .flatMap(category -> {
                            Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
                                .defaultIfEmpty(0.0);
                            
                            Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
                                .map(MostCommonReactionDto::getType)
                                .defaultIfEmpty("NONE");
                            
                            Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
                                .defaultIfEmpty(0L);
                            
                            Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
                                .defaultIfEmpty(0L);
                            
                            return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono)
                                .flatMap(tuple -> {
                                    
                                    try {
                                        Instance instance = new DenseInstance(5);
                                        instance.setDataset(dataStructure);
                                        instance.setValue(0, profile.getInterest());
                                        instance.setValue(1, profile.getPlatformPrefered());
                                        instance.setValue(2, course.getModality());
                                        instance.setValue(3, category.getName());

                                        double predictionValue = classifier.classifyInstance(instance);
                                        String prediction = dataStructure.classAttribute().value((int) predictionValue);

                                        return Mono.just(prediction.equals("true") ? 1 : 0);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return Mono.just(0);
                                    }
                                });
                        })
                        .defaultIfEmpty(0)
                    )
                    .reduce(0, Integer::sum)
                )
            
            )
            .defaultIfEmpty(0);
    }

    // public Mono<Page<CourseDto>> getRecomendedCoursesByAuth(Principal principal, int page, int size) {
    //     Pageable pageable = PageRequest.of(page, size);

    
    // return userRepository.findByEmail(principal.getName())
    //     .flatMap(user -> profileRepository.findByUserId(user.getId())
    //         .flatMap(profile -> courseRepository.findAll()
    //             .flatMap(course -> categoryRepository.findById(course.getCategoryId())
    //                 .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
    //                     .flatMap(institution -> {
    //                         System.out.println("Course ID: " + course.getId());
    //                         Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
    //                             .defaultIfEmpty(0.0);

    //                         Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
    //                             .map(MostCommonReactionDto::getType)
    //                             .defaultIfEmpty("NONE");

    //                         Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
    //                             .defaultIfEmpty(0L);

    //                         Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
    //                             .defaultIfEmpty(0L);

    //                         Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());

    //                         return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono, reviewFlux.collectList())
    //                             .flatMap(tuple -> {
    //                                 try {
    //                                     Instance instance = new DenseInstance(5);
    //                                     instance.setDataset(dataStructure);
    //                                     instance.setValue(0, profile.getInterest());
    //                                     instance.setValue(1, profile.getPlatformPrefered());
    //                                     instance.setValue(2, course.getModality());
    //                                     instance.setValue(3, category.getName());

    //                                     double predictionValue = classifier.classifyInstance(instance);
    //                                     String prediction = dataStructure.classAttribute().value((int) predictionValue);

    //                                     double[] probabilities = classifier.distributionForInstance(instance);
    //                                     double confidence = probabilities[(int) predictionValue];
    //                                     DecimalFormat df = new DecimalFormat("#.#");
    //                                     String confidencePercentage = df.format(confidence * 100) + "%";

    //                                     if (prediction.equals("true")) {
    //                                         PredictionDataDto predictionDto = new PredictionDataDto();
    //                                         predictionDto.setUserInterest(profile.getInterest());
    //                                         predictionDto.setUserAvailableTime(Double.valueOf(profile.getAvailableHoursTime()));
    //                                         predictionDto.setBudget(profile.getBudget().intValue());
    //                                         predictionDto.setPlatformPreference(profile.getPlatformPrefered());
    //                                         predictionDto.setCourseModality(course.getModality());
    //                                         predictionDto.setCourseDuration(Integer.parseInt(course.getDuration()));
    //                                         predictionDto.setCoursePrice(course.getPrice());
    //                                         predictionDto.setCourseCategory(category.getName());
    //                                         predictionDto.setCourseRatingAvg(tuple.getT1());
    //                                         predictionDto.setCourseMaxReaction(tuple.getT2());
    //                                         predictionDto.setCourseVisits(tuple.getT3().intValue());
    //                                         predictionDto.setCourseReviewsCount(tuple.getT4().intValue());
    //                                         predictionDto.setCourseRecomended(prediction.equals("true"));
    //                                         predictionDto.setConfidence(confidencePercentage);

    //                                         CourseDto courseDto = courseMapper.toCourseDto(course);
    //                                         courseDto.setCategory(categoryMapper.toCategoryDto(category));
    //                                         courseDto.setInstitution(institutionMapper.toInstitutionDto(institution));
    //                                         courseDto.setReviews(tuple.getT5().stream()
    //                                             .map(reviewMapper::toReviewDto)
    //                                             .toList()
    //                                         );
    //                                         courseDto.setPrediction(predictionDto);

    //                                         return Mono.just(courseDto);
    //                                     } else {
    //                                         return Mono.empty();
    //                                     }
    //                                 } catch (Exception e) {
    //                                     e.printStackTrace();
    //                                     return Mono.empty();
    //                                 }
    //                             });
    //                     })
    //                 )
    //             )
    //             .filter(c -> c != null)
    //             .take(size) // <-- Solo toma los primeros 'size' recomendados
    //             .collectList()
    //             .flatMap(courses -> {
    //                 Page<CourseDto> pageResult = new PageImpl<>(courses, pageable, courses.size());
    //                 return Mono.just(pageResult);
    //             })
    //         )
    //     )
    //     .defaultIfEmpty(new PageImpl<>(List.of(), pageable, 0));
    // }

    public Mono<Page<CourseDto>> getRecomendedCoursesByAuth(Principal principal, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> profileRepository.findByUserId(user.getId())
                .flatMap(profile -> getRecommendedPagedCourses(profile, 0, size, size)
                    .map(courses -> (Page<CourseDto>) new PageImpl<>(courses, pageable, courses.size()))
                )
            )
            .defaultIfEmpty(new PageImpl<>(List.of(), pageable, 0));
    }

    private Mono<List<CourseDto>> getRecommendedPagedCourses(
        com.test.demo.persistence.documents.Profile profile, int page, int pageSize, int remaining) {
        Pageable pageable = PageRequest.of(page, pageSize);

        return courseRepository.findAllBy(pageable)
            .collectList()
            .flatMap(courses -> {
                if (courses.isEmpty() || remaining <= 0) {
                    return Mono.just(List.of());
                }
                return Flux.fromIterable(courses)
                    .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                        .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                            .flatMap(institution -> {
                                System.out.println("Course ID: " + course.getId());
                                Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
                                    .defaultIfEmpty(0.0);
                                Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
                                    .map(MostCommonReactionDto::getType)
                                    .defaultIfEmpty("NONE");
                                Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
                                    .defaultIfEmpty(0L);
                                Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
                                    .defaultIfEmpty(0L);
                                Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());

                                return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono, reviewFlux.collectList())
                                    .flatMap(tuple -> {
                                        try {
                                            Instance instance = new DenseInstance(5);
                                            instance.setDataset(dataStructure);
                                            instance.setValue(0, profile.getInterest());
                                            instance.setValue(1, profile.getPlatformPrefered());
                                            instance.setValue(2, course.getModality());
                                            instance.setValue(3, category.getName());

                                            double predictionValue = classifier.classifyInstance(instance);
                                            String prediction = dataStructure.classAttribute().value((int) predictionValue);

                                            double[] probabilities = classifier.distributionForInstance(instance);
                                            double confidence = probabilities[(int) predictionValue];
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            String confidencePercentage = df.format(confidence * 100) + "%";

                                            if (prediction.equals("true")) {
                                                PredictionDataDto predictionDto = new PredictionDataDto();
                                                predictionDto.setUserInterest(profile.getInterest());
                                                predictionDto.setUserAvailableTime(Double.valueOf(profile.getAvailableHoursTime()));
                                                predictionDto.setBudget(profile.getBudget().intValue());
                                                predictionDto.setPlatformPreference(profile.getPlatformPrefered());
                                                predictionDto.setCourseModality(course.getModality());
                                                predictionDto.setCourseDuration(Integer.parseInt(course.getDuration()));
                                                predictionDto.setCoursePrice(course.getPrice());
                                                predictionDto.setCourseCategory(category.getName());
                                                predictionDto.setCourseRatingAvg(tuple.getT1());
                                                predictionDto.setCourseMaxReaction(tuple.getT2());
                                                predictionDto.setCourseVisits(tuple.getT3().intValue());
                                                predictionDto.setCourseReviewsCount(tuple.getT4().intValue());
                                                predictionDto.setCourseRecomended(true);
                                                predictionDto.setConfidence(confidencePercentage);

                                                CourseDto courseDto = courseMapper.toCourseDto(course);
                                                courseDto.setCategory(categoryMapper.toCategoryDto(category));
                                                courseDto.setInstitution(institutionMapper.toInstitutionDto(institution));
                                                courseDto.setReviews(tuple.getT5().stream()
                                                    .map(reviewMapper::toReviewDto)
                                                    .toList()
                                                );
                                                courseDto.setPrediction(predictionDto);

                                                return Mono.just(courseDto);
                                            } else {
                                                return Mono.empty();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.empty();
                                        }
                                    });
                            })
                        )
                    )
                    .take(remaining)
                    .collectList()
                    .flatMap(recommended -> {
                        if (recommended.size() >= remaining || courses.size() < pageSize) {
                            return Mono.just(recommended);
                        } else {
                            // Buscar en la siguiente página
                            return getRecommendedPagedCourses(profile, page + 1, pageSize, remaining - recommended.size())
                                .map(next -> {
                                    recommended.addAll(next);
                                    return recommended;
                                });
                        }
                    });
            });
    }

    public Mono<Page<CourseDto>> getRecomendedCoursesByHistoryAndAuth(Principal principal, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> profileRepository.findByUserId(user.getId())
                .flatMap(profile -> {

                    Mono<String> lastViewedCategorySortMono = viewRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                        .take(1)
                        .next()
                        .flatMap(view -> courseRepository.findById(view.getCourseId())
                            .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                                .map(category -> category.getName())
                            )
                        )
                        .defaultIfEmpty(profile.getInterest());

                    Mono<String> lastViewedModalitySortMono = viewRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                        .take(1)
                        .next()
                        .flatMap(view -> courseRepository.findById(view.getCourseId())
                            .map(course -> course.getModality())
                        )
                        .defaultIfEmpty(profile.getPlatformPrefered());
                    
                    return lastViewedCategorySortMono.flatMapMany(lastViewedCategory -> lastViewedModalitySortMono
                        .flatMapMany(lastViewedModality -> courseRepository.findAll()
                            .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                                .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                                    .flatMap(institution -> {

                                        Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
                                            .defaultIfEmpty(0.0);
                                        
                                        Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
                                            .map(MostCommonReactionDto::getType)
                                            .defaultIfEmpty("NONE");
                                        
                                        Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
                                            .defaultIfEmpty(0L);
                                        
                                        Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
                                            .defaultIfEmpty(0L);

                                        Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());
                                        
                                        return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono, reviewFlux.collectList())
                                            .flatMap(tuple -> {
                                                
                                                try {
                                                    Instance instance = new DenseInstance(13);
                                                    instance.setDataset(dataStructure);
                                                    instance.setValue(0, lastViewedCategory);
                                                    instance.setValue(1, lastViewedModality);
                                                    instance.setValue(2, course.getModality());
                                                    instance.setValue(3, category.getName());

                                                    double predictionValue = classifier.classifyInstance(instance);
                                                    String prediction = dataStructure.classAttribute().value((int) predictionValue);

                                                    double[] probabilities = classifier.distributionForInstance(instance);
                                                    double confidence = probabilities[(int) predictionValue];
                                                    DecimalFormat df = new DecimalFormat("#.#");
                                                    String confidencePercentage = df.format(confidence * 100) + "%";

                                                    if (prediction.equals("true")) {
                                                        PredictionDataDto predictionDto = new PredictionDataDto();
                                                        predictionDto.setUserInterest(profile.getInterest());
                                                        predictionDto.setUserAvailableTime(Double.valueOf(profile.getAvailableHoursTime()));
                                                        predictionDto.setBudget(profile.getBudget().intValue());
                                                        predictionDto.setPlatformPreference(profile.getPlatformPrefered());
                                                        predictionDto.setCourseModality(course.getModality());
                                                        predictionDto.setCourseDuration(Integer.parseInt(course.getDuration()));
                                                        predictionDto.setCoursePrice(course.getPrice());
                                                        predictionDto.setCourseCategory(category.getName());
                                                        predictionDto.setCourseRatingAvg(tuple.getT1());
                                                        predictionDto.setCourseMaxReaction(tuple.getT2());
                                                        predictionDto.setCourseVisits(tuple.getT3().intValue());
                                                        predictionDto.setCourseReviewsCount(tuple.getT4().intValue());
                                                        predictionDto.setCourseRecomended(prediction.equals("true"));
                                                        predictionDto.setConfidence(confidencePercentage);

                                                        CourseDto courseDto = courseMapper.toCourseDto(course);
                                                        courseDto.setCategory(categoryMapper.toCategoryDto(category));
                                                        courseDto.setInstitution(institutionMapper.toInstitutionDto(institution));
                                                        courseDto.setReviews(tuple.getT5().stream()
                                                            .map(reviewMapper::toReviewDto)
                                                            .toList()
                                                        );
                                                        courseDto.setPrediction(predictionDto);
                                                        
                                                        return Mono.just(courseDto);
                                                    } else {
                                                        return Mono.empty();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    return Mono.empty();
                                                }
                                            });
                                    })
                                )
                            )
                        )
                    )
                    .collectList()
                    .flatMap(courses -> {
                        List<CourseDto> paginatedCourses = courses.stream()
                            .filter(c -> c != null)
                            .skip(page * size)
                            .limit(size)            
                            .toList();
        
                        Page<CourseDto> pageResult = new PageImpl<>(paginatedCourses, pageable, courses.size());
                        return Mono.just(pageResult);
                    });
                })
            )
            .defaultIfEmpty(new PageImpl<>(List.of(), pageable, 0));
    }

    public Mono<Page<UserDto>> getRecomendedUsersByCourse(String courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findById(courseId)
            .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                    .flatMap(institution -> userRepository.findAll()
                        .flatMap(user -> profileRepository.findByUserId(user.getId())
                            .flatMap(profile -> {
                                Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
                                    .defaultIfEmpty(0.0);
                                
                                Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
                                    .map(MostCommonReactionDto::getType)
                                    .defaultIfEmpty("NONE");
                                
                                Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
                                    .defaultIfEmpty(0L);
                                
                                Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
                                    .defaultIfEmpty(0L);

                                Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());

                                return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono, reviewFlux.collectList())
                                    .flatMap(tuple -> {
                                        
                                        try {
                                            Instance instance = new DenseInstance(13);
                                            instance.setDataset(dataStructure);
                                            instance.setValue(0, profile.getInterest());
                                            instance.setValue(1, profile.getPlatformPrefered());
                                            instance.setValue(2, course.getModality());
                                            instance.setValue(3, category.getName());

                                            double predictionValue = classifier.classifyInstance(instance);
                                            String prediction = dataStructure.classAttribute().value((int) predictionValue);

                                            double[] probabilities = classifier.distributionForInstance(instance);
                                            double confidence = probabilities[(int) predictionValue];
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            String confidencePercentage = df.format(confidence * 100) + "%";

                                            if (prediction.equals("true")) {
                                                PredictionDataDto predictionDto = new PredictionDataDto();
                                                predictionDto.setUserInterest(profile.getInterest());
                                                predictionDto.setUserAvailableTime(Double.valueOf(profile.getAvailableHoursTime()));
                                                predictionDto.setBudget(profile.getBudget().intValue());
                                                predictionDto.setPlatformPreference(profile.getPlatformPrefered());
                                                predictionDto.setCourseModality(course.getModality());
                                                predictionDto.setCourseDuration(Integer.parseInt(course.getDuration()));
                                                predictionDto.setCoursePrice(course.getPrice());
                                                predictionDto.setCourseCategory(category.getName());
                                                predictionDto.setCourseRatingAvg(tuple.getT1());
                                                predictionDto.setCourseMaxReaction(tuple.getT2());
                                                predictionDto.setCourseVisits(tuple.getT3().intValue());
                                                predictionDto.setCourseReviewsCount(tuple.getT4().intValue());
                                                predictionDto.setCourseRecomended(prediction.equals("true"));
                                                predictionDto.setConfidence(confidencePercentage);

                                                UserDto userDto = userMapper.toUserDto(user);
                                                userDto.setProfile(profileMapper.toProfileDto(profile));
                                                userDto.setPrediction(predictionDto);
                                                
                                                return Mono.just(userDto);
                                            } else {
                                                return Mono.empty();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.empty();
                                        }
                                    });
                            })
                        )
                        .collectList()
                        .flatMap(users -> {
                            List<UserDto> paginatedUsers = users.stream()
                                .filter(c -> c != null)
                                .skip(page * size)
                                .limit(size)            
                                .toList();
            
                            Page<UserDto> pageResult = new PageImpl<>(paginatedUsers, pageable, users.size());
                            return Mono.just(pageResult);
                        })
                    )
                )
            )
            .defaultIfEmpty(new PageImpl<>(List.of(), pageable, 0));
    }

    public Mono<Page<CourseDto>> getAllCoursesWithAvgConfidence(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String searchRegex = (search != null && !search.isEmpty()) ? ".*" + search + ".*" : ".*";
        Flux<Course> courseFlux = courseRepository.findByTitleRegexIgnoreCaseOrUrlRegexIgnoreCaseOrDurationRegexIgnoreCaseOrModalityRegexIgnoreCase(
            searchRegex, searchRegex, searchRegex, searchRegex, pageable
        );

        return courseFlux.flatMap(course -> {
                Mono<Category> categoryMono = categoryRepository.findById(course.getCategoryId());
                Mono<Institution> institutionMono = institutionRepository.findById(course.getInstitutionId());
                Flux<Content> contentFlux = contentRepository.findByCourseId(course.getId());
                Flux<Reaction> reactionFlux = reactionRepository.findByCourseId(course.getId());
                Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());
                Flux<ViewDto> viewFlux = viewRepository.findByCourseId(course.getId())
                    .flatMap(view -> userRepository.findById(view.getUserId())
                        .map(user -> {
                            ViewDto viewDto = viewMapper.toViewDto(view);
                            viewDto.setUser(new UserDto(user.getId(), user.getEmail()));
                            return viewDto;
                        })
                    );

                return Mono.zip(categoryMono, institutionMono, contentFlux.collectList(), reactionFlux.collectList(), reviewFlux.collectList(), viewFlux.collectList())
                    .map(tuple -> {
                        CourseDto courseDto = courseMapper.toCourseDto(course);
                        courseDto.setCategory(categoryMapper.toCategoryDto(tuple.getT1()));
                        courseDto.setInstitution(institutionMapper.toInstitutionDto(tuple.getT2()));
                        courseDto.setContents(tuple.getT3().stream()
                            .map(contentMapper::toContentDto)
                            .toList()
                        );
                        courseDto.setReactions(tuple.getT4().stream().map(reactionMapper::toReactionDto).toList());
                        courseDto.setReviews(tuple.getT5().stream()
                            .map(reviewMapper::toReviewDto)
                            .toList()
                        );
                        courseDto.setViews(tuple.getT6());

                        return courseDto;
                    });
            })
            .collectList()
            .zipWith(courseRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }
}
