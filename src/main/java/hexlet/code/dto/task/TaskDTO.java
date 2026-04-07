package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;

@Schema(description = "Task details")
public record TaskDTO(
        @Schema(example = "5")
        Long id,

        @Schema(example = "1")
        Integer index,

        @Schema(example = "2026-01-01T00:00:00Z")
        Instant createdAt,

        @JsonProperty("assignee_id")
        @Schema(example = "1")
        Long assigneeId,

        @Schema(example = "Fix bug")
        String title,

        @Schema(example = "Something does not work")
        String content,

        @Schema(example = "in_progress")
        String status,

        @Schema(example = "[1, 2]")
        Set<Long> taskLabelIds
) { }
