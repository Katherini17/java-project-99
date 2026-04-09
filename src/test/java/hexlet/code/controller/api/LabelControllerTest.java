package hexlet.code.controller.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.generator.LabelGenerator;
import hexlet.code.util.generator.TaskGenerator;
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
class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelGenerator labelGenerator;

    @Autowired
    private TaskGenerator taskGenerator;

    @Autowired
    private TaskStatusGenerator taskStatusGenerator;

    private Label testLabel;

    private static final String BASE_URL = "/api/labels";
    private static final String ID_URL = BASE_URL + "/{id}";

    @BeforeEach
    void setUp() {
        testLabel = Instancio.create(labelGenerator.getModel());
        labelRepository.save(testLabel);
    }

    @Nested
    class GetLabels {

        @Test
        void index() throws Exception {
            long expectedCount = labelRepository.count();

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
            var result = mockMvc.perform(get(ID_URL, testLabel.getId())
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andReturn();

            var body = result.getResponse().getContentAsString();

            assertThatJson(body).and(
                    v -> v.node("id").isEqualTo(testLabel.getId()),
                    v -> v.node("name").isEqualTo(testLabel.getName())
            );
        }

        @Test
        void showNotFound() throws Exception {
            mockMvc.perform(get(ID_URL, 111111).with(jwt()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CreateLabel {

        @Test
        void create() throws Exception {
            Label data = Instancio.create(labelGenerator.getModel());
            LabelCreateDTO dto = toCreateDTO(data);

            mockMvc.perform(post(BASE_URL)
                            .with(jwt())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());

            Label createdLabel = labelRepository.findByName(data.getName())
                    .orElse(null);

            assertThat(createdLabel).isNotNull().satisfies(label ->
                assertThat(label.getName()).isEqualTo(data.getName())
            );
        }

        @Test
        void createWithoutAuth() throws Exception {
            var data = Instancio.create(labelGenerator.getCreateDTO());

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(data)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void createWithInvalidData() throws Exception {
            var dto = new LabelCreateDTO("ab");

            mockMvc.perform(post(BASE_URL)
                            .with(jwt())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdateLabel {
        @Test
        void update() throws Exception {
            var dto = Instancio.of(labelGenerator.getUpdateDTO())
                    .set(
                            Select.field(LabelUpdateDTO::name),
                            JsonNullable.of("New name")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testLabel.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            Label updatedLabel = labelRepository.findById(testLabel.getId())
                    .orElse(null);

            assertThat(updatedLabel).isNotNull().satisfies(label ->
                assertThat(label.getName()).isEqualTo("New name")
            );
        }

//        @Test
//        void updateWithoutAdmin() throws Exception {
//            var dto = new LabelUpdateDTO(JsonNullable.of("New Name"));
//
//            mockMvc.perform(put(ID_URL, testLabel.getId())
//                            .with(jwt())
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(dto)))
//                    .andExpect(status().isForbidden());
//        }

        @Test
        void updateWithInvalidData() throws Exception {
            var dto = Instancio.of(labelGenerator.getUpdateDTO())
                    .set(
                            Select.field(LabelUpdateDTO::name),
                            JsonNullable.of("")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testLabel.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    class DeleteLabel {
        @Test
        void destroy() throws Exception {
            mockMvc.perform(delete(ID_URL, testLabel.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isNoContent());

            assertThat(labelRepository.existsById(testLabel.getId())).isFalse();
        }

//        @Test
//        void destroyWithoutAdmin() throws Exception {
//            mockMvc.perform(delete(ID_URL, testLabel.getId())
//                            .with(jwt()))
//                    .andExpect(status().isForbidden());
//
//            assertThat(labelRepository.existsById(testLabel.getId())).isTrue();
//        }

        @Test
        void destroyLinkedLabel() throws Exception {
            var status = Instancio.create(taskStatusGenerator.getModel());
            taskStatusRepository.save(status);

            var task = Instancio.create(taskGenerator.getModel());
            task.setTaskStatus(status);
            task.addLabel(testLabel);
            taskRepository.save(task);

            mockMvc.perform(delete(ID_URL, testLabel.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isUnprocessableEntity());

            assertThat(labelRepository.existsById(testLabel.getId())).isTrue();
        }
    }

    private LabelCreateDTO toCreateDTO(Label model) {
        return new LabelCreateDTO(model.getName());
    }
}

