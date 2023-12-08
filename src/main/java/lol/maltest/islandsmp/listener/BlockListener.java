package lol.maltest.islandsmp.listener;

import lol.maltest.islandsmp.IslandSMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class BlockListener implements Listener {

    private IslandSMP plugin;

    public BlockListener(IslandSMP plugin) {
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
}
