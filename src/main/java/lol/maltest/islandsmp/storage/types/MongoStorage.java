package lol.maltest.islandsmp.storage.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lol.maltest.islandsmp.IslandSMP;
import lombok.Getter;
import lol.maltest.islandsmp.entities.type.IslandStorageObject;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public abstract class MongoStorage<I, T extends IslandStorageObject<I>> {

    // Identifiers
    private static final String ID_FIELD = "identifier";

    private final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
            .serializeNulls()
            .create();

    protected final MongoCollection<Document> collection;

    private final Class<T> objectClass;
    private final String collectionName;

    // Constructor
    public MongoStorage(Class<T> objectClass, String collectionName) {

        this.objectClass = objectClass;
        this.collectionName = collectionName;

        MongoClient mongoClient = MongoClients.create(Objects.requireNonNull(IslandSMP.getInstance().getConfig().getString("mongo-connection")));
        MongoDatabase database = mongoClient.getDatabase(Objects.requireNonNull(IslandSMP.getInstance().getConfig().getString("mongo-database-name")));

        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);

        this.collection = database.getCollection(collectionName);
    }

    // Save it async
    public CompletableFuture<Void> saveAsync(T object) {
        return CompletableFuture.runAsync(() -> {
            Document document = serialize(object);
            if (document != null) {
                collection.replaceOne(
                        Filters.eq(ID_FIELD, String.valueOf(object.getIslandUUID())),
                        document,
                        new ReplaceOptions().upsert(true));
            }
        });
    }

    public void save(T object) {
        Document document = serialize(object);
        if (document != null) {
            collection.replaceOne(
                    Filters.eq(ID_FIELD, String.valueOf(object.getIslandUUID())),
                    document,
                    new ReplaceOptions().upsert(true));
        }
    }

    // Get the first object from the key, use this if it's like a profile system and a player will only ever have one profile
    public CompletableFuture<T> getObject(I identifier) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = collection.find(Filters.eq(ID_FIELD, String.valueOf(identifier))).first();
            if (document != null) {
                return deserialize(document);
            }
            return null;
        });
    }

    // Return a list of all objects associated with the same key
    public CompletableFuture<List<T>> getAllObjects(I identifier) {
        return CompletableFuture.supplyAsync(() -> {
            List<T> objects = new ArrayList<>();

            Bson filter = Filters.eq(ID_FIELD, identifier);

            try (MongoCursor<Document> iterator = collection.find(filter).iterator()) {
                while (iterator.hasNext()) {
                    Document document = iterator.next();
                    T object = deserialize(document);
                    objects.add(object);
                }
            }

            return objects;
        });
    }


    public void deleteObject(I identifier) {
        CompletableFuture.runAsync(() -> {
            collection.deleteOne(Filters.eq(ID_FIELD, String.valueOf(identifier)));
        });
    }

    // Return a list of all objects
    public List<T> loadAllObjects() {
        List<T> objects = new ArrayList<>();

        try (MongoCursor<Document> iterator = collection.find().iterator()) {
            while (iterator.hasNext()) {
                Document document = iterator.next();
                T object = deserialize(document);
                objects.add(object);
            }
        }

        return objects;
    }

    private T deserialize(Document document) {
        return GSON.fromJson(document.toJson(), objectClass);
    }

    private Document serialize(T object) {
        return Document.parse(GSON.toJson(object, objectClass));
    }


}
