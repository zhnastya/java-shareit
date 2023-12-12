package ru.practicum.shareit.booking.validators;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<BookingValidator, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(BookingValidator constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        LocalDateTime fieldValue = (LocalDateTime) new BeanWrapperImpl(o)
                .getPropertyValue(field);
        LocalDateTime fieldMatchValue = (LocalDateTime) new BeanWrapperImpl(o)
                .getPropertyValue(fieldMatch);
        return fieldValue != null
                && fieldMatchValue != null
                && fieldValue.isBefore(fieldMatchValue);
    }
}
