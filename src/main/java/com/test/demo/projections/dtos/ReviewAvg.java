package com.test.demo.projections.dtos;

public class ReviewAvg {
    private String courseId;
    private Double rating;

    public ReviewAvg() {}
    
    public ReviewAvg(String courseId, Double rating) {
        this.courseId = courseId;
        this.rating = rating;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    } 
}
