package hexlet.code.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

@Schema(description = "Data for updating a user")
public record UserUpdateDTO(
        @Schema(example = "Ivan")
        JsonNullable<String> firstName,

        @Schema(example = "Ivanov")
        JsonNullable<String> lastName,

        @Email
        @Schema(example = "ivan_new@example.com")
        JsonNullable<String> email,

        @Size(min = 3)
        @Schema(example = "new_password_123")
        JsonNullable<String> password
) { }
