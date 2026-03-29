package hexlet.code.util.generator;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class UserGenerator {

    private Model<User> userModel;
    private Model<UserCreateDTO> userCreateDTOModel;

    private final Faker faker;

    @PostConstruct
    public void init() {
        userModel = buildUserModel();
        userCreateDTOModel = buildUserCreateDTOModel();
    }

    private Model<User> buildUserModel() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.credentials().password(3, 10))
                .toModel();
    }

    private Model<UserCreateDTO> buildUserCreateDTOModel() {
        return Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::firstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDTO::lastName), () -> faker.name().lastName())
                .supply(Select.field(UserCreateDTO::email), () -> faker.internet().emailAddress())
                .supply(Select.field(UserCreateDTO::password), () -> faker.credentials().password(3, 10))
                .toModel();
    }
}
