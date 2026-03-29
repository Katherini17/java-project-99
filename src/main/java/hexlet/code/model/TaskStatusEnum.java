package hexlet.code.model;

public enum TaskStatusEnum {
    DRAFT("draft", "Draft"),
    TO_REVIEW("to_review", "To Review"),
    TO_BE_FIXED("to_be_fixed", "To Be Fixed"),
    TO_PUBLISH("to_publish", "To Publish"),
    PUBLISHED("published", "Published");

    private final String slug;
    private final String name;

    TaskStatusEnum(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

    public String getSlug() { return slug; }
    public String getName() { return name; }


}
