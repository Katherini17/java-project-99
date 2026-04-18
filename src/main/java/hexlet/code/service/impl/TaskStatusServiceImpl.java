package hexlet.code.service.impl;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
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
public class TaskStatusServiceImpl implements TaskStatusService {
    private final LabelRepository labelRepository;

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusMapper taskStatusMapper;

    private static final String TASK_STATUS_NOT_FOUND_MESSAGE = "Task status with id %d not found";

    public Page<TaskStatusDTO> getAll(Pageable pageable) {
        return taskStatusRepository.findAll(pageable)
                .map(taskStatusMapper::map);
    }

    public TaskStatusDTO getById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_STATUS_NOT_FOUND_MESSAGE.formatted(id)));

        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusData) {
        var taskStatus = taskStatusMapper.map(taskStatusData);
        var savedStatus = taskStatusRepository.save(taskStatus);

        log.info("Task status created with id: {}", savedStatus.getId());
        return taskStatusMapper.map(savedStatus);
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

        taskStatusRepository.delete(taskStatus);
        log.info("Task status with id {} deleted", id);
    }


    /**
     * Finds status with pessimistic lock for safe update/delete.
     */
    private TaskStatus getStatusForUpdate(Long id) {
        return taskStatusRepository.findWithLockById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_STATUS_NOT_FOUND_MESSAGE.formatted(id)));
    }
}
