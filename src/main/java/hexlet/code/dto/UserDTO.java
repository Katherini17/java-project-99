package hexlet.code.dto;

import java.time.Instant;

public record UserDTO(
    Long id,
    String firstName,
    String lastName,
    String email,
    Instant createdAt
) { }
