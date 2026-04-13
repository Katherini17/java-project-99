package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskStatusService {
    Page<TaskStatusDTO> getAll(Pageable pageable);
    TaskStatusDTO getById(Long id);
    TaskStatusDTO create(TaskStatusCreateDTO taskStatusData);
    TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id);
    void delete(Long id);
}
