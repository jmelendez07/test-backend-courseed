package com.test.demo.projections.dtos;

public class InstitutionWithCoursesCountDto {
    private String id;
    private String name;
    private Long totalCourses;

    public InstitutionWithCoursesCountDto(String id, String name, Long totalCourses) {
        this.id = id;
        this.name = name;
        this.totalCourses = totalCourses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
    }
}
