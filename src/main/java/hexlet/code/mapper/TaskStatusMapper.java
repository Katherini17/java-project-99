package hexlet.code.mapper;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(
        config = CentralConfig.class
)
public abstract class TaskStatusMapper {

    public abstract TaskStatus map(TaskStatusCreateDTO dto);
    public abstract TaskStatusDTO map(TaskStatus model);
    public abstract List<TaskStatusDTO> map(List<TaskStatus> models);
    public abstract void update(TaskStatusUpdateDTO dto, @MappingTarget TaskStatus model);
}
