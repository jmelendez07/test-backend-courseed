package com.test.demo.persistence.documents;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_course_recomended")
public class UserCourseRecomended {

    @Id
    private String id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userProfileId;
    private String courseId;
    private int courseHours;
    private int ratingAvg;
    private String maxReaction;
    private int totalViews;
    private int reviewsCount;
    private boolean recomended;
    private String confidence;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getCourseHours() {
        return courseHours;
    }

    public void setCourseHours(int courseHours) {
        this.courseHours = courseHours;
    }

    public int getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(int ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public String getMaxReaction() {
        return maxReaction;
    }

    public void setMaxReaction(String maxReaction) {
        this.maxReaction = maxReaction;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public boolean isRecomended() {
        return recomended;
    }

    public void setRecomended(boolean recomended) {
        this.recomended = recomended;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }
}
