package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record TaskCreateDTO(
    Integer index,

    @JsonProperty("assignee_id")
    Long assigneeId,

    @NotBlank
    @Size(min = 1)
    String title,

    String content,

    @NotBlank
    String status,

    Set<Long> taskLabelIds
) { }

