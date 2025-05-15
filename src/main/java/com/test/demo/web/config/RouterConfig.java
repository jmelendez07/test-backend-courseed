package com.test.demo.web.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.web.controllers.AuthController;
import com.test.demo.web.controllers.CategoryController;
import com.test.demo.web.controllers.ContentController;
import com.test.demo.web.controllers.CourseController;
import com.test.demo.web.controllers.InstitutionController;
import com.test.demo.web.controllers.PredictionController;
import com.test.demo.web.controllers.ProfileController;
import com.test.demo.web.controllers.ReactionController;
import com.test.demo.web.controllers.ReviewController;
import com.test.demo.web.controllers.RoleController;
import com.test.demo.web.controllers.SearchHistoryController;
import com.test.demo.web.controllers.SubscriptionController;
import com.test.demo.web.controllers.UserController;
import com.test.demo.web.controllers.ViewController;

@Configuration
public class RouterConfig {

    @Bean
    RouterFunction<ServerResponse> routes(
        AuthController authController, CategoryController categoryController,
        ContentController contentController,  CourseController courseController,
        InstitutionController institutionController, ViewController viewController,
        ReviewController reviewController, UserController userController,
        RoleController roleController, ReactionController reactionController,
        SearchHistoryController searchHistoryController, SubscriptionController subscriptionController,
        ProfileController profileController, PredictionController predictionController
    ) {
        return RouterFunctions.route()
            .path("/auth", () -> authRoutes(authController))
            .GET("/", request -> ServerResponse.ok().bodyValue("Hola desde Spring WebFlux!"))
            .path("/categories", () -> categoryRoutes(categoryController))
            .path("/contents", () -> contentRoutes(contentController))
            .path("/courses", () -> courseRoutes(courseController))
            .path("/institutions", () -> institutionRoutes(institutionController))
            .path("/reviews", () -> reviewRoutes(reviewController))
            .path("/users", () -> userRoutes(userController))
            .path("/roles", () -> roleRoutes(roleController))
            .path("/reactions", () -> reactionRoutes(reactionController))
            .path("/views", () -> viewRoutes(viewController))
            .path("/search-histories", () -> searchHistoryRoutes(searchHistoryController))
            .path("/subscriptions", () -> subscriptionRoutes(subscriptionController))
            .path("/profiles", () -> profileRoutes(profileController))
            .path("/predictions", () -> predictionRoutes(predictionController))
            .build();
    }

