package hexlet.code.dto.taskstatus;

import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;
public record TaskStatusUpdateDTO(

    @Size(min = 1)
    JsonNullable<String> name,

    @Size(min = 1)
    JsonNullable<String> slug

) { }
