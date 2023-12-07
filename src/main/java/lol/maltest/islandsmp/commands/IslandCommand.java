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
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.utils.HexUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
        player.sendMessage("helol");
    }

    @Subcommand("create")
    public void onCreateCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() != null) {
            player.sendActionBar(HexUtils.colour("&cʏᴏᴜ ᴀʟʀᴇᴀᴅʏ ʜᴀᴠᴇ ᴀɴ ɪsʟᴀɴᴅ!"));
            return;
        }

        IslandSMP.getInstance().getIslandCache().createIsland("test", player.getUniqueId());
    }

    @Subcommand("test")
    @CommandPermission("minecraft.operator")
    public void onTestCommand(Player player) {

        int spawnX = -181;
        int spawnY = 94;
        int spawnZ = 134;


        try {
            player.sendMessage("Hopefully pasting");
            pasteSchematic(player.getWorld(), new File(plugin.getDataFolder(), "schematics/schem.schem"), BlockVector3.at(0, -14, 0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

                pasteCenter = BlockVector3.at(pasteCenter.getX() - 368, pasteCenter.getY(), pasteCenter.getZ());

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

                // Change biome
                for (int x = pasteLocation.getX(); x < pasteLocation.getX() + schematicSize.getX(); x++) {
                    for (int z = pasteLocation.getZ(); z < pasteLocation.getZ() + schematicSize.getZ(); z++) {
                        BlockVector3 position = BlockVector3.at(x, 0, z);
                        editSession.setBiome(position, BiomeTypes.JUNGLE);
                    }
                }
            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
