package hexlet.code.component.converter;

import hexlet.code.exception.UnprocessableEntityException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskStatusConverter {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatus toEntity(String slug) {
        return Optional.ofNullable(slug)
                .flatMap(taskStatusRepository::findBySlug)
                .orElseThrow(() -> new UnprocessableEntityException(
                        "TaskStatus with slug: %s not found".formatted(slug)
                ));
    }
}

