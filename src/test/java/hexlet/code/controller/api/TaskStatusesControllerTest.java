package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.generator.TaskStatusGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
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

    private TaskStatus testTaskStatus;

    private static final String BASE_URL = "/api/task_statuses";
    private static final String ID_URL = "%s/{id}".formatted(BASE_URL);

    @BeforeEach
    void setUp() {
        testTaskStatus = Instancio.of(taskStatusGenerator.getTaskStatusModel())
                .create();
        taskStatusRepository.save(testTaskStatus);
    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get(BASE_URL).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    void testShow() throws Exception {
        var result = mockMvc.perform(get(ID_URL, testTaskStatus.getId())
                        .with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    void testCreate() throws Exception {
        TaskStatus data = Instancio.of(taskStatusGenerator.getTaskStatusModel())
                .create();
        TaskStatusCreateDTO dto = toCreateDTO(data);

        mockMvc.perform(post(BASE_URL)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        TaskStatus createdTaskStatus = taskStatusRepository.findBySlug(data.getSlug())
                .orElse(null);

        assertThat(createdTaskStatus).isNotNull();
        assertThat(createdTaskStatus.getName()).isEqualTo(data.getName());
    }

    @Test
    void testUpdate() throws Exception {
        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("newName"));

        mockMvc.perform(put(ID_URL, testTaskStatus.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        TaskStatus updatedTaskStatus = taskStatusRepository.findById(testTaskStatus.getId())
                        .orElse(null);

        assertThat(updatedTaskStatus).isNotNull();
        assertThat(updatedTaskStatus.getName()).isEqualTo("newName");
        assertThat(updatedTaskStatus.getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    void testDestroy() throws Exception {
        mockMvc.perform(delete(ID_URL, testTaskStatus.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(testTaskStatus.getId())).isFalse();
    }

    @Test
    void testCreateWithoutAdmin() throws Exception {
        var dto = Instancio.of(taskStatusGenerator.getTaskStatusCreateDTOModel()).create();

        mockMvc.perform(post(BASE_URL)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateWithoutAdmin() throws Exception {
        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("New Name"));

        mockMvc.perform(put(ID_URL, testTaskStatus.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDestroyWithoutAdmin() throws Exception {
        mockMvc.perform(delete(ID_URL, testTaskStatus.getId())
                        .with(jwt()))
                .andExpect(status().isForbidden());

        assertThat(taskStatusRepository.existsById(testTaskStatus.getId())).isTrue();
    }



    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get(ID_URL, 11111).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        var dto = new TaskStatusCreateDTO("", "");

        mockMvc.perform(post(BASE_URL)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of(""));

        mockMvc.perform(put(ID_URL, testTaskStatus.getId())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    private TaskStatusCreateDTO toCreateDTO(TaskStatus data) {
        return new TaskStatusCreateDTO(
                data.getName(),
                data.getSlug()
        );
    }
}
