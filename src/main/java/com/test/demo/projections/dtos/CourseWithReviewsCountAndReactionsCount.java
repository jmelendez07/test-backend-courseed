package com.test.demo.projections.dtos;

public class CourseWithReviewsCountAndReactionsCount {
    private String id;
    private String title;
    private Long totalReviews;
    private Long totalReactions;

    public CourseWithReviewsCountAndReactionsCount(String id, String title, Long totalReviews, Long totalReactions) {
        this.id = id;
        this.title = title;
        this.totalReviews = totalReviews;
        this.totalReactions = totalReactions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Long getTotalReactions() {
        return totalReactions;
    }

    public void setTotalReactions(Long totalReactions) {
        this.totalReactions = totalReactions;
    }
}
