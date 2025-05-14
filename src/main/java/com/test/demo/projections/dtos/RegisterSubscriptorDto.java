package com.test.demo.projections.dtos;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@GroupSequence({ RegisterSubscriptorDto.class, FirstValidationGroup.class, SecondValidationGroup.class })
public class RegisterSubscriptorDto {
    @NotBlank(message = "Debes completar el campo correspondiente al correo electrónico.", groups = FirstValidationGroup.class)
    @Email(message = "Asegúrate de que el correo electrónico proporcionado sea correcto y válido.", groups = SecondValidationGroup.class)
    private String email;

    @NotBlank(message = "Debes completar el campo correspondiente a la contraseña.", groups = FirstValidationGroup.class)
    @Size(min = 8, max = 20, message = "Es necesario que la contraseña que elijas tenga un mínimo de 8 y un máximo de 20 caracteres.", groups = SecondValidationGroup.class)
    private String password;

    @NotBlank(message = "Debes completar el campo correspondiente a la confirmación de contraseña.", groups = FirstValidationGroup.class)
    private String confirmPassword;

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
