package lol.maltest.islandsmp.storage;

import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.storage.types.JSONStorage;

import java.util.UUID;


public final class UserStorage extends JSONStorage<UUID, User> {

    public UserStorage() {
        super(User.class, "users");
    }
}
