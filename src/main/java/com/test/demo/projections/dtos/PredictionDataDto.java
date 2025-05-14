package com.test.demo.projections.dtos;

import java.io.Serializable;

public class PredictionDataDto implements Serializable {
    private String userInterest;
    private Double userAvailableTime;
    private int budget;
    private String platformPreference;
    private String courseModality;
    private int courseDuration;
    private Double coursePrice;
    private String courseCategory;
    private Double courseRatingAvg;
    private String courseMaxReaction;
    private int courseVisits;
    private int courseReviewsCount;
    private Boolean courseRecomended;
    private String confidence;
    public String getUserInterest() {
        return userInterest;
    }
    public void setUserInterest(String userInterest) {
        this.userInterest = userInterest;
    }
    public Double getUserAvailableTime() {
        return userAvailableTime;
    }
    public void setUserAvailableTime(Double userAvailableTime) {
        this.userAvailableTime = userAvailableTime;
    }
    public int getBudget() {
        return budget;
    }
    public void setBudget(int budget) {
        this.budget = budget;
    }
    public String getPlatformPreference() {
        return platformPreference;
    }
    public void setPlatformPreference(String platformPreference) {
        this.platformPreference = platformPreference;
    }
    public String getCourseModality() {
        return courseModality;
    }
    public void setCourseModality(String courseModality) {
        this.courseModality = courseModality;
    }
    public int getCourseDuration() {
        return courseDuration;
    }
    public void setCourseDuration(int courseDuration) {
        this.courseDuration = courseDuration;
    }
    public Double getCoursePrice() {
        return coursePrice;
    }
    public void setCoursePrice(Double coursePrice) {
        this.coursePrice = coursePrice;
    }
    public String getCourseCategory() {
        return courseCategory;
    }
    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }
    public Double getCourseRatingAvg() {
        return courseRatingAvg;
    }
    public void setCourseRatingAvg(Double courseRatingAvg) {
        this.courseRatingAvg = courseRatingAvg;
    }
    public String getCourseMaxReaction() {
        return courseMaxReaction;
    }
    public void setCourseMaxReaction(String courseMaxReaction) {
        this.courseMaxReaction = courseMaxReaction;
    }
    public int getCourseVisits() {
        return courseVisits;
    }
    public void setCourseVisits(int courseVisits) {
        this.courseVisits = courseVisits;
    }
    public int getCourseReviewsCount() {
        return courseReviewsCount;
    }
    public void setCourseReviewsCount(int courseReviewsCount) {
        this.courseReviewsCount = courseReviewsCount;
    }
    public Boolean getCourseRecomended() {
        return courseRecomended;
    }
    public void setCourseRecomended(Boolean courseRecomended) {
        this.courseRecomended = courseRecomended;
    }
    public String getConfidence() {
        return confidence;
    }
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    
}
