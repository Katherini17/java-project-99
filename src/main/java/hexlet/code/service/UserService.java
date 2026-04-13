package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDTO> getAll(Pageable pageable);
    UserDTO getById(Long id);
    UserDTO create(UserCreateDTO userData);
    UserDTO update(UserUpdateDTO userData, Long id);
    void delete(Long id);
}
