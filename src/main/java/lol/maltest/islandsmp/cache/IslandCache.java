package lol.maltest.islandsmp.cache;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.storage.IslandStorage;
import lol.maltest.islandsmp.entities.Island;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class IslandCache {

    // Identifiers
    public static final List<Island> activeIslands = new ArrayList<>();
    public final IslandStorage islandStorage;

    public IslandCache(IslandStorage islandStorage) {
        this.islandStorage = islandStorage;
        activeIslands.addAll(islandStorage.loadAllObjects());

        Bukkit.getScheduler().runTaskTimerAsynchronously(IslandSMP.getInstance(), () -> {
            for (Island island : activeIslands) {
                islandStorage.saveAsync(island);
            }
        }, 24000L, 24000L);
    }

    // Methods
    public static Island getIsland(UUID islandUUID) {
        for (Island island : activeIslands) {
            if (island.getIslandUUID().equals(islandUUID)) {
                return island;
            }
        }

        return null;
    }

    public void deleteIslandFromDatabase(UUID islandUuid) {
        System.out.println("Deleting islandUUid " + islandUuid);
        islandStorage.deleteObject(islandUuid);
    }
}
