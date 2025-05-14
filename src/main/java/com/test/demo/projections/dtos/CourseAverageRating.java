package com.test.demo.projections.dtos;

public class CourseAverageRating {
    private String courseId;
    private double avgRating;
    
    public CourseAverageRating(String courseId, double avgRating) {
        this.courseId = courseId;
        this.avgRating = avgRating;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    
}