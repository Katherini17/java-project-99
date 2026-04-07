package hexlet.code.dto.label;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Label information")
public record LabelDTO(
        @Schema(example = "1")
        Long id,

        @Schema(example = "feature")
        String name,

        @Schema(example = "2026-01-01T00:00:00Z") // 1 января 2026
        Instant createdAt
) { }



