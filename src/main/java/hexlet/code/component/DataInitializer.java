package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.LabelEnum;
import hexlet.code.model.Role;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.TaskStatusEnum;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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

/**
 * Initializer for default application data: admin user, task statuses, and labels.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    @Value("${config.admin.email:hexlet@example.com}")
    private String adminEmail;

    @Value("${config.admin.password:qwerty}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("--- Starting database initialization ---");

        initializeAdmin();
        initializeTaskStatuses();
        initializeLabels();

        log.info("--- Database initialization finished ---");
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

                    var newStatus = new TaskStatus();
                    newStatus.setName(status.getName());
                    newStatus.setSlug(status.getSlug());

                    taskStatusRepository.save(newStatus);
                    log.info("Default task status created: {}", status.getSlug());
                });
    }

    private void initializeLabels() {
        Arrays.stream(LabelEnum.values())
                .filter(label -> labelRepository.findByName(label.getName()).isEmpty())
                .forEach(label -> {

                    var newLabel = new Label();
                    newLabel.setName(label.getName());

                    labelRepository.save(newLabel);
                    log.info("Initialized label: {}", label.getName());
                });
    }


}
