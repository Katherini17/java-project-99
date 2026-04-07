package hexlet.code.component;

import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userUtils")
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    /**
     * Checks if the user is the owner of the account.
     * Used for security checks in UsersController.
     */
    public boolean isOwner(Long id, String currentUsername) {
        return userRepository.findById(id)
                .map(user -> user.getUsername().equals(currentUsername))
                .orElse(false);
    }
}
