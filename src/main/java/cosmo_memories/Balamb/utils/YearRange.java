package cosmo_memories.Balamb.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.*;
import java.time.Year;

@Documented
@Constraint(validatedBy = YearRange.YearRangeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface YearRange {

    String message() default "Year must be between 0000 and today.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    class YearRangeValidator implements ConstraintValidator<YearRange, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null || value.isBlank()) {
                return true;
            }

            try {
                int year = Integer.parseInt(value);
                int currentYear = Year.now().getValue();
                return year >= 0 && year <= currentYear;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
