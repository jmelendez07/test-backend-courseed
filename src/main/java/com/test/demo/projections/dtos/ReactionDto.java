package com.test.demo.projections.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReactionDto implements Serializable {
    private String id;
    private UserDto user;
    private CourseDto course;
    private String type;
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public UserDto getUser() {
        return user;
    }
    public void setUser(UserDto user) {
        this.user = user;
    }
    public CourseDto getCourse() {
        return course;
    }
    public void setCourse(CourseDto course) {
        this.course = course;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
