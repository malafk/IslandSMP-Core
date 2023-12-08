package lol.maltest.islandsmp.storage.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.entities.type.PlayerStorageObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class JSONStorage<I, T extends PlayerStorageObject<I>> {

    // Identifiers
    private final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(java.lang.reflect.Modifier.STATIC, java.lang.reflect.Modifier.TRANSIENT)
            .serializeNulls()
            .create();

    private final File folder;
    private final Class<T> objectClass;

    // Constructor
    public JSONStorage(Class<T> objectClass, String folderName) {
        this.objectClass = objectClass;
        this.folder = new File(IslandSMP.getInstance().getDataFolder(), folderName);

        if (!this.folder.exists()) {
            this.folder.mkdir();
        }
    }

    // Save it async
    public CompletableFuture<Void> saveAsync(T object) {
        return CompletableFuture.runAsync(() -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(object);
            if (json != null) {
                try {
                    File objectFile = getObjectFile(object.getPlayer());

                    if (!objectFile.exists()) {
                        try {
                            objectFile.createNewFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    Files.writeString(objectFile.toPath(), json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void save(T object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(object);
        if (json != null) {
            try {
                File objectFile = getObjectFile(object.getPlayer());

                if (!objectFile.exists()) {
                    try {
                        objectFile.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                Files.writeString(objectFile.toPath(), json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete file baed on identifier method
    public CompletableFuture<Void> deleteAsync(I identifier) {
        return CompletableFuture.runAsync(() -> {
            File objectFile = getObjectFile(identifier);
            if (objectFile.exists()) {
                try {
                    Files.delete(objectFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Get the first object from the key
    public CompletableFuture<T> getObject(I identifier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File objectFile = getObjectFile(identifier);
                if (!objectFile.exists()) return null;

                return deserialize(Files.readString(objectFile.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    // Get all objects from the JSON storage
    public List<T> getAllObjects() {

        List<T> objects = new ArrayList<>();
        File[] objectFiles = getAllObjectFiles();

        if (objectFiles == null) {
            // Handle the case where objectFiles is null (optional)
            return objects; // Return an empty list in case of null objectFiles
        }

        for (File objectFile : objectFiles) {
            try {
                String json = Files.readString(objectFile.toPath());
                T object = deserialize(json);
                if (object != null) {
                    objects.add(object);
                }
            } catch (IOException e) {
                // Log the error for each file that couldn't be read
                e.printStackTrace();
            }
        }

        return objects;
    }


    // Helper method to get all object files in the directory
    private File[] getAllObjectFiles() {
        return folder.listFiles((dir, name) -> name.endsWith(".json"));
    }

    private T deserialize(String json) {
        return GSON.fromJson(json, objectClass);
    }

    private String serialize(T object) {
        return GSON.toJson(object, objectClass);
    }

    private File getObjectFile(I identifier) {
        return new File(folder, identifier + ".json");
    }
}