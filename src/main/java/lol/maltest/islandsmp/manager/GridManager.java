package lol.maltest.islandsmp.manager;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.entities.sub.IslandLocation;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class GridManager {

    private IslandSMP plugin;
    private World gridWorld;

    public GridManager(IslandSMP plugin) {
        this.plugin = plugin;
        this.gridWorld = Bukkit.getWorld("island");
    }

    public Location getFreeGridLocation() {
        int gridSize = IslandCache.activeIslands.size();
        int columns = 29999; // Set the number of columns as needed

        // The x-coordinate is the modulus of gridSize and columns
        int x = gridSize % columns;

        // The z-coordinate is the integer division of gridSize by columns
        int z = gridSize / columns;

        // Calculate the absolute position based on the grid position and spacing
        int spacing = 1000; // Adjust the spacing as needed
        int xOffset = x * spacing;
        int zOffset = z * spacing;
        return new Location(gridWorld, xOffset, -14, zOffset);
    }


    public void disbandIsland(UUID islandUuid) {
        Island island = IslandCache.getIsland(islandUuid);

        if(island == null) {
            System.out.println("Tried to disband a invalid island");
            return;
        }

        Player islandOwner = Bukkit.getPlayer(island.getIslandOwner());

        if(islandOwner == null) {
            System.out.println("Tried to disband island whilst owner was offline!");
            return;
        }

        islandOwner.closeInventory();

        islandOwner.getInventory().clear();
        islandOwner.getEnderChest().clear();

        for(IslandMember islandMember : island.getIslandMembers()) {
            Player islandPlayer = Bukkit.getPlayer(islandMember.getPlayerUuid());

            if(islandPlayer == null) {
                plugin.getUserCache().cacheProfileFromDatabase(islandMember.getPlayerUuid());

                User islandUser = UserCache.getUser(islandMember.getPlayerUuid());

                islandUser.setIslandUUID(plugin.getNullUuid());

                plugin.getUserCache().removeFromCacheAndSaveToDatabase(islandUser.getPlayer());
                continue;
            }

            User user = UserCache.getUser(islandPlayer.getUniqueId());

            user.setIslandUUID(null);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "espawn " + islandPlayer.getName());

            // Set all islandid in their json to null
        }

        island.setTrustedMembers(null);
        island.setIslandMembers(null);
        island.setIslandWarps(null);
        island.setRankPermissions(null);

        // Set null to save some memory in future idk

        island.setIslandOwner(plugin.getNullUuid());
        island.setIslandName(islandOwner.getName() +"'s deleted island");
    }

    public void createIsland(Player player) {
        Island island = new Island(player.getName() + "'s Island", UUID.randomUUID(), player.getUniqueId());
        IslandCache.activeIslands.add(island);

        User user = UserCache.getUser(player.getUniqueId());
        user.setIslandUUID(island.getIslandUUID());

        new BukkitRunnable() {
            @Override
            public void run() {

                IslandLocation islandLocation;
                Location rawGridLocation = getFreeGridLocation();

                try {
                    islandLocation = pasteSchematic(gridWorld, new File(plugin.getDataFolder(), "schematics/schem.schem"), BlockVector3.at(rawGridLocation.getX(), rawGridLocation.getY(), rawGridLocation.getZ()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                island.setIslandLocation(islandLocation);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(island.getIslandLocation().getSpawnLocation());
                    }
                }.runTask(plugin);

            }
        }.runTaskAsynchronously(plugin);
    }

    public IslandLocation pasteSchematic(World world, File schematicFile, BlockVector3 pasteLocation) throws IOException {
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
//                world.getWorldBorder().setCenter(pasteCenter.getX(), pasteCenter.getZ());
//                world.getWorldBorder().setSize(radius * 2);

                // Shift the region to coincide with the paste location instead of schematic's origin
                BlockVector3 pastedMaxPoint = pasteLocation.subtract(schematicSize.subtract(BlockVector3.ONE));

                // Retrieve the minimum and maximum X coordinates
                int minX = pasteLocation.getX();
                int maxX = pastedMaxPoint.getX();
                int minZ = pasteLocation.getZ();
                int maxZ = pastedMaxPoint.getZ();

                return new IslandLocation(minX, maxX, minZ, maxZ);

            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
