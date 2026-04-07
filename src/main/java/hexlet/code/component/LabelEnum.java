package hexlet.code.component;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LabelEnum {
    FEATURE("feature"),
    BUG("bug");

    private final String name;
}
