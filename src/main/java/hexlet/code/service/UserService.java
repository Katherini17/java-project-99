package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Role;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";

    public Page<UserDTO> getAll(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);

        return userRepository.findAll(PageRequest.of(page, size, sort))
                .map(userMapper::map);
    }

    @Transactional
    public UserDTO create(UserCreateDTO userData) {
        User user = userMapper.map(userData);
        user.setRole(Role.USER);
        user.setPassword(userData.password(), passwordEncoder);

        return userMapper.map(userRepository.save(user));
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE.formatted(id)));
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO update(UserUpdateDTO userData, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE.formatted(id)));
        userMapper.update(userData, user);

        Optional.ofNullable(userData.password())
                .filter(JsonNullable::isPresent)
                .map(JsonNullable::get)
                .ifPresent(pw -> user.setPassword(pw, passwordEncoder));

        return userMapper.map(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE.formatted(id)));

        userRepository.delete(user);
    }
}

