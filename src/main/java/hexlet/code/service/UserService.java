package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.exception.UnprocessableEntityException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Role;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";
    private static final String USER_LINKED_TO_TASKS_MESSAGE = "User is an assignee for one or more tasks";

    public Page<UserDTO> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::map);
    }

    @Transactional
    public UserDTO create(UserCreateDTO userData) {
        var user = userMapper.map(userData);
        user.setRole(Role.USER);
        user.setPassword(userData.password(), passwordEncoder);

        var savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());

        return userMapper.map(savedUser);
    }

    public UserDTO findById(Long id) {
        return userMapper.map(findUserById(id));
    }

    @Transactional
    public UserDTO update(UserUpdateDTO userData, Long id) {
        var user = findUserById(id);
        userMapper.update(userData, user);

        Optional.ofNullable(userData.password())
                .filter(JsonNullable::isPresent)
                .map(JsonNullable::get)
                .ifPresent(pw -> user.setPassword(pw, passwordEncoder));

        log.info("User with id {} updated", id);
        return userMapper.map(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        var user = findUserById(id);

        if (taskRepository.existsByAssigneeId(id)) {
            throw new UnprocessableEntityException(USER_LINKED_TO_TASKS_MESSAGE);
        }

        userRepository.delete(user);
        log.info("User with id {} deleted", id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE.formatted(id)));
    }
}

