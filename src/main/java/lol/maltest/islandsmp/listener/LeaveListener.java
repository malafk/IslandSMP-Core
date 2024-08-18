package lol.maltest.islandsmp.listener;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class LeaveListener implements Listener {

    // Identifiers
    private final UserCache userCache;

    public LeaveListener(UserCache userCache) {
        this.userCache = userCache;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        // Identifiers
        UUID player = event.getPlayer().getUniqueId();

        Island island = UserCache.getUser(player).getIsland();
        int online = -1;
        if(island != null) {
            for(IslandMember islandMember : island.getIslandMembers()) {
                Player player1 = Bukkit.getPlayer(islandMember.getPlayerUuid());
                if(player1 != null) online++;
            }
        }

        if(online < 1) {
            IslandCache.islandsWithPlayersOnline.remove(island);
        }

        // Remove profile from cache
        userCache.removeFromCacheAndSaveToDatabase(player);

        IslandSMP.getInstance().getQueueManager().removePlayer(player);
    }
}
