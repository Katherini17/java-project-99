package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    private static final String TASK_STATUS_NOT_FOUND_MESSAGE = "Task status with id %d not found";

    public List<TaskStatusDTO> getAll() {
        return taskStatusMapper.map(taskStatusRepository.findAll());
    }

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        TaskStatus taskStatus = taskStatusMapper.map(taskStatusData);

        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    public TaskStatusDTO findById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_STATUS_NOT_FOUND_MESSAGE.formatted(id)));

        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_STATUS_NOT_FOUND_MESSAGE.formatted(id)));
        taskStatusMapper.update(taskStatusData, taskStatus);

        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    public void delete(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_STATUS_NOT_FOUND_MESSAGE.formatted(id)));

        taskStatusRepository.delete(taskStatus);
    }

}
