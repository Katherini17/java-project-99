package hexlet.code.controller.api;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static hexlet.code.util.PageUtils.buildPagingResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@Validated
@PreAuthorize("isAuthenticated()")
@Tag(name = "Tasks", description = "Task CRUD and advanced filtering")
public class TasksController {

    private final TaskService taskService;

    @Operation(summary = "Get list of tasks with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @GetMapping(path = "")
    public ResponseEntity<List<TaskDTO>> index(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int perPage,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") Sort.Direction order
    ) {
        var pageRequest = PageRequest.of(
                page - 1,
                perPage,
                Sort.by(order, sort)
        );
        Page<TaskDTO> resultPage = taskService.getAll(pageRequest);

        return buildPagingResponse(resultPage);
    }

    @Operation(summary = "Get task details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping(path = "/{id}")
    public TaskDTO show(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request (validation failed)"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity (invalid status or assignee reference)")
    })
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@RequestBody @Valid TaskCreateDTO taskData) {
        return taskService.create(taskData);
    }

    @Operation(summary = "Update an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request (validation failed)"),
            @ApiResponse(responseCode = "403", description = "Access denied (Only ADMIN or Assignee can update)"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isAssignee(#id, authentication.name)")
    public TaskDTO update(
            @RequestBody @Valid TaskUpdateDTO taskData,
            @PathVariable Long id
    ) {
        return taskService.update(taskData, id);
    }

    @Operation(summary = "Delete a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (Only ADMIN or Assignee can delete)"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @taskUtils.isAssignee(#id, authentication.name)")
    public void destroy(@PathVariable Long id) {
        taskService.delete(id);
    }

}
