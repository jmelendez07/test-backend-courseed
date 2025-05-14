package com.test.demo.projections.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LikeDto implements Serializable {
    private String id;
    private CourseDto course;
    private UserDto user;
    private LocalDateTime createdAt;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public CourseDto getCourse() {
        return course;
    }
    public void setCourse(CourseDto course) {
        this.course = course;
    }
    public UserDto getUser() {
        return user;
    }
    public void setUser(UserDto user) {
        this.user = user;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
