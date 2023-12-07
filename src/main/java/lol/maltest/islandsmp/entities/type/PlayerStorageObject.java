package lol.maltest.islandsmp.entities.type;

public class PlayerStorageObject<T> {

    private final T identifier;

    public PlayerStorageObject(T identifier) {
        this.identifier = identifier;
    }


    public T getPlayer() {
        return identifier;
    }
}
