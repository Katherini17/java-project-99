package hexlet.code.dto.taskstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

@Schema(description = "Data for updating a task status")
public record TaskStatusUpdateDTO(
        @Size(min = 1)
        @Schema(example = "On Hold")
        JsonNullable<String> name,

        @Size(min = 1)
        @Schema(example = "on_hold")
        JsonNullable<String> slug
) { }
