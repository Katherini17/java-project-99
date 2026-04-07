package hexlet.code.dto.label;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data for creating a new label")
public record LabelCreateDTO(
        @NotBlank
        @Size(min = 3, max = 1000)
        @Schema(example = "bug")
        String name
) { }
