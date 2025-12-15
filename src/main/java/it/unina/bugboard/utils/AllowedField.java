package it.unina.bugboard.utils;

public enum AllowedField {

    PRIORITY("priority"),
    STATE("state"),
    CREATED_AT("createdAt");

    private final String property;

    AllowedField(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    // 🔥 converte input frontend → enum
    public static AllowedField from(String value) {
        return switch (value.toLowerCase()) {
            case "priority" -> PRIORITY;
            case "state" -> STATE;
            case "createdat" -> CREATED_AT;
            default -> throw new IllegalArgumentException("Invalid sort field");
        };
    }
}

