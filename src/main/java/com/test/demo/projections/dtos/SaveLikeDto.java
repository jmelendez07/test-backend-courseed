package com.test.demo.projections.dtos;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

public class SaveLikeDto implements Serializable {
    @NotBlank(message = "Es importante que selecciones un curso antes de continuar.")
    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
}
