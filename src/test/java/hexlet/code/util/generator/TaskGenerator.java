package hexlet.code.util.generator;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.util.ModelUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Getter
@Component
@RequiredArgsConstructor
public class TaskGenerator {

    private Model<Task> model;
    private Model<TaskCreateDTO> createDTO;
    private Model<TaskUpdateDTO> updateDTO;

    private final Faker faker;

    @PostConstruct
    public void init() {
        model = buildModel();
        createDTO = buildCreateDTO();
        updateDTO = ModelUtils.buildUpdateModel(TaskUpdateDTO.class);

    }

    private Model<Task> buildModel() {
        return Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getUpdatedAt))
                .ignore(Select.field(Task::getCreatedAt))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getTaskStatus))
                .supply(Select.field(Task::getName), () -> faker.lorem().sentence(3))
                .supply(Select.field(Task::getDescription), () -> faker.lorem().paragraph())
                .supply(Select.field(Task::getIndex), () -> faker.number().positive())
                .supply(Select.field(Task::getLabels), () -> new HashSet<>())
                .toModel();
    }

    private Model<TaskCreateDTO> buildCreateDTO() {
        return Instancio.of(TaskCreateDTO.class)
                .supply(Select.field(TaskCreateDTO::title), () -> faker.lorem().sentence(3))
                .supply(Select.field(TaskCreateDTO::content), () -> faker.lorem().paragraph())
                .supply(Select.field(TaskCreateDTO::index), () -> faker.number().positive())
                .supply(Select.field(TaskCreateDTO::taskLabelIds), () -> new HashSet<>())
                .toModel();
    }
}
