package hexlet.code.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User credentials for authentication")
public record AuthRequest(
        @Schema(example = "ivan@example.com")
        String username,

        @Schema(example = "password123")
        String password
) { }
