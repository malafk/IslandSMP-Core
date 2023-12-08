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

    @Subcommand("go")
    public void onIslandGoCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() == null) {
            player.sendActionBar(HexUtils.colour("&dno island dork"));
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

    @Subcommand("test")
    @CommandPermission("minecraft.operator")
    public void onTestCommand(Player player) {
//
//        int spawnX = -181;
//        int spawnY = 94;
//        int spawnZ = 134;
//
//
//        player.sendMessage("Hopefully pasting");
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                try {
//                    pasteSchematic(player.getWorld(), new File(plugin.getDataFolder(), "schematics/schem.schem"), BlockVector3.at(0, -14, 0));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }.runTaskAsynchronously(plugin);

        plugin.getGridManager().pasteTest(player, player.getLocation());
    }

    public void pasteSchematic(World world, File schematicFile, BlockVector3 pasteLocation) throws IOException {
        if (!schematicFile.exists()) {
            throw new IllegalStateException("Schematic file does not exist.");
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(world.getName()))) {
            try (FileInputStream fis = new FileInputStream(schematicFile);
                 ClipboardReader reader = ClipboardFormats.findByFile(schematicFile).getReader(fis)) {
                ClipboardHolder holder = new ClipboardHolder(reader.read());

                // Calculate the center of the schematic
                BlockVector3 schematicSize = holder.getClipboard().getDimensions();
                BlockVector3 centerOffset = schematicSize.divide(2);
                BlockVector3 pasteCenter = pasteLocation.add(centerOffset);

                pasteCenter = BlockVector3.at(-pasteCenter.getX(), pasteCenter.getY(), pasteCenter.getZ());

                // Paste the schematic
                Operation operation = holder.createPaste(editSession)
                        .to(pasteLocation)
                        .ignoreAirBlocks(true)
                        .build();
                Operations.completeLegacy(operation);

                // Set world border
                int radius = Math.max(schematicSize.getX(), schematicSize.getZ()) / 2;
                world.getWorldBorder().setCenter(pasteCenter.getX(), pasteCenter.getZ());
                world.getWorldBorder().setSize(radius * 2);

                // Shift the region to coincide with the paste location instead of schematic's origin
                BlockVector3 pastedMaxPoint = pasteLocation.subtract(schematicSize.subtract(BlockVector3.ONE));

                // Retrieve the minimum and maximum X coordinates
                int minX = pasteLocation.getX();
                int maxX = pastedMaxPoint.getX();
                int minZ = pasteLocation.getZ();
                int maxZ = pastedMaxPoint.getZ();

                System.out.println("minX: " + minX + " maxX" + maxX);

                Bukkit.broadcastMessage("minZ:" + minZ + " maxZ:" + maxZ);

            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
