package hexlet.code.dto.taskstatus;

import java.time.Instant;

public record TaskStatusDTO(

        Long id,
        String name,
        String slug,
        Instant createdAt

) { }
