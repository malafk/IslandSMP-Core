package lol.maltest.islandsmp.entities.sub;

import lol.maltest.islandsmp.utils.Rank;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.UUID;

public class IslandMember {

    @Getter private final UUID playerUuid;
    @Getter private final String playerName;
    private String rank = Rank.MEMBER.getDisplay();

    public IslandMember(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.playerName = Bukkit.getPlayer(playerUuid).getName();
    }

    public Rank getRank() {
        return Arrays.stream(Rank.values())
                .filter(rank -> rank.getDisplay().equalsIgnoreCase(this.rank))
                .findFirst()
                .orElse(null);
    }

    public void setRank(Rank rank) {
        this.rank = rank.getDisplay();
    }
}
