package com.test.demo.projections.dtos;

import java.io.Serializable;

public class ContentDto implements Serializable {
    private String id;
    private String description;
    private String courseId;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    
}
