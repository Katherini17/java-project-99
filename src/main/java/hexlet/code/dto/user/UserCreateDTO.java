package hexlet.code.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data for creating a new user")
public record UserCreateDTO(
        @Schema(example = "Ivan")
        String firstName,

        @Schema(example = "Ivanov")
        String lastName,

        @Email
        @NotBlank
        @Schema(example = "ivan@example.com")
        String email,

        @NotBlank
        @Size(min = 3)
        @Schema(example = "password123")
        String password
) { }
