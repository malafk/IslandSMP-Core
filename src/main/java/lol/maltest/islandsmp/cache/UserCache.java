package lol.maltest.islandsmp.cache;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.manager.GridManager;
import lol.maltest.islandsmp.storage.UserStorage;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.utils.HexUtils;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserCache {

    // Identifiers
    public static final List<User> users = new ArrayList<>();
    private final UserStorage userStorage;

    public UserCache(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Methods
    public static User getUser(UUID playerUUID) {
        for (User user : users) {
            if (user.getPlayer().equals(playerUUID)) {
                return user;
            }
        }

        return null;
    }

    public void cacheProfileFromDatabase(UUID identifier) {
        userStorage.getObject(identifier).whenComplete((user, throwable) -> {
            if (throwable != null) {
                return;
            }

            if (user != null) {
                users.add(user);


                if(user.getIslandUUID() != null) {
                    if(user.getIslandUUID() == IslandSMP.getInstance().getNullUuid()) {
                        Player player = Bukkit.getPlayer(user.getIslandUUID());
                        player.sendMessage(HexUtils.colour("&cYour island got disbanded or you got kicked from it whilst you were offline."));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "espawn " + player.getName());
                        return;
                    }
                    Island is = IslandCache.getIsland(user.getIslandUUID());

                    if(is == null) {
                        System.out.println(user.getPlayer() + " had a invalid island id");
                        return;
                    }

                    Island island = IslandCache.getIsland(user.getIslandUUID());
                    IslandCache.islandsWithPlayersOnline.add(island);

                    user.setIsland(island);
                }

                return;
            }

            User newUser = new User(identifier);
            users.add(newUser);
        });
    }

    public void cacheProfileFromDatabase(UUID identifier, Runnable callback) {
        userStorage.getObject(identifier).whenComplete((user, throwable) -> {
            if (throwable != null) {
                return;
            }

            if (user != null) {
                users.add(user);


                if(user.getIslandUUID() != null) {
                    if(user.getIslandUUID() == IslandSMP.getInstance().getNullUuid()) {
                        Player player = Bukkit.getPlayer(user.getIslandUUID());
                        player.sendMessage(HexUtils.colour("&cYour island got disbanded or you got kicked from it whilst you were offline."));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "espawn " + player.getName());
                        return;
                    }
                    Island is = IslandCache.getIsland(user.getIslandUUID());

                    if(is == null) {
                        System.out.println(user.getPlayer() + " had a invalid island id");
                        return;
                    }

                    Island island = IslandCache.getIsland(user.getIslandUUID());
                    IslandCache.islandsWithPlayersOnline.add(island);

                    user.setIsland(island);
                }

                callback.run();

                return;
            }

            User newUser = new User(identifier);
            users.add(newUser);
            callback.run();
        });
    }

    public void removeFromCacheAndSaveToDatabase(UUID identifier) {

        // Get the user and remove it from cache
        User user = getUser(identifier);

        // Null check
        if (user == null) {
            return;
        }

        // Save it to the database
        userStorage.saveAsync(user);

        // Remove from cache
        users.remove(user);
    }
}
