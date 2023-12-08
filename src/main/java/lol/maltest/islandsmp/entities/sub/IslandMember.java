package lol.maltest.islandsmp.entities.sub;

import lol.maltest.islandsmp.utils.IslandRank;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.UUID;

public class IslandMember {

    @Getter private final UUID playerUuid;
    @Getter @Setter private final String playerName;
    private String rank = IslandRank.MEMBER.getDisplay();

    public IslandMember(UUID playerUuid) {
        this.playerUuid = playerUuid;
        this.playerName = Bukkit.getPlayer(playerUuid).getName();
    }

    public IslandRank getRank() {
        return Arrays.stream(IslandRank.values())
                .filter(rank -> rank.getDisplay().equalsIgnoreCase(this.rank))
                .findFirst()
                .orElse(null);
    }

    public void setRank(IslandRank islandRank) {
        this.rank = islandRank.getDisplay();
    }
}
