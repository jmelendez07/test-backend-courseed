package com.test.demo.projections.dtos;

import java.util.List;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;
import com.test.demo.projections.validators.groups.ThirdValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@GroupSequence({ CreateUserDto.class, FirstValidationGroup.class, SecondValidationGroup.class, ThirdValidationGroup.class })
public class CreateUserDto {
    @NotBlank(message = "Para proceder, debes completar el campo correspondiente al correo electrónico del usuario.", groups = FirstValidationGroup.class)
    @Email(message = "Asegúrate de que el correo electrónico proporcionado sea correcto y válido.", groups = SecondValidationGroup.class)
    private String email;

    @NotBlank(message = "Para proceder, debes completar el campo correspondiente a la contraseña del usuario.", groups = FirstValidationGroup.class)
    @Size(min = 8, max = 20, message = "Es necesario que la contraseña que elijas tenga un mínimo de 8 y un máximo de 20 caracteres.", groups = SecondValidationGroup.class)
    private String password;

    @NotBlank(message = "Para proceder, debes completar el campo correspondiente a la confirmación de contraseña del usuario.", groups = FirstValidationGroup.class)
    private String confirmPassword;

    @NotEmpty(message = "Es importante que selecciones los roles antes de continuar.", groups = FirstValidationGroup.class)
    @Size(min = 1, message = "Es importante que selecciones los roles antes de continuar.", groups = SecondValidationGroup.class)
    private List<@NotBlank(message = "Es importante que selecciones los roles antes de continuar.", groups = ThirdValidationGroup.class) String> roles;

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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
