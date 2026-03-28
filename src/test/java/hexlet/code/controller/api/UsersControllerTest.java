package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users")
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    void testShow() throws Exception {
        var result = mockMvc.perform(get("/api/users/{id}", testUser.getId())
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

    @Test
    void testCreate() throws Exception {
        User data = Instancio.of(modelGenerator.getUserModel()).create();

        UserCreateDTO dto = toCreateDTO(data);

        mockMvc.perform(post("/api/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        User createdUser = userRepository.findByEmail(data.getEmail()).orElse(null);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(passwordEncoder.matches(data.getPassword(), createdUser.getPassword())).isTrue();
    }

    @Test
    void testUpdate() throws Exception {
        var dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("newName"));

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .with(jwt().jwt(builder -> builder.subject(testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();

        assertThat(updatedUser.getFirstName()).isEqualTo("newName");
        assertThat(updatedUser.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    void testDestroy() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                        .with(jwt().jwt(builder -> builder.subject(testUser.getEmail()))))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }



    @Test
    void testCreateWithInvalidData() throws Exception {
        var dto = Instancio.of(modelGenerator.getUserCreateDTOModel())
                .set(Select.field(UserCreateDTO::email), "Invalid Email")
                .create();

        mockMvc.perform(post("/api/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWithInvalidData() throws Exception {
        var dto = new UserUpdateDTO();
        dto.setPassword(JsonNullable.of("12"));

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .with(jwt().jwt(builder -> builder.subject(testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 111111)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound());
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
