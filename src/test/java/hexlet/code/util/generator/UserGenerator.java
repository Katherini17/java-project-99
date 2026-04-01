package hexlet.code.util.generator;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.util.ModelUtils;
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

    private Model<User> model;
    private Model<UserCreateDTO> createDTO;
    private Model<UserUpdateDTO> updateDTO;


    private final Faker faker;

    @PostConstruct
    public void init() {
        this.model = buildModel();
        this.createDTO = buildCreateDTO();
        this.updateDTO = ModelUtils.buildUpdateModel(UserUpdateDTO.class);
    }

    private Model<User> buildModel() {
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

    private Model<UserCreateDTO> buildCreateDTO() {
        return Instancio.of(UserCreateDTO.class)
                .supply(Select.field(UserCreateDTO::firstName), () -> faker.name().firstName())
                .supply(Select.field(UserCreateDTO::lastName), () -> faker.name().lastName())
                .supply(Select.field(UserCreateDTO::email), () -> faker.internet().emailAddress())
                .supply(Select.field(UserCreateDTO::password), () -> faker.credentials().password(3, 10))
                .toModel();
    }

}
