package lol.maltest.islandsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.FlatRegionFunction;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.FlatRegionVisitor;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.Regions;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.MenuItem;
import lol.maltest.islandsmp.menu.sub.IslandMainMenu;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.LanguageUtil;
import lol.maltest.islandsmp.utils.MenuUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@CommandAlias("island|is")
public class IslandCommand extends BaseCommand {

    private IslandSMP plugin;

    public IslandCommand(IslandSMP plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onIslandCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        if(user.getIsland() == null) {
            plugin.getGridManager().createIsland(player);
            return;
        }

        Menu menu = new IslandMainMenu();
        menu.open(player);
    }

    @Subcommand("setwarp")
    public void onSetWarpCommand(Player player, String warpName) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() == null) {
            player.sendActionBar(HexUtils.colour(LanguageUtil.publicNeedIsland));
            return;
        }

        // TODO: Check if user has permmission

        if(user.getIsland().getFreeWarps() == 0) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorWarpsMax));
            return;
        }

        user.getIsland().setNewWarp(warpName, player);
        player.sendMessage(HexUtils.colour(LanguageUtil.messageWarpCreated.replace("%warp%", warpName)));

        // Todo: alert all island members that they put a warp
    }

    @Subcommand("go")
    public void onIslandGoCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() == null) {
            player.sendActionBar(HexUtils.colour(LanguageUtil.publicNeedIsland));
            return;
        }

        player.teleport(user.getIsland().getIslandLocation().getSpawnLocation());
    }

    @Subcommand("create")
    public void onCreateCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() != null) {
            player.sendActionBar(HexUtils.colour("&dcʏᴏᴜ ᴀʟʀᴇᴀᴅʏ ʜᴀᴠᴇ ᴀɴ ɪsʟᴀɴᴅ!"));
            return;
        }

        plugin.getGridManager().createIsland(player);
    }

    @Subcommand("forcesave")
    @CommandPermission("minecraft.operator")
    public void onForceSaveCommand(Player player) {
        for (Island island : IslandCache.activeIslands) {
            plugin.getIslandCache().islandStorage.saveAsync(island);
        }
        player.sendMessage("done");
    }

}
