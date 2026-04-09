package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.generator.TaskGenerator;
import hexlet.code.util.generator.TaskStatusGenerator;
import hexlet.code.util.generator.UserGenerator;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserGenerator userGenerator;

    @Autowired
    private TaskStatusGenerator taskStatusGenerator;

    @Autowired
    private TaskGenerator taskGenerator;

    private User testUser;

    private static final String BASE_URL = "/api/users";
    private static final String ID_URL = BASE_URL + "/{id}";

    @BeforeEach
    void setUp() {
        testUser = Instancio.create(userGenerator.getModel());
        userRepository.save(testUser);
    }

    @Nested
    class GetUsers {
        @Test
        void index() throws Exception {
            long expectedCount = userRepository.count();
            var result = mockMvc.perform(get(BASE_URL)
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-Total-Count", String.valueOf(expectedCount)))
                    .andExpect(header().string("Access-Control-Expose-Headers", "X-Total-Count"))
                    .andReturn();
            var body = result.getResponse().getContentAsString();

            assertThatJson(body).isArray();
        }

//        @Test
//        void indexWithoutAdmin() throws Exception {
//            mockMvc.perform(get(BASE_URL).with(jwt()))
//                    .andExpect(status().isForbidden());
//        }

        @Test
        void show() throws Exception {
            var result = mockMvc.perform(get(ID_URL, testUser.getId())
                            .with(jwt().jwt(builder -> builder.subject(testUser.getEmail()))))
                    .andExpect(status().isOk())
                    .andReturn();

            var body = result.getResponse().getContentAsString();
            assertThatJson(body).and(
                    v -> v.node("email").isEqualTo(testUser.getEmail()),
                    v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                    v -> v.node("password").isAbsent()
            );
        }

//        @Test
//        void showByOtherUser() throws Exception {
//            var otherUser = Instancio.create(userGenerator.getModel());
//            userRepository.save(otherUser);
//
//            mockMvc.perform(get(ID_URL, otherUser.getId())
//                            .with(jwt().jwt(builder -> builder.subject(testUser.getEmail()))))
//                    .andExpect(status().isForbidden());
//        }

        @Test
        void showNotFound() throws Exception {
            mockMvc.perform(get(ID_URL, 111111)
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CreateUser {
        @Test
        void create() throws Exception {
            User data = Instancio.create(userGenerator.getModel());
            UserCreateDTO dto = toCreateDTO(data);

            mockMvc.perform(post(BASE_URL)
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());

            User createdUser = userRepository.findByEmail(data.getEmail()).orElse(null);

            assertThat(createdUser).isNotNull().satisfies(user -> {
                assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
                assertThat(user.getEmail()).isEqualTo(data.getEmail());
                assertThat(passwordEncoder.matches(data.getPassword(), user.getPassword())).isTrue();
            });
        }

//        @Test
//        void createWithoutAdmin() throws Exception {
//            UserCreateDTO dto = Instancio.create(userGenerator.getCreateDTO());
//
//            mockMvc.perform(post(BASE_URL)
//                            .with(jwt())
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(dto)))
//                    .andExpect(status().isForbidden());
//        }

        @Test
        void createWithInvalidData() throws Exception {
            var dto = Instancio.of(userGenerator.getCreateDTO())
                    .set(Select.field(UserCreateDTO::password), "12")
                    .create();

            mockMvc.perform(post(BASE_URL)
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void update() throws Exception {
            var dto = Instancio.of(userGenerator.getUpdateDTO())
                    .set(
                            Select.field(UserUpdateDTO::firstName),
                            JsonNullable.of("New name")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testUser.getId())
                            .with(jwt().jwt(builder -> builder.subject(testUser.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();

            assertThat(updatedUser).isNotNull().satisfies(user -> {
                assertThat(user.getFirstName()).isEqualTo("New name");
                assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
            });
        }

        @Test
        void updateByAdmin() throws Exception {
            var dto = Instancio.of(userGenerator.getUpdateDTO())
                    .set(Select.field(UserUpdateDTO::firstName), JsonNullable.of("New name"))
                    .create();

            mockMvc.perform(put(ID_URL, testUser.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            var user = userRepository.findById(testUser.getId()).orElseThrow();

            assertThat(user).isNotNull().satisfies(u ->
                assertThat(u.getFirstName()).isEqualTo("New name")
            );
        }

        @Test
        void updateByOtherUser() throws Exception {
            User otherUser = Instancio.create(userGenerator.getModel());
            userRepository.save(otherUser);

            var dto = Instancio.of(userGenerator.getUpdateDTO())
                    .set(
                            Select.field(UserUpdateDTO::firstName),
                            JsonNullable.of("New name")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, otherUser.getId())
                            .with(jwt().jwt(builder -> builder.subject(testUser.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updateWithInvalidEmail() throws Exception {
            var dto = Instancio.of(userGenerator.getUpdateDTO())
                    .set(
                            Select.field(UserUpdateDTO::email),
                            JsonNullable.of("Invalid email")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testUser.getId())
                            .with(jwt().jwt(builder -> builder.subject(testUser.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class DeleteUser {

        @Test
        void destroy() throws Exception {
            mockMvc.perform(delete(ID_URL, testUser.getId())
                            .with(jwt().jwt(builder -> builder.subject(testUser.getEmail()))))
                    .andExpect(status().isNoContent());

            assertThat(userRepository.existsById(testUser.getId())).isFalse();
        }

        @Test
        void destroyUserWithTasks() throws Exception {
            var status = Instancio.create(taskStatusGenerator.getModel());
            taskStatusRepository.save(status);

            var task = Instancio.create(taskGenerator.getModel());
            task.setAssignee(testUser);
            task.setTaskStatus(status);
            taskRepository.save(task);

            mockMvc.perform(delete(ID_URL, testUser.getId())
                            .with(jwt().jwt(builder -> builder.subject(testUser.getEmail()))))
                    .andExpect(status().isUnprocessableEntity());

            assertThat(userRepository.existsById(testUser.getId())).isTrue();
        }


        @Test
        void destroyByOtherUser() throws Exception {
            User otherUser = Instancio.create(userGenerator.getModel());
            userRepository.save(otherUser);

            mockMvc.perform(delete(ID_URL, otherUser.getId())
                            .with(jwt().jwt(builder -> builder.subject(testUser.getEmail()))))
                    .andExpect(status().isForbidden());

            assertThat(userRepository.existsById(otherUser.getId())).isTrue();
        }

    }


    private UserCreateDTO toCreateDTO(User data) {
        return new UserCreateDTO(
                data.getFirstName(),
                data.getLastName(),
                data.getEmail(),
                data.getPassword()
        );
    }

}
