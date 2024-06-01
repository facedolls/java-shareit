package ru.practicum.shareit.booking.dto.validator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidBookingCreateImpl.class)
public @interface ValidBookingCreate {
    String message() default "Start date and time must not be later than end date and time. " +
            "Start date and time and end date and time must not be in past. " +
            "Start date and time and end date and time must not be equal";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "";
}
