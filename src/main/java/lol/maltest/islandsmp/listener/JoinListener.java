package lol.maltest.islandsmp.listener;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public final class JoinListener implements Listener {

    // Identifiers
    private final UserCache userCache;

    public JoinListener(UserCache userCache) {
        this.userCache = userCache;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Identifiers
        UUID player = event.getPlayer().getUniqueId();

        // Cache the profile
        userCache.cacheProfileFromDatabase(player);

        User user = UserCache.getUser(player);

        if(!event.getPlayer().hasPlayedBefore()) {
            IslandSMP.getInstance().newPlayers.add(event.getPlayer().getUniqueId());
            event.getPlayer().setPlayerTime(9000, false);

            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().resetPlayerTime();
                }
            }.runTaskLater(IslandSMP.getInstance(), 20 * 60 * 15);
        }

    }
}
