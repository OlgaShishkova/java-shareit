package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookingDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDtoInput, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDtoInput.getStart();
        LocalDateTime end = bookingDtoInput.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
