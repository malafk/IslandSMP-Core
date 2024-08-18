package lol.maltest.islandsmp.listener;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.utils.*;
import org.bukkit.block.*;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowSquid;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class IslandListener implements Listener {

    private IslandSMP plugin;

    public IslandListener(IslandSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void leafDecayEvent(LeavesDecayEvent e) {
        if(e.getBlock().getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) e.setCancelled(true);
    }

    @EventHandler
    public void leafDecayEvent(BlockBurnEvent e) {
        if(e.getBlock().getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) e.setCancelled(true);
    }

    @EventHandler
    public void onFireSpread(BlockIgniteEvent e) {
        if(e.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) || e.getCause().equals(BlockIgniteEvent.IgniteCause.FIREBALL)) return;
        if(e.getBlock().getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEntityEvent(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player player)) return;
        if(!(e.getEntity() instanceof Player target)) return;

        User playerUser = UserCache.getUser(player.getUniqueId());
        User targetUser = UserCache.getUser(target.getUniqueId());

        UUID playerIsland = playerUser.getIslandUUID();
        UUID targetIsland = targetUser.getIslandUUID();

        if(playerIsland != targetIsland) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(@NotNull EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        if(!player.getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) return;

        Island island = plugin.getBorderManager().getPlayerIsland(player);

        if(island == null) return;

        if(island.isTrustedOrIslandMember(player)) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        cancelEventIfNoPermission(e, e.getPlayer(), Permission.BREAK);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        cancelEventIfNoPermission(e, e.getPlayer(), Permission.PLACE);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player player)) return;
        if(e.getEntity() instanceof Player) return;

        Island island = plugin.getBorderManager().getPlayerIsland(player);
        if(island == null) return;

        if (!island.isSettingActive(Setting.ENTITY_INTERACT)) return;

        if(island.isTrustedOrIslandMember(player)) return;

        e.setCancelled(true);
        e.getDamager().sendMessage(HexUtils.colour(LanguageUtil.messageIslandCantInterEntity));
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) return;

        Island island = plugin.getBorderManager().getIsland(event.getLocation());
        if (island == null) return;


        if(event.getEntity() instanceof GlowSquid) return;

        if (event.getEntity() instanceof Animals) {
            if (island.isSettingActive(Setting.ANIMAL_SPAWNING)) {
                event.setCancelled(true);
            }
        } else {
            if (plugin.newPlayers.contains(island.getIslandOwner()) || island.isSettingActive(Setting.MOB_SPAWNING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().equalsIgnoreCase("/worldborder")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(HexUtils.colour("&c/worldborder command is disabled"));
        }
    }



    @EventHandler
    public void onPlayerInteractDetect(PlayerInteractEntityEvent e) {
        cancelEventIfNoPermission(e, e.getPlayer(), Permission.PLACE);
    }

    @EventHandler
    public void onPlayerInteractDetect(PlayerItemFrameChangeEvent e) {
        cancelEventIfNoPermission(e, e.getPlayer(), Permission.PLACE);
    }

    List<String> blocksNoInteract = Arrays.asList("pressure plate", "button", "door", "gate");

    @EventHandler
    public void onPlayerInteractDetect(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) return;
        BlockState state = e.getClickedBlock().getState();
        if(state instanceof InventoryHolder) {
            cancelEventIfNoPermission(e, e.getPlayer(), Permission.CONTAINER);
            return;
        }

        String blockName = e.getClickedBlock().getType().name().toLowerCase().replace("_", " ");
        if(blocksNoInteract.stream().anyMatch(blockName::contains)) {
            cancelEventIfNoPermission(e, e.getPlayer(), Permission.PLACE);
        }
    }

    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent e) {
        if(e.getEntityType() != EntityType.PHANTOM) return;
        if(!e.getEntity().getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) return;

        Island island = plugin.getBorderManager().getIsland(e.getLocation());

        if(island == null) return;

        if(!island.isSettingActive(Setting.PHANTOMS)) return;

        e.getEntity().remove();
    }

    @EventHandler
    public void onCreeperExplode(@NotNull ExplosionPrimeEvent e) {
        if(e.getEntityType().equals(EntityType.CREEPER)) e.setCancelled(true);

        if(!e.getEntity().getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) return;

        Island island = plugin.getBorderManager().getIsland(e.getEntity().getLocation());

        if(island == null) return;

        if(!island.isSettingActive(Setting.EXPLOSIONS)) return;

        e.setCancelled(true);
    }

    private void cancelEventIfNoPermission(Cancellable e, @NotNull Player player, Permission permission) {
        if (!player.getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) return;

        Island island = plugin.getBorderManager().getPlayerIsland(player);
        if(island == null) return;

        e.setCancelled(!PermUtil.hasPermissionInCurrentIsland(player, permission));
    }

    private boolean cancelEventIfSetting(Cancellable e, @NotNull Player player, Setting setting) {
        if (!player.getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) return false;

        Island island = plugin.getBorderManager().getPlayerIsland(player);
        if(island == null) return false;

        e.setCancelled(island.isSettingActive(setting));
        return true;
    }
}
