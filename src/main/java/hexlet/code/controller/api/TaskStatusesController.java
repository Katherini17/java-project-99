package hexlet.code.controller.api;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static hexlet.code.util.PageUtils.buildPagingResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task_statuses")
@Validated
@PreAuthorize("isAuthenticated()")
@Tag(name = "Statuses", description = "Dictionary of task statuses")
public class TaskStatusesController {

    private final TaskStatusService taskStatusService;

    @GetMapping("")
    public ResponseEntity<List<TaskStatusDTO>> index() {
        return buildPagingResponse(taskStatusService.getAll(Pageable.unpaged()));
    }

    @GetMapping("/{id}")
    public TaskStatusDTO show(@PathVariable Long id) {
        return taskStatusService.findById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDTO create(@RequestBody @Valid TaskStatusCreateDTO taskStatusData) {
        return taskStatusService.create(taskStatusData);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDTO update(
            @RequestBody @Valid TaskStatusUpdateDTO taskStatusData,
            @PathVariable Long id
    ) {
        return taskStatusService.update(taskStatusData, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void destroy(@PathVariable Long id) {
        taskStatusService.delete(id);
    }

}