    private RouterFunction<ServerResponse> authRoutes(AuthController authController) {
        return RouterFunctions
            .route()
            .GET(authController::getAuthUser)
            .POST("/login", authController::login)
            .POST("/register", authController::register)
            .POST("/register/subscriptor", authController::registerSubscriptor)
            .PUT("/password", authController::updatePassword)
            .PUT("/profile", authController::updateProfile)
            .PUT("/subscribe", authController::subscribe)
            .PUT("/upload-avatar", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), authController::uploadAvatar)
            .build();
    }

    private RouterFunction<ServerResponse> categoryRoutes(CategoryController categoryController) {
        return RouterFunctions
            .route()
            .GET("", categoryController::getAllCategories)
            .GET("/{id}", categoryController::getCategoryById)
            .GET("/name/{name}", categoryController::getCategoryByName)
            .DELETE("/{id}", categoryController::deleteCategory)
            .build();
    }

    private RouterFunction<ServerResponse> contentRoutes(ContentController contentController) {
        return RouterFunctions
            .route()
            .GET("/{id}", contentController::getContentById)
            .GET("/course/{courseId}", contentController::getContentByCourseId)
            .POST(contentController::createContent)
            .PUT("/{id}", contentController::updateContent)
            .DELETE("/{id}", contentController::deleteContent)
            .build();
    }

    private RouterFunction<ServerResponse> courseRoutes(CourseController courseController) {
        return RouterFunctions
            .route()
            .GET("", courseController::getAllCourses)
            .GET("/auth", courseController::getCoursesByAuthUser)
            .GET("/search", courseController::searchCoursesByText)
            .GET("/reviews/avg", courseController::getTopCoursesWithRatingAvg)
            .GET("/type", courseController::getCoursesByType)
            .GET("/{id}", courseController::getCourseById)
            .GET("/category/{categoryId}", courseController::getCoursesByCategoryId)
            .GET("/institution/{institutionId}", courseController::getCoursesByInstitutionId)
            .GET("/reviews-reactions/count", courseController::getTopCoursesWithReviewsAndReactions)
            .GET("/suscriptor/count", courseController::getTotalCoursesBySuscriptor)
            .POST(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), courseController::createCourse)
            .PUT("/{id}", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), courseController::updateCourse)
            .DELETE("/{id}", courseController::deleteCourse)
            .build();
    }

    private RouterFunction<ServerResponse> institutionRoutes(InstitutionController institutionController) {
        return RouterFunctions
            .route()
            .GET("", institutionController::getAllInstitutions)
            .GET("/auth", institutionController::getInstitutionByAuth)
            .GET("/{id}", institutionController::getInstitutionById)
            .GET("/name/{name}", institutionController::getInstitutionByName)
            .GET("/courses/count", institutionController::getInstitutionsWithCoursesCount)
            .POST(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), institutionController::createInstitution)
            .PUT("/{id}", RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA), institutionController::updateInstitution)
            .DELETE("/{id}", institutionController::deleteInstitution)
            .build();
    }

    private RouterFunction<ServerResponse> reactionRoutes(ReactionController reactionController) {
        return RouterFunctions
            .route()
            .GET("/course/{courseId}", reactionController::findReactionsByCourseId)
            .GET("/auth", reactionController::findReactionsByAuthUser)
            .GET("/total/this-month", reactionController::getTotalReactions)
            .GET("/suscriptor/count", reactionController::getTotalReactionsBySuscriptor)    
            .POST(reactionController::createReaction)
            .PUT(reactionController::updateReaction)
            .DELETE("/{id}", reactionController::deleteReaction)
            .build();
    }

    private RouterFunction<ServerResponse> reviewRoutes(ReviewController reviewController) {
        return RouterFunctions
            .route()
            .GET("", reviewController::getAllReviews)
            .GET("/months/count", reviewController::getReviewCountsForLastSixMonths)
            .GET("/total/this-month", reviewController::getTotalReviews)
            .GET("/total/negative", reviewController::getTotalNegativeReviews)
            .GET("/course/{courseId}", reviewController::getReviewsByCourseId)
            .GET("/auth", reviewController::getReviewsByAuthUser)
            .GET("/suscriptor/count", reviewController::getTotalReviewsBySuscriptor)
            .POST(reviewController::createReview)
            .PUT("/{id}", reviewController::updateReview)
            .DELETE("/{id}", reviewController::deleteReview)
            .build();
    }

    private RouterFunction<ServerResponse> userRoutes(UserController userController) {
        return RouterFunctions
            .route()
            .GET("", userController::getAllUsers)
            .GET("/count", userController::getAllUsersCount)
            .GET("/count/interest-or-modality", userController::getAllUsersCountByInterestOrModality)
            .GET("/{id}", userController::getUserById)
            .GET("/email/{email}", userController::getUserByEmail)
            .GET("/total/this-month", userController::getTotalUsers)
            .GET("/months/count", userController::getUserCountForLastSixMonths)
            .POST("/create", userController::createUser)
            .PUT("/email/{id}", userController::updateUserEmail)
            .PUT("/password/{id}", userController::updateUserPassword)
            .PUT("/roles/{id}", userController::updateUserRoles)
            .DELETE("/{id}", userController::deleteUser)
            .build();
    }

    private RouterFunction<ServerResponse> roleRoutes(RoleController roleController) {
        return RouterFunctions
            .route()
            .GET("", roleController::getAllRoles)
            .GET("/users/count", roleController::getRolesWithUserCount)
            .build();
    }

    private RouterFunction<ServerResponse> viewRoutes(ViewController viewController) {
        return RouterFunctions
            .route()
            .GET("/course/{courseId}", viewController::findViewsByCourseId)
            .GET("/auth", viewController::findViewsByAuthUser)
            .GET("/total/this-month", viewController::getTotalViews)
            .GET("/courses/this-month/decreasing", viewController::findCoursesWithDecreasingViews)
            .GET("/suscriptor/count", viewController::getTotalViewsBySuscriptor)
            .POST("/create", viewController::createView)
            .build();
    }

    private RouterFunction<ServerResponse> searchHistoryRoutes(SearchHistoryController searchHistoryController) {
        return RouterFunctions
            .route()
            .GET("/auth", searchHistoryController::findByAuthUser)
            .POST("/create", searchHistoryController::createSearchHistory)
            .DELETE("/delete-by-ids", searchHistoryController::deleteSearchHistories)
            .DELETE("/{id}", searchHistoryController::deleteSearchHistory)
            .build();
    }

    private RouterFunction<ServerResponse> subscriptionRoutes(SubscriptionController subscriptionController) {
        return RouterFunctions
            .route()
            .GET("/auth", subscriptionController::findByAuthUser)
            .POST("/confirm", subscriptionController::confirm)
            .build();
    }

    private RouterFunction<ServerResponse> profileRoutes(ProfileController profileController) {
        return RouterFunctions
            .route()
            .GET("/auth", profileController::getProfileByAuth)
            .GET("/{id}", profileController::getProfileById)
            .POST("/create", profileController::createProfile)
            .PUT("/update", profileController::updateProfile)
            .build();
    }

    private RouterFunction<ServerResponse> predictionRoutes(PredictionController predictionController) {
        return RouterFunctions
            .route()
            .GET("/user-course-recomended", predictionController::getUserCourseRecomended)
            .GET("/courses-recomended-for-user", predictionController::getRecomendedCoursesByUser)
            .GET("/total-courses-recomended", predictionController::getTotalCoursesRecomended)
            .GET("/courses-recomended/auth", predictionController::getRecomendedCoursesByAuth)
            .GET("/courses-recomended/history/auth", predictionController::getRecomendedCoursesByHistoryAndAuth)
            .GET("/users-recomended/course/{courseId}", predictionController::getRecomendedUsersByCourse)
            .GET("/courses", predictionController::getAllCoursesWithAvgConfidence)
            .POST("/form-prediction", predictionController::predictCourseRecommendation)
            .build();
    }

    @Bean
    AuthController authController() {
        return new AuthController();
    }

    @Bean
    CategoryController categoryController() {
        return new CategoryController();
    }

    @Bean
    ContentController contentController() {
        return new ContentController();
    }

    @Bean
    CourseController courseController() {
        return new CourseController();
    }

    @Bean
    InstitutionController institutionController() {
        return new InstitutionController();
    }

    @Bean
    ReactionController reactionController() {
        return new ReactionController();
    }

    @Bean
    ReviewController reviewController() {
        return new ReviewController();
    }

    @Bean
    UserController userController() {
        return new UserController();
    }
    
    @Bean
    RoleController roleController() {
        return new RoleController();
    }

    @Bean
    ViewController viewController() {
        return new ViewController();
    }

    @Bean
    SearchHistoryController searchHistoryController() {
        return new SearchHistoryController();
    }

    @Bean
    SubscriptionController subscriptionController() {
        return new SubscriptionController();
    }

    @Bean
    ProfileController profileController() {
        return new ProfileController();
    }

    @Bean
    PredictionController predictionController() {
        return new PredictionController();
    }
}
