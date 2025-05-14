package com.test.demo.projections.dtos;

import java.io.Serializable;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@GroupSequence({ LoginUserDto.class, FirstValidationGroup.class, SecondValidationGroup.class })
public class LoginUserDto implements Serializable {

    @NotBlank(message = "Debes completar el campo correspondiente al correo electrónico del usuario.", groups = FirstValidationGroup.class)
    @Email(message = "Asegúrate de que el correo electrónico proporcionado sea correcto y válido.", groups = SecondValidationGroup.class)
    private String email;

    @NotBlank(message = "Debes completar el campo correspondiente a la contraseña del usuario.", groups = FirstValidationGroup.class)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
