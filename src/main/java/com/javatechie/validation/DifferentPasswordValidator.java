package com.javatechie.validation;

import com.javatechie.dto.ChangePasswordRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentPasswordValidator implements ConstraintValidator<DifferentPassword, ChangePasswordRequestDTO> {

    @Override
    public void initialize(DifferentPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(ChangePasswordRequestDTO value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String currentPassword = value.getCurrentPassword();
        String newPassword = value.getNewPassword();

        if (currentPassword == null || !currentPassword.equals(newPassword)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("New password must be different from the old password")
                .addPropertyNode("newPassword")
                .addConstraintViolation();

        return false;
    }
}