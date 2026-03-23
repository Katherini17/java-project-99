package hexlet.code.component;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${config.admin.email}")
    private String adminEmail;

    @Value("${config.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            var userData = new UserCreateDTO();
            userData.setEmail(adminEmail);
            userData.setPassword(adminPassword);

            userService.create(userData);
            log.info("Admin user successfully created.");
        } else {
            log.info("Admin user already exists");
        }
    }
}
