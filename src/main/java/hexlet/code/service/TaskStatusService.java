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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusMapper taskStatusMapper;

    private static final String TASK_STATUS_NOT_FOUND_MESSAGE = "Task status with id %d not found";
    private static final String TASK_STATUS_LINKED_MESSAGE =
            "Cannot delete this status: it is assigned to one or more tasks";

    public Page<TaskStatusDTO> getAll(Pageable pageable) {
        return taskStatusRepository.findAll(pageable)
                .map(taskStatusMapper::map);
    }

    public TaskStatusDTO findById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_STATUS_NOT_FOUND_MESSAGE.formatted(id)));

        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);

        log.info("Task status created: {}", taskStatus.getName());
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusData, Long id) {
        var taskStatus = getStatusForUpdate(id);
        taskStatusMapper.update(taskStatusData, taskStatus);

        log.info("Task status with id {} updated", id);
        return taskStatusMapper.map(taskStatusRepository.save(taskStatus));
    }

    @Transactional
    public void delete(Long id) {
        var taskStatus = getStatusForUpdate(id);

        if (taskRepository.existsByTaskStatusId(id)) {
            throw new UnprocessableEntityException(TASK_STATUS_LINKED_MESSAGE);
        }

        taskStatusRepository.delete(taskStatus);
        log.info("Task status with id {} deleted", id);
    }

    private TaskStatus getStatusForUpdate(Long id) {
        return taskStatusRepository.findWithLockById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_STATUS_NOT_FOUND_MESSAGE.formatted(id)));
    }
}
