package lol.maltest.islandsmp.entities;

import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.sub.IslandLocation;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import lol.maltest.islandsmp.entities.sub.IslandWarp;
import lol.maltest.islandsmp.entities.type.IslandStorageObject;
import lol.maltest.islandsmp.utils.Permission;
import lol.maltest.islandsmp.utils.Rank;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Setter
public final class Island extends IslandStorageObject<UUID> {

    // Object Values
    @Getter @Setter private String islandName;
    @Getter private final UUID islandOwner;
    @Getter @Setter private IslandLocation islandLocation;

    private List<IslandMember> islandMembers = new ArrayList<>();
    @Getter private List<UUID> trustedMembers = new ArrayList<>();

    @Getter private List<IslandWarp> islandWarps = new ArrayList<>();

    @Getter private final Map<Rank, Set<String>> rankPermissions = new HashMap<>();

    // Island Upgrade Related Levels
    @Getter private int worldBorderSize = 150;

    private int maxWarps = 3; // default

    private int oreDropUpgrade, farmDropUpgrade, mobDropUpgrade, mobSpawnUpgrade, xpUpgrade;
    private boolean keepInventoryUpgrade;

    public Island(String islandName, UUID islandUUID, UUID islandOwner) {
        super(islandUUID);
        this.islandName = islandName;
        this.islandOwner = islandOwner;

        this.rankPermissions.put(Rank.TRUSTED, new HashSet<>(Arrays.asList(Permission.PLACE.name(), Permission.BREAK.name())));
        this.rankPermissions.put(Rank.MEMBER, new HashSet<>(Arrays.asList(Permission.PLACE.name(), Permission.BREAK.name(), Permission.HOME.name())));
        this.rankPermissions.put(Rank.MODERATOR, new HashSet<>(Arrays.asList(Permission.PLACE.name(), Permission.BREAK.name(), Permission.CONTAINER.name(), Permission.INVITE.name(), Permission.HOME.name(), Permission.WARP.name())));
        this.rankPermissions.put(Rank.ADMINISTRATOR, new HashSet<>(Arrays.asList(Permission.PLACE.name(), Permission.BREAK.name(), Permission.CONTAINER.name(), Permission.INVITE.name(), Permission.KICK.name(), Permission.PROMOTE.name(), Permission.DEMOTE.name(), Permission.UPGRADE.name(), Permission.SETHOME.name(), Permission.HOME.name(), Permission.SETWARP.name(), Permission.WARP.name())));

        this.rankPermissions.put(Rank.OWNER, EnumSet.allOf(Permission.class)
                .stream()
                .map(Permission::name)
                .collect(Collectors.toSet()));
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
        owner.setRank(Rank.OWNER);
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

    public void addTrustedMember(Player player) {
        trustedMembers.add(player.getUniqueId());
    }

    public void grantPermission(Rank rank, String permission) {
        this.rankPermissions.computeIfAbsent(rank, k -> new HashSet<>()).add(permission);
    }

    public Rank getPlayerRank(Player player) {

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return null;

        if(user.getIsland().getIslandOwner().equals(player.getUniqueId())) return Rank.OWNER;

        for(IslandMember islandMember : islandMembers) {
            if(islandMember.getPlayerUuid().equals(player.getUniqueId())) {
                return islandMember.getRank();
            }
        }
        return null;
    }

    // Remove a permission from a specific rank
    public void revokePermission(Rank rank, String permission) {
        Set<String> permissions = this.rankPermissions.get(rank);
        if (permissions != null) {
            permissions.remove(permission);
        }
    }

    public boolean hasPermission(Rank rank, String permission) {
        Set<String> permissions = this.rankPermissions.get(rank);
        return permissions != null && permissions.contains(permission);
    }

    // Check if a rank has a permission
    public boolean hasPermission(Player player, String permission) {
        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return false;

        if(user.getIsland().getIslandOwner().equals(player.getUniqueId())) return true;

        Set<String> permissions = this.rankPermissions.get(getPlayerRank(player));
        return permissions != null && permissions.contains(permission);
    }
}
