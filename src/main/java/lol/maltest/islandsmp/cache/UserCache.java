package lol.maltest.islandsmp.cache;

import lol.maltest.islandsmp.storage.UserStorage;
import lol.maltest.islandsmp.entities.User;

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
                return;
            }

            User newUser = new User(identifier);
            users.add(newUser);
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
