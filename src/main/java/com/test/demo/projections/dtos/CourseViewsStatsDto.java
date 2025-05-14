package com.test.demo.projections.dtos;

public class CourseViewsStatsDto {
    private String courseId;
    private long lastMonthViews;
    private long currentMonthViews;

    public CourseViewsStatsDto(String courseId, long lastMonthViews, long currentMonthViews) {
        this.courseId = courseId;
        this.lastMonthViews = lastMonthViews;
        this.currentMonthViews = currentMonthViews;
    }

    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    public long getLastMonthViews() {
        return lastMonthViews;
    }
    public void setLastMonthViews(long lastMonthViews) {
        this.lastMonthViews = lastMonthViews;
    }
    public long getCurrentMonthViews() {
        return currentMonthViews;
    }
    public void setCurrentMonthViews(long currentMonthViews) {
        this.currentMonthViews = currentMonthViews;
    }
}
