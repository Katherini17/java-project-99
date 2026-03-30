package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.generator.UserGenerator;
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
    private UserGenerator userGenerator;

    private User testUser;

    private static final String BASE_URL = "/api/users";
    private static final String ID_URL = "%s/{id}".formatted(BASE_URL);

    @BeforeEach
    void setUp() {
        testUser = Instancio.of(userGenerator.getUserModel()).create();
        userRepository.save(testUser);
    }

    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get(BASE_URL)
                    .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    void testShow() throws Exception {
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

    @Test
    void testCreate() throws Exception {
        User data = Instancio.of(userGenerator.getUserModel()).create();

        UserCreateDTO dto = toCreateDTO(data);

        mockMvc.perform(post(BASE_URL)
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

        mockMvc.perform(put(ID_URL, testUser.getId())
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
        mockMvc.perform(delete(ID_URL, testUser.getId())
                        .with(jwt().jwt(builder -> builder.subject(testUser.getEmail()))))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }


    @Test
    void testIndexWithoutAdmin() throws Exception {
        mockMvc.perform(get(BASE_URL).with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateWithoutAdmin() throws Exception {
        UserCreateDTO dto = Instancio.of(userGenerator.getUserCreateDTOModel()).create();

        mockMvc.perform(post(BASE_URL)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }


    @Test
    void testUpdateOtherUser() throws Exception {
        User otherUser = Instancio.of(userGenerator.getUserModel()).create();
        userRepository.save(otherUser);

        var dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("newName"));

        mockMvc.perform(put(ID_URL, otherUser.getId())
                        .with(jwt().jwt(builder -> builder.subject(testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDestroyOtherUser() throws Exception {
        User otherUser = Instancio.of(userGenerator.getUserModel()).create();
        userRepository.save(otherUser);

        mockMvc.perform(delete(ID_URL, otherUser.getId())
                        .with(jwt().jwt(builder -> builder.subject(testUser.getEmail()))))
                .andExpect(status().isForbidden());

        assertThat(userRepository.existsById(otherUser.getId())).isTrue();
    }

    @Test
    void testCreateWithInvalidData() throws Exception {
        var dto = Instancio.of(userGenerator.getUserCreateDTOModel())
                .set(Select.field(UserCreateDTO::password), "12")
                .create();

        mockMvc.perform(post(BASE_URL)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWithInvalidEmail() throws Exception {
        var dto = new UserUpdateDTO();
        dto.setEmail(JsonNullable.of("Invalid email"));

        mockMvc.perform(put(ID_URL, testUser.getId())
                        .with(jwt().jwt(builder -> builder.subject(testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShowNotFound() throws Exception {
        mockMvc.perform(get(ID_URL, 111111)
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
