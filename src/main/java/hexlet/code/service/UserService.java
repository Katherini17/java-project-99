package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";

    public Page<UserDTO> getAll(int page, int limit) {
        return userRepository.findAll(PageRequest.of(page, limit))
                .map(userMapper::map);
    }

    @Transactional
    public UserDTO create(UserCreateDTO userData) {
        User user = userMapper.map(userData);
        user.setPassword(passwordEncoder.encode(userData.getPassword()));
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
        applyPasswordUpdate(userData, user);

        return userMapper.map(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE.formatted(id)));

        userRepository.delete(user);
    }

    private void applyPasswordUpdate(UserUpdateDTO userData, User user) {
        userData.getPassword().ifPresent(password -> {
            if (password != null) {
                user.setPassword(passwordEncoder.encode(password));
            }
        });
    }
}
