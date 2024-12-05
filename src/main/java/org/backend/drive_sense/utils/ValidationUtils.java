package org.backend.drive_sense.utils;

import java.util.Set;

import jakarta.validation.ConstraintViolation;


public class ValidationUtils {

    public static <T> String getValidationErrors(Set<ConstraintViolation<T>> constraintViolations) {
        StringBuilder errors = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            errors.append(constraintViolation.getPropertyPath()).append(": ")
                    .append(constraintViolation.getMessage()).append("\n");
        }
        return errors.toString();
    }
}
