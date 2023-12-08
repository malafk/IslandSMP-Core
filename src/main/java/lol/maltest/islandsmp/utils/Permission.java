package lol.maltest.islandsmp.utils;

public class Permission {
    private String name;
    private String displayName;
    private String description;

    public Permission(String name, String displayName, String description) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}