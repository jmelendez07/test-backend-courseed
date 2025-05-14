package com.test.demo.projections.dtos;

import java.io.Serializable;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;
import com.test.demo.projections.validators.groups.ThirdValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@GroupSequence({ UpdateReviewDto.class, FirstValidationGroup.class, SecondValidationGroup.class, ThirdValidationGroup.class })
public class UpdateReviewDto implements Serializable {

    @NotBlank(message = "Para proceder, debes completar el campo del contenido de la reseña.", groups = { FirstValidationGroup.class })
    private String content;

    @NotNull(message = "Para proceder, debes completar el campo de la calificación.", groups = { FirstValidationGroup.class })
    @Min(value = 1, message = "La calificación debe ser mayor o igual a 1.", groups = { SecondValidationGroup.class })
    @Max(value = 5, message = "La calificación debe ser menor o igual a 5.", groups = { ThirdValidationGroup.class })
    private int rating;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
}