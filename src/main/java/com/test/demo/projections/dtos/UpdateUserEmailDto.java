package com.test.demo.projections.dtos;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@GroupSequence({ UpdateUserEmailDto.class, FirstValidationGroup.class, SecondValidationGroup.class })
public class UpdateUserEmailDto {

    @NotBlank(message = "Para proceder, debes completar el campo correspondiente al correo electrónico del usuario.", groups = FirstValidationGroup.class)
    @Email(message = "Asegúrate de que el correo electrónico proporcionado sea correcto y válido.", groups = SecondValidationGroup.class)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
