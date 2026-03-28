package hexlet.code.controller.api;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
public class UsersController {

    private final UserService userService;

    @GetMapping(path = "")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> index(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit
    ) {
        Page<UserDTO> resultPage = userService.getAll(page - 1, limit);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resultPage.getTotalElements()))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(resultPage.getContent());
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userUtils.isOwner(#id, authentication.name)")
    public UserDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO create(@RequestBody @Valid UserCreateDTO userData) {
        return userService.create(userData);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userUtils.isOwner(#id, authentication.name)")
    public UserDTO update(
            @RequestBody @Valid UserUpdateDTO userData,
            @PathVariable Long id
    ) {
        return userService.update(userData, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @userUtils.isOwner(#id, authentication.name)")
    public void destroy(@PathVariable Long id) {
        userService.delete(id);
    }

}
