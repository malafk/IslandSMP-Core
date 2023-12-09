package lol.maltest.islandsmp.entities.sub;

import lol.maltest.islandsmp.IslandSMP;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class IslandLocation {
    public int minX, maxX, minZ, maxZ;

    private int spawnX, spawnY, spawnZ;
    private int visitX = -1, visitY = -1, visitZ = -1;

    private String worldName;

    public IslandLocation(int minX, int maxX, int minZ, int maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = Math.abs(minZ);
        this.maxZ = Math.abs(maxZ);

        this.worldName = IslandSMP.getInstance().getWorldName();

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

    public Location getVisitorLocation() {
        return new Location(Bukkit.getWorld(this.worldName), visitX, visitY, visitZ);
    }

    public Location getMiddleLocation() {
        int middleX = (minX + maxX) / 2;


        int middleZ = (minZ + maxZ) / 2;

        int middleY = spawnY;

        return new Location(Bukkit.getWorld(this.worldName), middleX, middleY, middleZ);
    }

    public boolean hasVisitorLocation() {
        return visitX != -1 && visitY != -1 && visitZ != -1;
    }

    public void setSpawnLocation(Location location) {
        spawnX = location.getBlockX();
        spawnY = location.getBlockY();
        spawnZ = location.getBlockZ();
    }

    public void setVisitorLocation(Location location) {
        visitX = location.getBlockX();
        visitY = location.getBlockY();
        visitZ = location.getBlockZ();
    }

}
