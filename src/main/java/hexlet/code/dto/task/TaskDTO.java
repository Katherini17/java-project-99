package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Set;

public record TaskDTO(
    Long id,
    Integer index,
    Instant createdAt,

    @JsonProperty("assignee_id")
    Long assigneeId,

    String title,
    String content,
    String status,

    @JsonProperty("task_labels_ids")
    Set<Long> taskLabelIds
) { }
