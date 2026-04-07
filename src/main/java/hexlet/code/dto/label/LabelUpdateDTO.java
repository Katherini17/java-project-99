package hexlet.code.dto.label;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

@Schema(description = "Data for updating a label")
public record LabelUpdateDTO(
        @NotBlank
        @Size(min = 3, max = 1000)
        @Schema(example = "improvement")
        JsonNullable<String> name
) { }
