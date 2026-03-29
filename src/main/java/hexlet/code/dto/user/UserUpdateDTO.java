package hexlet.code.dto.user;

import hexlet.code.validator.EmailNullable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {
    private JsonNullable<String> firstName;
    private JsonNullable<String> lastName;

    @EmailNullable
    private JsonNullable<String> email;

    @Size(min = 3)
    private JsonNullable<String> password;

}
