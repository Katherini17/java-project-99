package hexlet.code.component;

import hexlet.code.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("taskUtils")
@RequiredArgsConstructor
public class TaskUtils {

    private final TaskRepository taskRepository;

    public boolean isAssignee(Long taskId, String email) {
        return taskRepository.findById(taskId)
                .map(task -> {
                    var assignee = task.getAssignee();
                    return assignee != null && assignee.getEmail().equals(email);
                })
                .orElse(false);
    }
}
