package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.generator.LabelGenerator;
import hexlet.code.util.generator.TaskGenerator;
import hexlet.code.util.generator.TaskStatusGenerator;
import hexlet.code.util.generator.UserGenerator;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskGenerator taskGenerator;

    @Autowired
    private UserGenerator userGenerator;

    @Autowired
    private TaskStatusGenerator taskStatusGenerator;

    @Autowired
    private LabelGenerator labelGenerator;

    @Autowired
    private TaskMapper taskMapper;

    private Task testTask;
    private User testAssignee;
    private TaskStatus testStatus;
    private Label testLabel1;
    private Label testLabel2;

    private static final String BASE_URL = "/api/tasks";
    private static final String ID_URL = BASE_URL + "/{id}";

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        testAssignee = Instancio.create(userGenerator.getModel());
        userRepository.save(testAssignee);

        testStatus = Instancio.create(taskStatusGenerator.getModel());
        taskStatusRepository.save(testStatus);

        testLabel1 = Instancio.create(labelGenerator.getModel());
        testLabel2 = Instancio.create(labelGenerator.getModel());
        labelRepository.saveAll(List.of(testLabel1, testLabel2));

        testTask = Instancio.create(taskGenerator.getModel());
        testTask.setAssignee(testAssignee);
        testTask.setTaskStatus(testStatus);
        testTask.setLabels(Set.of(testLabel1, testLabel2));

        taskRepository.save(testTask);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GetTasks {

        @Test
        void index() throws Exception {
            long expectedCount = taskRepository.count();
            var result = mockMvc.perform(get(BASE_URL).with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-Total-Count", String.valueOf(expectedCount)))
                    .andExpect(header().string("Access-Control-Expose-Headers", "X-Total-Count"))
                    .andReturn();

            var body = result.getResponse().getContentAsString();

            List<TaskDTO> actualDtos = objectMapper.readValue(body, new TypeReference<>() { });
            var expectedDtos = taskRepository.findAll().stream()
                    .map(taskMapper::map)
                    .toList();

            assertThat(actualDtos).containsExactlyInAnyOrderElementsOf(expectedDtos);
        }

        private Stream<String> filterKeyProvider() {
            return Stream.of("titleCont", "status", "assigneeId", "labelId");
        }

        @ParameterizedTest
        @MethodSource("filterKeyProvider")
        void indexWithFilters(String key) throws Exception {

            String value = switch (key) {
                case "status" -> testTask.getTaskStatus().getSlug();
                case "titleCont" -> testTask.getName();
                case "assigneeId" -> testTask.getAssignee().getId().toString();
                case "labelId" ->
                        testTask.getLabels()
                                .stream()
                                .findFirst()
                                .get()
                                .getId()
                                .toString();
                default -> "";
            };

            mockMvc.perform(get("/api/tasks")
                            .param(key, value)
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-Total-Count", "1"))
                    .andExpect(content().string(containsString(testTask.getName())));
        }


        @Test
        void show() throws Exception {
            var result = mockMvc.perform(get(ID_URL, testTask.getId())
                            .with(jwt()))
                    .andExpect(status().isOk())
                    .andReturn();
            var body = result.getResponse().getContentAsString();

            var actualDto = objectMapper.readValue(body, TaskDTO.class);
            var expectedTask = taskRepository.findById(testTask.getId()).orElseThrow();

            assertThat(actualDto).isNotNull().satisfies(dto -> {
                assertThat(dto.id()).isEqualTo(expectedTask.getId());
                assertThat(dto.title()).isEqualTo(expectedTask.getName());
                assertThat(dto.status()).isEqualTo(expectedTask.getTaskStatus().getSlug());
                assertThat(dto.assigneeId()).isEqualTo(expectedTask.getAssignee().getId());
                assertThat(dto.index()).isEqualTo(expectedTask.getIndex());
            });
        }

        @Test
        void showNotFound() throws Exception {
            mockMvc.perform(get(ID_URL, 11111).with(jwt()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CreateTask {
        @Test
        void create() throws Exception {
            Task data = Instancio.create(taskGenerator.getModel());
            data.setTaskStatus(testStatus);
            data.setAssignee(testAssignee);
            TaskCreateDTO dto = toCreateDto(data);

            var result = mockMvc.perform(post(BASE_URL)
                            .with(jwt())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            var body = result.getResponse().getContentAsString();
            Long createdTaskId = objectMapper.readTree(body)
                    .get("id")
                    .asLong();


            Task createdTask = taskRepository.findById(createdTaskId)
                    .orElse(null);

            assertThat(createdTask).isNotNull().satisfies(task -> {
                assertThat(task.getName()).isEqualTo(data.getName());
                assertThat(task.getIndex()).isEqualTo(data.getIndex());
                assertThat(task.getAssignee().getId()).isEqualTo(data.getAssignee().getId());
                assertThat(task.getTaskStatus().getSlug()).isEqualTo(data.getTaskStatus().getSlug());
                assertThat(task.getDescription()).isEqualTo(data.getDescription());
            });
        }

        @Test
        void createWithLabels() throws Exception {
            Long labelId = testLabel1.getId();

            var dto = Instancio.of(taskGenerator.getCreateDto())
                    .set(Select.field(TaskCreateDTO::status), testStatus.getSlug())
                    .set(Select.field(TaskCreateDTO::taskLabelIds), Set.of(labelId))
                    .create();

            var result = mockMvc.perform(post(BASE_URL)
                            .with(jwt())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            var body = result.getResponse().getContentAsString();
            Long createdTaskId = objectMapper.readTree(body)
                    .get("id")
                    .asLong();

            Task createdTask = taskRepository.findById(createdTaskId)
                    .orElse(null);

            assertThat(createdTask).isNotNull().satisfies(task ->
                assertThat(task.getLabels())
                        .extracting(Label::getId)
                        .containsExactly(labelId)
            );
        }

        @Test
        void createWithoutAuth() throws Exception {
            var data = Instancio.create(taskGenerator.getCreateDto());

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(data)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void createWithInvalidData() throws Exception {
            var dto = Instancio.of(taskGenerator.getCreateDto())
                    .set(Select.field(TaskCreateDTO::status), null)
                    .create();

            mockMvc.perform(post(BASE_URL)
                            .with(jwt())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdateTask {
        @Test
        void update() throws Exception {
            var dto = Instancio.of(taskGenerator.getUpdateDto())
                    .set(
                            Select.field(TaskUpdateDTO::title),
                            JsonNullable.of("New title")
                    )
                    .set(
                            Select.field(TaskUpdateDTO::content),
                            JsonNullable.of("New content")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testTask.getId())
                            .with(jwt().jwt(builder -> builder.subject(testAssignee.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            Task updatedTask = taskRepository.findById(testTask.getId())
                    .orElse(null);

            assertThat(updatedTask).isNotNull().satisfies(task -> {
                assertThat(task.getName()).isEqualTo("New title");
                assertThat(task.getDescription()).isEqualTo("New content");

                assertThat(task.getTaskStatus().getSlug()).isEqualTo(testStatus.getSlug());
                assertThat(task.getAssignee().getId()).isEqualTo(testAssignee.getId());
            });
        }

        @Test
        void updateReplaceLabels() throws Exception {
            var newLabel = Instancio.create(labelGenerator.getModel());
            labelRepository.save(newLabel);

            var dto = Instancio.of(taskGenerator.getUpdateDto())
                    .set(
                            Select.field(TaskUpdateDTO::taskLabelIds),
                            JsonNullable.of(Set.of(newLabel.getId()))
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testTask.getId())
                            .with(jwt().jwt(builder -> builder.subject(testAssignee.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            var updatedTask = taskRepository.findById(testTask.getId()).orElse(null);

            assertThat(updatedTask).isNotNull().satisfies(task ->
                assertThat(task.getLabels())
                        .extracting(Label::getId)
                        .containsExactly(newLabel.getId())
            );
        }

        @Test
        void updateClearLabels() throws Exception {
            var dto = Instancio.of(taskGenerator.getUpdateDto())
                    .set(Select.field(
                            TaskUpdateDTO::taskLabelIds),
                            JsonNullable.of(Set.of())
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testTask.getId())
                            .with(jwt().jwt(builder -> builder.subject(testAssignee.getEmail())))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());

            var updatedTask = taskRepository.findById(testTask.getId()).orElse(null);

            assertThat(updatedTask).isNotNull().satisfies(task ->
                assertThat(task.getLabels()).isEmpty()
            );
        }

        @Test
        void updateWithInvalidData() throws Exception {
            var dto = Instancio.of(taskGenerator.getUpdateDto())
                    .set(
                            Select.field(TaskUpdateDTO::title),
                            JsonNullable.of("")
                    )
                    .create();

            mockMvc.perform(put(ID_URL, testTask.getId())
                            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    class DeleteTask {

        @Test
        void destroy() throws Exception {
            mockMvc.perform(delete(ID_URL, testTask.getId())
                            .with(jwt().jwt(builder -> builder.subject(testAssignee.getEmail()))))
                    .andExpect(status().isNoContent());

            assertThat(taskRepository.existsById(testTask.getId())).isFalse();
        }
    }


    private TaskCreateDTO toCreateDto(Task data) {
        Set<Long> labelIds = data.getLabels()
                .stream()
                .map(Label::getId)
                .collect(Collectors.toSet());

        Long assigneeId = Optional.of(data.getAssignee())
                .map(User::getId)
                .orElse(null);

        return new TaskCreateDTO(
                data.getIndex(),
                assigneeId,
                data.getName(),
                data.getDescription(),
                data.getTaskStatus().getSlug(),
                labelIds
        );
    }

}
