package hexlet.code.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "User details")
public record UserDTO(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Ivan")
        String firstName,

        @Schema(example = "Ivanov")
        String lastName,

        @Schema(example = "ivan@example.com")
        String email,

        @Schema(example = "2026-01-01T00:00:00Z")
        Instant createdAt
) { }
