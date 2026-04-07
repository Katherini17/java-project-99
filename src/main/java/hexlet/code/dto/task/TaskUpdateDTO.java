package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Schema(description = "Data for updating a task")
public record TaskUpdateDTO(
        @Schema(example = "2")
        JsonNullable<Integer> index,

        @JsonProperty("assignee_id")
        @Schema(example = "1")
        JsonNullable<Long> assigneeId,

        @Size(min = 1)
        @Schema(example = "Update task")
        JsonNullable<String> title,

        @Schema(example = "New description")
        JsonNullable<String> content,

        @Schema(example = "published")
        JsonNullable<String> status,

        @Schema(example = "[]")
        JsonNullable<Set<Long>> taskLabelIds
) { }
