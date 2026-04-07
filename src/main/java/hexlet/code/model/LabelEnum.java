package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Predefined labels used for automatic database initialization.
 */
@Getter
@AllArgsConstructor
public enum LabelEnum {
    FEATURE("feature"),
    BUG("bug");

    private final String name;
}
