package lol.maltest.islandsmp.manager;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.github.yannicklamprecht.worldborder.impl.WorldBorder;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.entities.Island;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BorderManager {

    private IslandSMP plugin;
    private WorldBorderApi worldBorderApi;

    public BorderManager(IslandSMP plugin) {
        this.plugin = plugin;

        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        if (worldBorderApiRegisteredServiceProvider == null) {
            Bukkit.getLogger().info("No WorldBorderAPI not found");
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();

        startBorderTask();
    }

    public void startBorderTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    for(Island island : IslandCache.activeIslands) {
                        worldBorderApi.setBorder(player, island.getWorldBorderSize(), island.getIslandLocation().getMiddleLocation());
                        System.out.println("Set " + player.getName() +"  border " + island.getIslandLocation().getMiddleLocation());
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }
}
