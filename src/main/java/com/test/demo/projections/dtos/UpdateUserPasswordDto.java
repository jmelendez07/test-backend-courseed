package com.test.demo.projections.dtos;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@GroupSequence({ UpdateUserPasswordDto.class, FirstValidationGroup.class, SecondValidationGroup.class })
public class UpdateUserPasswordDto {
    
    @NotBlank(message = "Para proceder, debes completar el campo correspondiente a la contraseña del usuario.", groups = FirstValidationGroup.class)
    @Size(min = 8, max = 20, message = "Es necesario que la contraseña que elijas tenga un mínimo de 8 y un máximo de 20 caracteres.", groups = SecondValidationGroup.class)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
