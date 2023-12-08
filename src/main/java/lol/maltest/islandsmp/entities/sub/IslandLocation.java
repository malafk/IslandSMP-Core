package lol.maltest.islandsmp.entities.sub;

import lol.maltest.islandsmp.IslandSMP;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class IslandLocation {
    public int minX, maxX, minZ, maxZ;
    private int spawnX, spawnY, spawnZ;
    private String worldName;

    public IslandLocation(int minX, int maxX, int minZ, int maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = Math.abs(minZ);
        this.maxZ = Math.abs(maxZ);

        this.worldName = IslandSMP.getInstance().getWorldName();

        // Assuming defaultLocation is at minX, -14, minZ
        // For spawnLocation, adding the offsets to the default location
        this.spawnX = minX - 182; // Adjusted from -181.5 for integer
        this.spawnY = 81;         // 95 - 14
        this.spawnZ = minZ + 135; // Adjusted from 134.5 for integer
    }

    public Location getDefaultLocation() {
        return new Location(Bukkit.getWorld(this.worldName), minX, -14, minZ);
    }

    public Location getSpawnLocation() {
        return new Location(Bukkit.getWorld(this.worldName), spawnX, spawnY, spawnZ);
    }

    public Location getMiddleLocation() {
        int middleX = (minX + maxX) / 2;


        int middleZ = (minZ + maxZ) / 2;

        int middleY = spawnY;

        return new Location(Bukkit.getWorld(this.worldName), middleX, middleY, middleZ);
    }


}
