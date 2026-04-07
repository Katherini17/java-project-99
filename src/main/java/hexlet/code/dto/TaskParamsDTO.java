package hexlet.code.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters for filtering tasks")
public record TaskParamsDTO(

        @Schema(description = "Substring in task name (case-insensitive)", example = "fix")
        String titleCont,

        @Schema(description = "Assignee identifier", example = "1")
        Long assigneeId,

        @Schema(description = "Task status slug", example = "to_review")
        String status,

        @Schema(description = "Label identifier", example = "1")
        Long labelId
) { }