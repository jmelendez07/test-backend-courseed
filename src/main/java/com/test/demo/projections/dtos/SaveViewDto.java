package com.test.demo.projections.dtos;

import jakarta.validation.constraints.NotBlank;

public class SaveViewDto {
    @NotBlank(message = "Es importante que selecciones un curso antes de continuar.")
    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
