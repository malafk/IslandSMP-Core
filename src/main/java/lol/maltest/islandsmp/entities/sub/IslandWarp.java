package lol.maltest.islandsmp.entities.sub;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Locale;
import java.util.UUID;

public class IslandWarp {

    private UUID warpCreator;
    @Getter private final String warpName;

    private final String worldName;
    private final double x,y,z;

    public IslandWarp(String warpName, UUID warpCreator, Location warpLocation) {
        this.warpName = warpName;
        this.warpCreator = warpCreator;
        this.worldName = warpLocation.getWorld().getName();

        this.x = roundHalf(warpLocation.getX());
        this.y = roundHalf(warpLocation.getY());
        this.z = roundHalf(warpLocation.getZ());
    }

    private double roundHalf(double number) {
        return Math.round(number * 2) / 2.0;
    }

    public Location getWarpLocation() {
        return new Location(
                Bukkit.getWorld(worldName),
                x,
                y,
                z
        );
    }

}
