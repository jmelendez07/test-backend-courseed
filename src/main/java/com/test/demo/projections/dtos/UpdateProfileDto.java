package com.test.demo.projections.dtos;

import java.time.LocalDateTime;

import com.test.demo.projections.validators.groups.FirstValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@GroupSequence({ UpdateProfileDto.class, FirstValidationGroup.class })
public class UpdateProfileDto {
    @NotBlank(message = "Debes completar el campo correspondiente al nivel academico.", groups = FirstValidationGroup.class)
    private String academicLevel;

    @NotBlank(message = "Debes completar el campo correspondiente al sexo.", groups = FirstValidationGroup.class)
    private String sex;

    @NotNull(message = "Debes completar el campo correspondiente a la fecha de nacimiento.", groups = FirstValidationGroup.class)
    private LocalDateTime birthdate;

    public String getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(String academicLevel) {
        this.academicLevel = academicLevel;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public LocalDateTime getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDateTime birthdate) {
        this.birthdate = birthdate;
    }

}
