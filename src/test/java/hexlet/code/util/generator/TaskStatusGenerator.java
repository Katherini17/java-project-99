package hexlet.code.util.generator;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.util.ModelUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
@RequiredArgsConstructor
public class TaskStatusGenerator {

    private Model<TaskStatus> model;
    private Model<TaskStatusCreateDTO> createDTO;
    private Model<TaskStatusUpdateDTO> updateDTO;

    private final Faker faker;

    private static final AtomicLong COUNTER = new AtomicLong(1);

    @PostConstruct
    public void init() {
        model = buildModel();
        createDTO = buildCreateDTO();
        updateDTO = ModelUtils.buildUpdateModel(TaskStatusUpdateDTO.class);

    }

    private Model<TaskStatus> buildModel() {
        return Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(Select.field(TaskStatus::getName), () -> faker.lorem().word())
                .supply(
                        Select.field(TaskStatus::getSlug),
                        () -> "%s_%d".formatted(faker.internet().slug(), COUNTER.getAndIncrement())
                )
                .toModel();
    }

    private Model<TaskStatusCreateDTO> buildCreateDTO() {
        return Instancio.of(TaskStatusCreateDTO.class)
                .supply(Select.field(TaskStatusCreateDTO::name), () -> faker.lorem().word())
                .supply(Select.field(TaskStatusCreateDTO::slug), () -> faker.internet().slug())
                .toModel();
    }
}
