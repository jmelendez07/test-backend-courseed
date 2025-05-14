package com.test.demo.projections.dtos;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;

public class SaveReactionDto implements Serializable {
    @NotBlank(message = "Es importante que selecciones un curso antes de continuar.")
    private String courseId;

    @NotBlank(message = "Es importante que selecciones un curso antes de continuar.")
    private String type;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
}
