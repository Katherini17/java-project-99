package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.exception.UnprocessableEntityException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusMapper taskStatusMapper;

    private static final String TASK_STATUS_NOT_FOUND_MESSAGE = "Task status with id %d not found";
    private static final String STATUS_LINKED_TO_TASKS_MESSAGE = "Cannot delete status that is used in tasks";

    public List<TaskStatusDTO> getAll() {
        return taskStatusMapper.map(taskStatusRepository.findAll());
    }

    public TaskStatusDTO findById(Long id) {
        return taskStatusMapper.map(findStatusById(id));
    }

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);

        log.info("Task status created: {}", taskStatus.getName());
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id) {
        var taskStatus = findStatusById(id);
        taskStatusMapper.update(taskStatusData, taskStatus);

        log.info("Task status with id {} updated", id);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    public void delete(Long id) {
        var taskStatus = findStatusById(id);

        if (taskRepository.existsByTaskStatusId(id)) {
            throw new UnprocessableEntityException(STATUS_LINKED_TO_TASKS_MESSAGE);
        }

        taskStatusRepository.delete(taskStatus);
        log.info("Task status with id {} deleted", id);
    }

    private TaskStatus findStatusById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_STATUS_NOT_FOUND_MESSAGE.formatted(id)));
    }

}
