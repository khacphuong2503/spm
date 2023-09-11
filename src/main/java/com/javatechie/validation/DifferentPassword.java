package com.javatechie.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DifferentPasswordValidator.class)
public @interface DifferentPassword {
    String message() default "New password must be different from the current password.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
