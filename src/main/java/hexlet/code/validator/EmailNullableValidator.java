package hexlet.code.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;

public class EmailNullableValidator implements ConstraintValidator<EmailNullable, JsonNullable<String>> {

    private static final String EMAIL_PATTERN = """
                ^(?>[\\p{L}0-9._%+-]{1,64})@\
                (?>[\\p{L}0-9.-]+\\.[\\p{L}]{2,})$\
                """;

    @Override
    public boolean isValid(JsonNullable<String> value, ConstraintValidatorContext context) {

        if (value == null || !value.isPresent()) {
            return true;
        }

        return Optional.ofNullable(value.get())
                .filter(email -> !email.isBlank())
                .map(email -> email.matches(EMAIL_PATTERN))
                .orElse(false);
    }

}
