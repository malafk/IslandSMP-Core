package lol.maltest.islandsmp.manager;

import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.github.yannicklamprecht.worldborder.impl.WorldBorder;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.utils.HexUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BorderManager {

    private IslandSMP plugin;
    private WorldBorderApi worldBorderApi;

    private Map<UUID, Island> playerIslandMap = new HashMap<>();
    private HashMap<UUID, Integer> suspiciousPlayers = new HashMap<>();

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

    public Island getIsland(Location location) {
        if(!location.getWorld().getName().equals(plugin.getWorldName()))
            return null;

        for (Island island : IslandCache.activeIslands) {
            if (island.isLocationInsideIsland(location)) {
                return island;
            }
        }

        return null;
    }

    public Island getPlayerIsland(Player player) {
        return playerIslandMap.get(player.getUniqueId());
    }

    public List<Player> getPlayersOnIsland(Island island) {
        List<Player> playersOnIsland = new LinkedList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Objects.equals(playerIslandMap.get(player.getUniqueId()), island)) {
                playersOnIsland.add(player);
            }
        }
        return playersOnIsland;
    }


    public void startBorderTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Island island = getIsland(player.getLocation());
                    if(island != null) {
                        worldBorderApi.setBorder(player, island.getWorldBorderSize(), island.getIslandLocation().getMiddleLocation());
                        playerIslandMap.put(player.getUniqueId(), island);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 5);

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(!player.getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) {
                        continue;
                    }

                    Island island = getPlayerIsland(player);

                    if(island == null) continue;

                    NPC npc = GridManager.getNpcByName(island.getIslandUUID() + "");
                    if (npc == null) return;

                    npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

                    Villager villager = (Villager) npc.getEntity();
                    villager.setProfession(Villager.Profession.FARMER);


                    Hologram hologram = DHAPI.getHologram(island.getIslandUUID() + "");
                    if(hologram != null) {
                        hologram.setLocation(npc.getEntity().getLocation().add(0, 2.75, 0));
                        hologram.realignLines();
                        hologram.save();
                    }


                }
            }
        }.runTaskTimer(plugin, 0, 20 * 3);

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(!player.getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) {
                        suspiciousPlayers.remove(player.getUniqueId());
                        continue;
                    }

                    Island island = getPlayerIsland(player);
                    if(island == null || island.getIslandOwner().equals(plugin.getNullUuid())) {


                        int count = suspiciousPlayers.getOrDefault(player.getUniqueId(), 0);
                        System.out.println(count + " " + player.getName());
                        count++;

                        if (count >= 3) {
                            player.sendMessage(HexUtils.colour("&cYou were in a island that wasn't owned by anyone. Sending you to spawn!"));
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "espawn " + player.getName());
                            suspiciousPlayers.remove(player.getUniqueId());
                        } else {
                            suspiciousPlayers.put(player.getUniqueId(), count);
                        }

                    }
                }
            }
        }.runTaskTimer(plugin, 0, 10);
    }
}
