package lol.maltest.islandsmp.entities.type;

public class IslandStorageObject<T> {

    private final T identifier;

    public IslandStorageObject(T identifier) {
        this.identifier = identifier;
    }


    public T getIslandUUID() {
        return identifier;
    }
}
