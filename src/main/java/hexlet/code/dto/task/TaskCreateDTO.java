package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(description = "Data for creating a new task")
public record TaskCreateDTO(
        @Schema(example = "3")
        Integer index,

        @JsonProperty("assignee_id")
        @Schema(example = "1")
        Long assigneeId,

        @NotBlank
        @Size(min = 1)
        @Schema(example = "Fix bug")
        String title,

        @Schema(example = "Something does not work")
        String content,

        @NotBlank
        @Schema(example = "to_be_fixed")
        String status,

        @Schema(example = "[1, 2]")
        Set<Long> taskLabelIds
) { }

