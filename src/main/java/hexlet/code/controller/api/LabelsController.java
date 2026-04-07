package hexlet.code.controller.api;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/labels")
@Validated
@PreAuthorize("isAuthenticated()")
@Tag(name = "Labels", description = "Dictionary of labels for grouping tasks")
public class LabelsController {

    private final LabelService labelService;

    @Operation(summary = "Get list of all labels")
    @ApiResponse(responseCode = "200", description = "List of labels retrieved successfully")
    @GetMapping(path = "")
    public ResponseEntity<List<LabelDTO>> index() {
        Page<LabelDTO> resultPage = labelService.getAll(Pageable.unpaged());
        return buildPagingResponse(resultPage);
    }

    @Operation(summary = "Get label details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found"),
            @ApiResponse(responseCode = "404", description = "Label not found")
    })
    @GetMapping(path = "/{id}")
    public LabelDTO show(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @Operation(summary = "Create a new label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Label created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request (validation failed)"),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity (e.g. non-unique name)")
    })
    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO create(@RequestBody @Valid LabelCreateDTO labelData) {
        return labelService.create(labelData);
    }

    @Operation(summary = "Update an existing label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request (validation failed)"),
            @ApiResponse(responseCode = "403", description = "Access denied (ADMIN role required)"),
            @ApiResponse(responseCode = "404", description = "Label not found")
    })
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public LabelDTO update(
            @RequestBody @Valid LabelUpdateDTO labelData,
            @PathVariable Long id
    ) {
        return labelService.update(labelData, id);
    }

    @Operation(summary = "Delete a label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Label deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (ADMIN role required)"),
            @ApiResponse(responseCode = "404", description = "Label not found"),
            @ApiResponse(responseCode = "422", description = "Label is in use and cannot be deleted")
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void destroy(@PathVariable Long id) {
        labelService.delete(id);
    }

}
