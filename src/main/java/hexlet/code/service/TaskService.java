package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
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
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    private static final String TASK_NOT_FOUND_MESSAGE = "Task with id %d not found";

    public Page<TaskDTO> getAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::map);
    }

    public TaskDTO findById(Long id) {
        return taskMapper.map(findTaskById(id));
    }

    @Transactional
    public TaskDTO create(TaskCreateDTO taskData) {
        var task = taskMapper.map(taskData);

        log.info("Task created: {}", task.getName());
        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO update(TaskUpdateDTO taskData, Long id) {
        var task = findTaskById(id);
        taskMapper.update(taskData, task);

        log.info("Task with id {} updated", id);
        return taskMapper.map(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id) {
        taskRepository.delete(findTaskById(id));
        log.info("Task with id {} deleted", id);
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TASK_NOT_FOUND_MESSAGE.formatted(id)));
    }
}

