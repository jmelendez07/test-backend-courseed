package com.test.demo.projections.dtos;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;
import com.test.demo.projections.validators.groups.ThirdValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@GroupSequence({ SaveProfileDto.class, FirstValidationGroup.class, SecondValidationGroup.class, ThirdValidationGroup.class })
public class SaveProfileDto {

    @NotBlank(message = "Debes completar el campo correspondiente al nivel de conocimiento.", groups = FirstValidationGroup.class)
    private String knowledgeLevel;

    @NotNull(message = "Para proceder, debes completar el campo correspondiente a las horas de estudio.", groups = { FirstValidationGroup.class })
    @Min(value = 0, message = "Las horas de estudio deben ser mayores o iguales a 0.", groups = { SecondValidationGroup.class })
    @Max(value = 8, message = "Las horas de estudio deben ser menores o iguales a 8.", groups = { ThirdValidationGroup.class })
    private int availableHoursTime;

    @NotBlank(message = "Debes completar el campo correspondiente a la modalidad.", groups = FirstValidationGroup.class)
    private String platformPrefered;

    @NotNull(message = "Debes completar el campo correspondiente al presupuesto aproximado.", groups = FirstValidationGroup.class)
    @DecimalMin(value = "0.0", message = "No podemos aceptar un presupuesto aproximado menor a 0.0, Revisa el valor ingresado.", groups = SecondValidationGroup.class)
    private Double budget;

    @NotBlank(message = "Debes completar el campo correspondiente al interes.", groups = FirstValidationGroup.class)
    private String interest;
 
    public String getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(String knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }

    public int getAvailableHoursTime() {
        return availableHoursTime;
    }

    public void setAvailableHoursTime(int availableHoursTime) {
        this.availableHoursTime = availableHoursTime;
    }

    public String getPlatformPrefered() {
        return platformPrefered;
    }

    public void setPlatformPrefered(String platformPrefered) {
        this.platformPrefered = platformPrefered;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
