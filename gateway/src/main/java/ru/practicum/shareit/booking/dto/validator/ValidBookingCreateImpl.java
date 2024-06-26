package ru.practicum.shareit.booking.dto.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Slf4j
public class ValidBookingCreateImpl implements ConstraintValidator<ValidBookingCreate, BookingDtoCreate> {
    private final LocalDateTime currentTime = LocalDateTime.now();

    @Override
    public void initialize(ValidBookingCreate constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoCreate booking, ConstraintValidatorContext constraintValidatorContext) {
        if (booking.getItemId() < 1) {
            log.warn("Item id is less than 1 and equal={}", booking.getItemId());
            return false;
        } else if (booking.getStart() == null || booking.getEnd() == null
                || booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().isBefore(currentTime)
                || booking.getStart().isEqual(booking.getEnd())
                || booking.getEnd().isBefore(currentTime)) {
            log.warn("Start time={} of booking is after end time={}, or it's before current time={}",
                    booking.getStart(), booking.getEnd(), currentTime);
            return false;
        }
        return true;
    }
}
