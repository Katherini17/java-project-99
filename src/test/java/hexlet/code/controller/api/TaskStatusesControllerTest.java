package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.generator.TaskStatusGenerator;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TaskStatusesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusGenerator taskStatusGenerator;

    private TaskStatus testStatus;

    private static final String BASE_URL = "/api/task_statuses";
    private static final String ID_URL = BASE_URL + "/{id}";

    @BeforeEach
    void setUp() {
        testStatus = Instancio.create(taskStatusGenerator.getModel());
        taskStatusRepository.save(testStatus);
    }

    @Nested
    class GetTaskStatuses {
        @Test
        void index() throws Exception {
            long expectedCount = taskStatusRepository.count();
            var result = mockMvc.perform(get(BASE_URL).with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-Total-Count", String.valueOf(expectedCount)))
                    .andExpect(header().string("Access-Control-Expose-Headers", "X-Total-Count"))
                    .andReturn();

            var body = result.getResponse().getContentAsString();
            assertThatJson(body).isArray();
        }

        @Test
        void show() throws Exception {
            var result = mockMvc.perform(get(ID_URL, testStatus.getId())
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andReturn();
            var body = result.getResponse().getContentAsString();

            assertThatJson(body).and(
                    v -> v.node("name").isEqualTo(testStatus.getName()),
                    v -> v.node("slug").isEqualTo(testStatus.getSlug())
            );
        }

        @Test
        void showNotFound() throws Exception {
            mockMvc.perform(get(ID_URL, 11111).with(jwt()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CreateTaskStatus {
        @Test
        void create() throws Exception {
            TaskStatus data = Instancio.create(taskStatusGenerator.getModel());
            TaskStatusCreateDTO dto = toCreateDTO(data);

            mockMvc.perform(post(BASE_URL)
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());

            TaskStatus createdStatus = taskStatusRepository.findBySlug(data.getSlug())
                    .orElse(null);

            assertThat(createdStatus).isNotNull().satisfies(status -> {
                assertThat(status.getName()).isEqualTo(data.getName());
                assertThat(status.getSlug()).isEqualTo(data.getSlug());
            });
        }

        @Test
        void createWithoutAdmin() throws Exception {
            var dto = Instancio.create(taskStatusGenerator.getCreateDTO());

            mockMvc.perform(post(BASE_URL)
                            .with(jwt())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void createWithInvalidData() throws Exception {
            var dto = new TaskStatusCreateDTO("", "");

            mockMvc.perform(post(BASE_URL)
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class UpdateTaskStatus {
        @Test
        void update() throws Exception {
            var dto = Instancio.of(taskStatusGenerator.getUpdateDTO())
                    .set(
                            Select.field(TaskStatusUpdateDTO::name),
                            JsonNullable.of("New name")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testStatus.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            TaskStatus updatedStatus = taskStatusRepository.findById(testStatus.getId())
                    .orElse(null);

            assertThat(updatedStatus).isNotNull().satisfies(status -> {
                assertThat(status.getName()).isEqualTo("New name");
                assertThat(status.getSlug()).isEqualTo(testStatus.getSlug());
            });
        }

        @Test
        void updateWithoutAdmin() throws Exception {
            var dto = Instancio.of(taskStatusGenerator.getUpdateDTO())
                    .set(
                            Select.field(TaskStatusUpdateDTO::name),
                            JsonNullable.of("New name")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testStatus.getId())
                            .with(jwt())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updateWithInvalidData() throws Exception {
            var dto = Instancio.of(taskStatusGenerator.getUpdateDTO())
                    .set(
                            Select.field(TaskStatusUpdateDTO::name),
                            JsonNullable.of("")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testStatus.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class DeleteTaskStatus {
        @Test
        void destroy() throws Exception {
            mockMvc.perform(delete(ID_URL, testStatus.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isNoContent());

            assertThat(taskStatusRepository.existsById(testStatus.getId())).isFalse();
        }


        @Test
        void destroyWithoutAdmin() throws Exception {
            mockMvc.perform(delete(ID_URL, testStatus.getId())
                            .with(jwt()))
                    .andExpect(status().isForbidden());

            assertThat(taskStatusRepository.existsById(testStatus.getId())).isTrue();
        }

    }


    private TaskStatusCreateDTO toCreateDTO(TaskStatus data) {
        return new TaskStatusCreateDTO(
                data.getName(),
                data.getSlug()
        );
    }
}
