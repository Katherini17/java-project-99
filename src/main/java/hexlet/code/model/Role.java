package hexlet.code.model;

public enum Role {
    ADMIN,
    USER;

    public String withPrefix() {
        return "ROLE_" + this.name();
    }
}
