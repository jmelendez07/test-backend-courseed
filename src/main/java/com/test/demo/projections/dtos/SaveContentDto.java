package com.test.demo.projections.dtos;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

public class SaveContentDto implements Serializable {

    @NotBlank(message = "Es importante que selecciones un curso antes de continuar.")
    private String courseId;   

    @NotBlank(message = "Para proceder, debes completar el campo correspondiente a la descripción del contenido.")
    private String description;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
