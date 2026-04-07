package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

public record TaskUpdateDTO(
        JsonNullable<Integer> index,

        @JsonProperty("assignee_id")
        JsonNullable<Long> assigneeId,

        @Size(min = 1)
        JsonNullable<String> title,

        JsonNullable<String> content,
        JsonNullable<String> status,

        JsonNullable<Set<Long>> taskLabelIds
) { }
