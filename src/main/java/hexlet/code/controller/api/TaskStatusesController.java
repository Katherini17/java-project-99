package hexlet.code.controller.api;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Statuses", description = "Dictionary of task statuses")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized")
})
public class TaskStatusesController {

    private final TaskStatusService taskStatusService;

    @Operation(summary = "Get list of all task statuses")
    @ApiResponse(responseCode = "200", description = "List of statuses retrieved successfully")
    @GetMapping("")
    public ResponseEntity<List<TaskStatusDTO>> index() {
        return buildPagingResponse(taskStatusService.getAll(Pageable.unpaged()));
    }

    @Operation(summary = "Get task status details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status found"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    @GetMapping("/{id}")
    public TaskStatusDTO show(@PathVariable Long id) {
        return taskStatusService.getById(id);
    }

    @Operation(summary = "Create a new task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request (validation failed)"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity (e.g. non-unique slug)")
    })
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@RequestBody @Valid TaskStatusCreateDTO taskStatusData) {
        return taskStatusService.create(taskStatusData);
    }

    @Operation(summary = "Update an existing task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request (validation failed)"),
            @ApiResponse(responseCode = "404", description = "Status not found")
    })
    @PutMapping("/{id}")
    public TaskStatusDTO update(
            @RequestBody @Valid TaskStatusUpdateDTO taskStatusData,
            @PathVariable Long id
    ) {
        return taskStatusService.update(taskStatusData, id);
    }

    @Operation(summary = "Delete a task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Status deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Status not found"),
            @ApiResponse(responseCode = "422", description = "Status is in use and cannot be deleted")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskStatusService.delete(id);
    }

}
