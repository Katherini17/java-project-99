package hexlet.code.component;

import hexlet.code.model.Role;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.TaskStatusEnum;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;

    @Value("${config.admin.email}")
    private String adminEmail;

    @Value("${config.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        initializeAdmin();
        initializeTaskStatuses();

    }

    private void initializeAdmin() {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            var admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(adminPassword, passwordEncoder);
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            log.info("Default admin user created");
        }
    }

    private void initializeTaskStatuses() {
        Arrays.stream(TaskStatusEnum.values())
                .filter(status -> taskStatusRepository.findBySlug(status.getSlug()).isEmpty())
                .forEach(status -> {

                    var taskStatus = new TaskStatus();
                    taskStatus.setName(status.getName());
                    taskStatus.setSlug(status.getSlug());

                    taskStatusRepository.save(taskStatus);
                    log.info("Default task status created: {}", status.getSlug());

                });
    }
}
