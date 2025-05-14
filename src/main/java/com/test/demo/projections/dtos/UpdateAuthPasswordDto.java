package com.test.demo.projections.dtos;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@GroupSequence({ UpdateAuthPasswordDto.class, FirstValidationGroup.class, SecondValidationGroup.class })
public class UpdateAuthPasswordDto {

    @NotBlank(message = "Para proceder, debes completar el campo correspondiente a la contraseña actual del usuario.", groups = FirstValidationGroup.class)
    private String currentPassword;

    @NotBlank(message = "Para proceder, debes completar el campo correspondiente a la nueva contraseña del usuario.", groups = FirstValidationGroup.class)
    @Size(min = 8, max = 20, message = "Es necesario que la contraseña que elijas tenga un mínimo de 8 y un máximo de 20 caracteres.", groups = SecondValidationGroup.class)
    private String newPassword;

    @NotBlank(message = "Para proceder, debes completar el campo correspondiente a la confirmación de la nueva contraseña del usuario.", groups = FirstValidationGroup.class)
    private String confirmNewPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    
    
}
