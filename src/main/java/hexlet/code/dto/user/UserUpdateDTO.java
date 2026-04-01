package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record UserUpdateDTO(
    JsonNullable<String> firstName,
    JsonNullable<String> lastName,

    @Email
    JsonNullable<String> email,

    @Size(min = 3)
    JsonNullable<String> password
) { }
