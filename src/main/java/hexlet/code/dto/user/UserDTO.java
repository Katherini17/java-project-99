package hexlet.code.dto.user;

import java.time.Instant;

public record UserDTO(
    Long id,
    String firstName,
    String lastName,
    String email,
    Instant createdAt
) { }
