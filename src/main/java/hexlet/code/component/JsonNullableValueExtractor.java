package hexlet.code.component;

import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.ValueExtractor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

@Component
public class JsonNullableValueExtractor implements ValueExtractor<JsonNullable<@ExtractedValue ?>> {

    public void extractValues(JsonNullable<?> originalValue, ValueReceiver receiver) {
        if (originalValue == null || !originalValue.isPresent()) {
            receiver.value(null, null);
        } else {
            receiver.value(null, originalValue.get());
        }
    }
}
