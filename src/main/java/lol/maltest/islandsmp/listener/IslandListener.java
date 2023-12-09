package lol.maltest.islandsmp.listener;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.utils.PermUtil;
import lol.maltest.islandsmp.utils.Permission;
import org.bukkit.block.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
        if(e.getBlock().getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) e.setCancelled(true);
    }


    public User doChecksAndGetUser(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) {
            player.kick();
            return null;
        }

        return user;
    }

    @EventHandler
    public void onBlockBreakEvent(@NotNull EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        if(player.getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) e.setCancelled(true);
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
    public void onPlayerInteractDetect(PlayerInteractEntityEvent e) {
        cancelEventIfNoPermission(e, e.getPlayer(), Permission.PLACE);
    }
    List<String> blocksNoInteract = Arrays.asList("pressure plate", "button", "door", "gate");

    @EventHandler
    public void onPlayerInteractDetect(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) return;
        BlockState state = e.getClickedBlock().getState();
        if(state instanceof InventoryHolder) {
            cancelEventIfNoPermission(e, (Player) e.getPlayer(), Permission.CONTAINER);
            return;
        }

        String blockName = e.getClickedBlock().getType().name().toLowerCase().replace("_", " ");
        if(blocksNoInteract.stream().anyMatch(blockName::contains)) {
            cancelEventIfNoPermission(e, e.getPlayer(), Permission.PLACE);
        }
    }

    @EventHandler
    public void onPlayerInteractDetect(PlayerItemFrameChangeEvent e) {
        cancelEventIfNoPermission(e, e.getPlayer(), Permission.PLACE);
    }

    @EventHandler
    public void onCreeperExplode(@NotNull ExplosionPrimeEvent e) {
        if(e.getEntityType().equals(EntityType.CREEPER)) e.setCancelled(true);
    }

    private void cancelEventIfNoPermission(Cancellable e, @NotNull Player player, Permission permission) {
        if (!player.getWorld().getName().equalsIgnoreCase(plugin.getWorldName())) return;

        Island island = plugin.getBorderManager().getPlayerIsland(player);
        if(island == null) return;

        e.setCancelled(!PermUtil.hasPermissionInCurrentIsland(player, permission));
    }
}
