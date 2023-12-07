package lol.maltest.islandsmp.listener;

import lol.maltest.islandsmp.cache.UserCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

    }
}
