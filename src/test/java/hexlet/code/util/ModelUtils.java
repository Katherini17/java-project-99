package hexlet.code.util;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.openapitools.jackson.nullable.JsonNullable;

public class ModelUtils {
    public static <T> Model<T> buildUpdateModel(Class<T> clazz) {
        return Instancio.of(clazz)
                .lenient()
                .set(Select.all(JsonNullable.class), JsonNullable.undefined())
                .toModel();
    }
}
