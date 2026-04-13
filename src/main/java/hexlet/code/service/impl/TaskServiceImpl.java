package hexlet.code.service.impl;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import hexlet.code.specification.TaskSpecification;
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
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    private static final String TASK_NOT_FOUND_MESSAGE = "Task with id %d not found";

    public Page<TaskDTO> getAll(TaskParamsDTO params, Pageable pageable) {
        var specification = taskSpecification.build(params);

        return taskRepository.findAll(specification, pageable)
                .map(taskMapper::map);
    }

    public TaskDTO getById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_MESSAGE.formatted(id)));

        return taskMapper.map(task);
    }

    @Transactional
    public TaskDTO create(TaskCreateDTO taskData) {
        var task = taskMapper.map(taskData);
        var savedTask = taskRepository.save(task);

        log.info("Task created with id: {}", savedTask.getId());
        return taskMapper.map(savedTask);
    }

    @Transactional
    public TaskDTO update(TaskUpdateDTO taskData, Long id) {
        var task = getTaskForUpdate(id);
        taskMapper.update(taskData, task);

        log.info("Task with id {} updated", id);
        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id) {
        var task = getTaskForUpdate(id);

        taskRepository.delete(task);
        log.info("Task with id {} deleted", id);
    }

    /**
     * Finds task with pessimistic lock for safe update/delete.
     */
    private Task getTaskForUpdate(Long id) {
        return taskRepository.findWithLockById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_MESSAGE.formatted(id)));
    }
}

