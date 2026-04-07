package hexlet.code.dto.taskstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Task status details")
public record TaskStatusDTO(
        @Schema(example = "1")
        Long id,

        @Schema(example = "To Review")
        String name,

        @Schema(example = "to_review")
        String slug,

        @Schema(example = "2026-01-01T00:00:00Z")
        Instant createdAt
) { }
