package lol.maltest.islandsmp.listener;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.manager.UpgradeManager;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.PermUtil;
import lol.maltest.islandsmp.utils.Permission;
import lol.maltest.islandsmp.utils.UpgradeType;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class UpgradeListener implements Listener {

    private IslandSMP plugin;

    public UpgradeListener(IslandSMP plugin) {
        this.plugin = plugin;
    }

    // KEEP_INVENTORY KEEP_XP
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


    // ORE_DROPS
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Island island = UserCache.getUser(player.getUniqueId()).getIsland();

        if (island == null) return;

        int fortuneLevel = island.getLevel(UpgradeType.ORE_DROPS);
        if(fortuneLevel == 0) return;

        Material blockType = event.getBlock().getType();

        if (!blockType.toString().contains("ORE")) return;
        ItemStack drop = null;

        Collection<ItemStack> drops = event.getBlock().getDrops();
        if (!drops.isEmpty()) {
            drop = drops.iterator().next();
        }

        if(drop == null || drop.getType().toString().contains("ORE")) return; // stop duplicating ore with silk touch

        // Apply Fortune effect
        for (int i = 1; i <= fortuneLevel; i++) {
            if (Math.random() < 0.5) {
                drop.setAmount(drop.getAmount() + 1);
            }
        }

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
    }

    // MOB_DROPS
    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        // Check if the entity was killed by a player
        if (event.getEntity().getKiller() == null) return;

        Player player = event.getEntity().getKiller();
        Island island = UserCache.getUser(player.getUniqueId()).getIsland();

        // Early return if the player doesn't have an island
        if (island == null) return;

        int lootingUpgradeLevel = island.getLevel(UpgradeType.MOB_DROPS);
        if(lootingUpgradeLevel == 0) return;
        List<ItemStack> drops = event.getDrops();

        // Apply looting-like effect to each drop
        for (ItemStack drop : drops) {
            for (int i = 1; i <= lootingUpgradeLevel; i++) {
                if (Math.random() < 0.5) {
                    drop.setAmount(drop.getAmount() + 1);
                }
            }
        }
    }

    // FARM_DROPS
    @EventHandler
    public void onFarmBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = UserCache.getUser(player.getUniqueId());
        Island island = user.getIsland();
        // Early return if the player doesn't have an island
        if (island == null) return;

        if(!PermUtil.hasPermission(user, Permission.BREAK)) return;

        Material blockType = event.getBlock().getType();

        // Early return if the block is not a farming material
        if (!isFarmingMaterial(blockType)) return;

        int farmUpgradeLevel = island.getLevel(UpgradeType.FARM_DROPS);
        if(farmUpgradeLevel == 0) return;
        double chance = getFarmDropChance(farmUpgradeLevel);

        // Process each drop
        Collection<ItemStack> drops = event.getBlock().getDrops();
        for (ItemStack drop : drops) {
            if (Math.random() < chance) {
                drop.setAmount(drop.getAmount() * 2); // Double the drops if condition met
            }

            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
        }
    }

    // MOB_SPAWNS
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Check if the creature is spawned from a spawner
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) return;

        // Implement logic to determine the player associated with the spawner
        Island island = plugin.getBorderManager().getIsland(event.getLocation());
        if (island == null) return;

        int spawnUpgradeLevel = island.getLevel(UpgradeType.MOB_SPAWNS);
        double spawnMultiplier = getSpawnMultiplier(spawnUpgradeLevel);

        // Determine the total number of mobs to spawn, including the original one
        int totalMobsToSpawn = (int) Math.ceil(spawnMultiplier);

        for (int i = 1; i < totalMobsToSpawn; i++) {
            event.getLocation().getWorld().spawnEntity(event.getLocation(), event.getEntityType());
        }
    }

    // XP_DROPS
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Check if the entity is an instance of Animals
        if (!(event.getEntity() instanceof Animals)) return;

        // Check if the entity was killed by a player
        if (event.getEntity().getKiller() == null) return;

        Player player = event.getEntity().getKiller();
        Island island = UserCache.getUser(player.getUniqueId()).getIsland();

        // Early return if the player doesn't have an island
        if (island == null) return;

        int xpUpgradeLevel = island.getLevel(UpgradeType.XP_DROPS);

        // Get the current dropped XP and multiply it by the upgrade factor
        int droppedXP = event.getDroppedExp();
        double xpMultiplier = getXpMultiplier(xpUpgradeLevel);
        int newDroppedXP = (int) Math.round(droppedXP * xpMultiplier);

        // Set the new XP drop amount
        event.setDroppedExp(newDroppedXP);
    }

    private double getXpMultiplier(int xpUpgradeLevel) {
        return switch (xpUpgradeLevel) {
            case 1 -> 1.2; // 1.2x xp drop rate
            case 2 -> 1.4; // 1.4x xp drop rate
            case 3 -> 1.6; // 1.6x xp drop rate
            case 4 -> 2.0; // 2x xp drop rate
            default -> 1.0; // No extra XP
        };
    }


    private double getSpawnMultiplier(int spawnUpgradeLevel) {
        return switch (spawnUpgradeLevel) {
            case 1 -> 1.1; // 10% extra mob spawn rate
            case 2 -> 1.5; // 50% extra mob spawn rate
            case 3 -> 2.0; // 100% extra mob spawn rate
            default -> 1.0; // No extra mobs
        };
    }


    private boolean isFarmingMaterial(Material material) {
        return switch (material) {
            case WHEAT, CARROTS, POTATOES, BEETROOTS, SUGAR_CANE, NETHER_WART -> true;
            default -> false;
        };
    }

    private double getFarmDropChance(int farmUpgradeLevel) {
        return switch (farmUpgradeLevel) {
            case 1 -> 0.25; // 25% chance
            case 2 -> 0.50; // 50% chance
            case 3 -> 0.75; // 75% chance
            case 4 -> 1.00; // 100% chance
            default -> 0.00; // No extra drops
        };
    }


    @EventHandler
    public void onPortalUse(PlayerPortalEvent e) {
        if(!e.getTo().getWorld().getName().equals("world_nether")) return;
        if(e.getFrom().getWorld().getName().equalsIgnoreCase("spawn")) {
            e.setCancelled(true);
            e.getPlayer().chat("/is go");
            return;
        }


        Player player = e.getPlayer();
        Island island = UserCache.getUser(player.getUniqueId()).getIsland();

        if (island == null) return;

        if(island.getLevel(UpgradeType.NETHER_ACCESS) != 1) {
            e.setCancelled(true);
            player.sendMessage(HexUtils.colour("&cYour island can't access the nether yet! Unlock it via island upgrades"));
        }
    }
}
