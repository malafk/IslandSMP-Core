package lol.maltest.islandsmp.entities;

import lol.maltest.islandsmp.entities.type.IslandStorageObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public final class Island extends IslandStorageObject<UUID> {

    // Object Values
    private final String islandName;
    private final UUID islandOwner;
    private String islandCentreLocation;
    private List<UUID> islandMembers;

    // Island Upgrade Related Levels
    private int oreDropUpgrade, farmDropUpgrade, mobDropUpgrade, mobSpawnUpgrade, xpUpgrade;
    private boolean keepInventoryUpgrade;

    public Island(String islandName, UUID islandUUID, UUID islandOwner) {
        super(islandUUID);
        this.islandName = islandName;
        this.islandOwner = islandOwner;
    }
}
