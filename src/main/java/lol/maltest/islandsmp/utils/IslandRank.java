package lol.maltest.islandsmp.utils;

import lombok.Getter;

@Getter
public enum IslandRank {

    MEMBER("Member"),
    MODERATOR("Moderator"),
    ADMINISTRATOR("Admin"),
    OWNER("Owner");

    private final String display;

    IslandRank(String display) {
        this.display = display;
    }
}
