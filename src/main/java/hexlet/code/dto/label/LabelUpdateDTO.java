package hexlet.code.dto.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record LabelUpdateDTO(
        @NotBlank
        @Size(min = 3, max = 1000)
        JsonNullable<String> name
) { }
