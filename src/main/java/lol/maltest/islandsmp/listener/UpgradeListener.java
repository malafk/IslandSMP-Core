package lol.maltest.islandsmp.listener;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.manager.UpgradeManager;
import lol.maltest.islandsmp.utils.UpgradeType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class UpgradeListener implements Listener {

    private IslandSMP plugin;

    public UpgradeListener(IslandSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Island island = UserCache.getUser(player.getUniqueId()).getIsland();

        if (island == null) return;


        boolean keepInv = plugin.getUpgradeManager().getCurrentLevel(UpgradeType.KEEP_INVENTORY, island) != 0;
        boolean keepXp = plugin.getUpgradeManager().getCurrentLevel(UpgradeType.KEEP_XP, island) != 0;

        if (keepInv) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }

        if(keepXp) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
    }
}
