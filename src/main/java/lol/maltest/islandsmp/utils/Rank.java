package lol.maltest.islandsmp.utils;

import lombok.Getter;

@Getter
public enum Rank {

    TRUSTED("Trusted"),

    MEMBER("Member"),
    MODERATOR("Moderator"),
    ADMINISTRATOR("Admin"),
    OWNER("Owner");

    private final String display;

    Rank(String display) {
        this.display = display;
    }

    public boolean isHigherThan(Rank other){
        return this.ordinal() > other.ordinal();
    }

    public boolean isLowerThan(Rank other){
        return this.ordinal() < other.ordinal();
    }
}
