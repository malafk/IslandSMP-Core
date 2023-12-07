package lol.maltest.islandsmp.cache;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.manager.IslandCreationManager;
import lol.maltest.islandsmp.storage.IslandStorage;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class IslandCache {

    // Identifiers
    public static final List<Island> activeIslands = new ArrayList<>();
    private final IslandCreationManager islandCreationManager;
    private final IslandStorage islandStorage;

    public IslandCache(IslandCreationManager islandCreationManager, IslandStorage islandStorage) {
        this.islandCreationManager = islandCreationManager;
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

    public void createIsland(String islandName, UUID islandOwner) {
        Island island = new Island(islandName, UUID.randomUUID(), islandOwner);
        activeIslands.add(island);

        // create island method or just do it in the island constructor

        Bukkit.getScheduler().runTaskLaterAsynchronously(IslandSMP.getInstance(), () -> {
            Player player = Bukkit.getPlayer(islandOwner);
            if (player == null) {
                return;
            }
            player.teleportAsync(LocationUtils.locFromString(island.getIslandCentreLocation()));
        }, 40L);
    }
}
