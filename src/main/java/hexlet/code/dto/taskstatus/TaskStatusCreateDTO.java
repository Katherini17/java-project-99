package hexlet.code.dto.taskstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data for creating a new task status")
public record TaskStatusCreateDTO(
        @NotBlank
        @Size(min = 1)
        @Schema(example = "Archived")
        String name,

        @NotBlank
        @Size(min = 1)
        @Schema(example = "archived")
        String slug
) { }
