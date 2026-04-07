package hexlet.code.dto.label;

import java.time.Instant;

public record LabelDTO(
        Long id,
        String name,
        Instant createdAt
) { }



