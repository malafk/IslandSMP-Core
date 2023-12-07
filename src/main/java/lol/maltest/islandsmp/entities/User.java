package lol.maltest.islandsmp.entities;

import lol.maltest.islandsmp.cache.IslandCache;
import lombok.Getter;
import lombok.Setter;
import lol.maltest.islandsmp.entities.type.PlayerStorageObject;

import java.util.UUID;

@Getter
@Setter
public final class User extends PlayerStorageObject<UUID> {

    // Object Values
    private UUID islandUUID;
    private transient Island island;

    // Constructor
    public User(UUID player) {
        super(player);
    }

    public void setIslandUUID(UUID islandUUID) {
        this.islandUUID = islandUUID;
        this.island = IslandCache.getIsland(islandUUID);
    }
}
