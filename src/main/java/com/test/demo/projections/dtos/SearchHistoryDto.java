package com.test.demo.projections.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class SearchHistoryDto {
    private String id;
    private UserDto user;
    private String search;
    private LocalDateTime createdAt;
    private List<CourseDto> courses;

    public SearchHistoryDto(String id, UserDto user, String search, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.search = search;
        this.createdAt = createdAt;
    }

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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CourseDto> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDto> courses) {
        this.courses = courses;
    }

        
}
