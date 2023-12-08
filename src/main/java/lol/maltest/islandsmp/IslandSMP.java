package lol.maltest.islandsmp;

import co.aikar.commands.PaperCommandManager;
import lol.maltest.islandsmp.commands.IslandCommand;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.manager.BorderManager;
import lol.maltest.islandsmp.manager.GridManager;
import lol.maltest.islandsmp.storage.UserStorage;
import lol.maltest.islandsmp.utils.LanguageUtil;
import lol.maltest.islandsmp.utils.MenuUtil;
import lombok.Getter;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.listener.JoinListener;
import lol.maltest.islandsmp.listener.LeaveListener;
import lol.maltest.islandsmp.storage.IslandStorage;
import lol.maltest.islandsmp.utils.VoidChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class IslandSMP extends JavaPlugin {

    // Identifiers
    @Getter private static IslandSMP instance;

    private IslandStorage islandStorage;
    private UserStorage userStorage;

    @Getter private GridManager gridManager;
    @Getter private BorderManager borderManager;

    @Getter private IslandCache islandCache;
    @Getter private UserCache userCache;

    @Getter private String worldName;

    @Override
    public void onEnable() {

        // Register singleton
        instance = this;
        saveDefaultConfig();

        worldName = IslandSMP.getInstance().getConfig().getString("world-name");

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

        gridManager = new GridManager(this);
        borderManager = new BorderManager(this);

        islandCache = new IslandCache(islandStorage);
        userCache = new UserCache(userStorage);

        new LanguageUtil(this);
        new MenuUtil(this);

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
        Bukkit.getLogger().info("Saving data on shutdown...");
        for (Island island : IslandCache.activeIslands) {
            islandStorage.save(island);
        }

        for(User user : UserCache.users) {
            userStorage.save(user);
        }
        Bukkit.getLogger().info("Saved data!");
    }

    private boolean worldExists(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }
}
