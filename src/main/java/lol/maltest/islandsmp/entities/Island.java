package lol.maltest.islandsmp.entities;

import lol.maltest.islandsmp.entities.sub.IslandLocation;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import lol.maltest.islandsmp.entities.sub.IslandWarp;
import lol.maltest.islandsmp.entities.type.IslandStorageObject;
import lol.maltest.islandsmp.utils.IslandRank;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
public final class Island extends IslandStorageObject<UUID> {

    // Object Values
    @Getter @Setter private String islandName;
    @Getter private final UUID islandOwner;
    @Getter @Setter private IslandLocation islandLocation;

    private List<IslandMember> islandMembers = new ArrayList<>();
    @Getter private List<IslandWarp> islandWarps = new ArrayList<>();

    // Island Upgrade Related Levels
    @Getter private int worldBorderSize = 150;

    private int maxWarps = 3; // default

    private int oreDropUpgrade, farmDropUpgrade, mobDropUpgrade, mobSpawnUpgrade, xpUpgrade;
    private boolean keepInventoryUpgrade;

    public Island(String islandName, UUID islandUUID, UUID islandOwner) {
        super(islandUUID);
        this.islandName = islandName;
        this.islandOwner = islandOwner;
    }

    /**
     * Checks if the given location is inside this island.
     *
     * @param location The location to check.
     * @return True if the location is inside the island; otherwise, false.
     */
    public boolean isLocationInsideIsland(Location location) {
        // Debug: Print the coordinates being checked
        boolean xWithinBounds = (getIslandLocation().minX <= getIslandLocation().maxX) ?
                (location.getX() >= getIslandLocation().minX && location.getX() <= getIslandLocation().maxX) :
                (location.getX() >= getIslandLocation().maxX && location.getX() <= getIslandLocation().minX);

        if (!xWithinBounds) {
            return false;
        }

        // Check the z coordinate
        boolean zWithinBounds = (getIslandLocation().minZ <= getIslandLocation().maxZ) ?
                (location.getZ() >= getIslandLocation().minZ && location.getZ() <= getIslandLocation().maxZ) :
                (location.getZ() >= getIslandLocation().maxZ && location.getZ() <= getIslandLocation().minZ);

        if (!zWithinBounds) {
            return false;
        }

        // If we've passed both checks, the location is within the island.
        return true;
    }


    public List<IslandMember> getIslandMembers() {
        List<IslandMember> members = new ArrayList<>();
        IslandMember owner = new IslandMember(islandOwner);
        owner.setRank(IslandRank.OWNER);
        members.add(owner);

        if(islandMembers != null) {
            members.addAll(islandMembers);
        }

        return members;
    }

    public int getFreeWarps() {
        return maxWarps - islandWarps.size();
    }

    public IslandWarp getWarpByName(String warpName) {
        for(IslandWarp warp : islandWarps) {
            if(warp.getWarpName().equalsIgnoreCase(warpName)) {
                return warp;
            }
        }
        return null;
    }

    public void setNewWarp(String warpName, Player creator) {
        islandWarps.add(new IslandWarp(warpName, creator.getUniqueId(), creator.getLocation()));
    }
}
