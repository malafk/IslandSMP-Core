package lol.maltest.islandsmp.entities;

import lol.maltest.islandsmp.entities.sub.IslandLocation;
import lol.maltest.islandsmp.entities.type.IslandStorageObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public final class Island extends IslandStorageObject<UUID> {

    // Object Values
    @Getter @Setter private String islandName;
    @Getter private final UUID islandOwner;
    @Setter private IslandLocation islandLocation;

    private List<UUID> islandMembers;

    // Island Upgrade Related Levels
    private int worldBorderSize = 150;
    private int oreDropUpgrade, farmDropUpgrade, mobDropUpgrade, mobSpawnUpgrade, xpUpgrade;
    private boolean keepInventoryUpgrade;

    public Island(String islandName, UUID islandUUID, UUID islandOwner) {
        super(islandUUID);
        this.islandName = islandName;
        this.islandOwner = islandOwner;
    }

}
