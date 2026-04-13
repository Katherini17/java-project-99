package hexlet.code.service;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Page<TaskDTO> getAll(TaskParamsDTO params, Pageable pageable);
    TaskDTO getById(Long id);
    TaskDTO create(TaskCreateDTO taskData);
    TaskDTO update(TaskUpdateDTO taskData, Long id);
    void delete(Long id);
}
