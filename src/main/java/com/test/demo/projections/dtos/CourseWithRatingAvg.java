package com.test.demo.projections.dtos;

public class CourseWithRatingAvg {
    private String id;
    private String title;
    private Double avgRating;
    
    public CourseWithRatingAvg(String id, String title, Double avgRating) {
        this.id = id;
        this.title = title;
        this.avgRating = avgRating;
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

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double rating) {
        this.avgRating = rating;
    }
}
