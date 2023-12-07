package lol.maltest.islandsmp;

import co.aikar.commands.PaperCommandManager;
import lol.maltest.islandsmp.commands.IslandCommand;
import lol.maltest.islandsmp.manager.IslandCreationManager;
import lol.maltest.islandsmp.storage.UserStorage;
import lombok.Getter;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.listener.JoinListener;
import lol.maltest.islandsmp.listener.LeaveListener;
import lol.maltest.islandsmp.storage.IslandStorage;
import lol.maltest.islandsmp.utils.VoidChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Queue;

public final class IslandSMP extends JavaPlugin {

    // Identifiers
    @Getter private static IslandSMP instance;

    private IslandStorage islandStorage;
    private UserStorage userStorage;

    private IslandCreationManager islandCreationManager;

    @Getter private IslandCache islandCache;
    @Getter private UserCache userCache;


    @Override
    public void onEnable() {

        // Register singleton
        instance = this;
        saveDefaultConfig();

        String worldName = IslandSMP.getInstance().getConfig().getString("world-name");

        // Check if the world already exists
        if (worldExists(worldName)) {
            return;
        }

        // Create the world if it doesn't exist
        WorldCreator wc = new WorldCreator(worldName);
        wc.generator(new VoidChunkGenerator());
        wc.createWorld();

        islandStorage = new IslandStorage();
        userStorage = new UserStorage();

        islandCreationManager = new IslandCreationManager();

        islandCache = new IslandCache(islandCreationManager, islandStorage);
        userCache = new UserCache(userStorage);

        getServer().getPluginManager().registerEvents(new JoinListener(userCache), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(userCache), this);

        loadCommands();

    }

    public void loadCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);

        commandManager.registerCommand(new IslandCommand(this));
    }

    @Override
    public void onDisable() {
        for (Island island : IslandCache.activeIslands) {
            islandStorage.saveAsync(island);
        }
    }

    private boolean worldExists(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }
}
