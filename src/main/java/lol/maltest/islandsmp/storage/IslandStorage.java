package lol.maltest.islandsmp.storage;

import lol.maltest.islandsmp.storage.types.MongoStorage;
import lol.maltest.islandsmp.entities.Island;

import java.util.UUID;

public final class IslandStorage extends MongoStorage<UUID, Island> {

    public IslandStorage() {
        super(Island.class, "islands");
    }
}
