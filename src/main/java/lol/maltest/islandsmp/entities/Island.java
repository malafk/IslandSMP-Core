package lol.maltest.islandsmp.entities;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.entities.sub.IslandLocation;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import lol.maltest.islandsmp.entities.sub.IslandWarp;
import lol.maltest.islandsmp.entities.type.IslandStorageObject;
import lol.maltest.islandsmp.utils.Permission;
import lol.maltest.islandsmp.utils.Rank;
import lol.maltest.islandsmp.utils.Setting;
import lol.maltest.islandsmp.utils.UpgradeType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

@Setter
public final class Island extends IslandStorageObject<UUID> {

    // Object Values
    @Getter @Setter private String islandName;
    @Getter @Setter private UUID islandOwner;
    @Getter @Setter private IslandLocation islandLocation;

    private List<IslandMember> islandMembers = new ArrayList<>();
    @Getter private List<UUID> trustedMembers = new ArrayList<>();

    @Getter private List<IslandWarp> islandWarps = new ArrayList<>();

    @Getter @Setter private Map<Rank, Set<String>> rankPermissions = new HashMap<>();
    private List<Setting> settings = new ArrayList<>();

    // Island Upgrade Related Levels
    @Getter @Setter private transient boolean isWorldBorderGrowing = false;

    @Getter @Setter private boolean locked = false;

    // used to keep track of island upgrade lveels
    @Getter private Map<String, Integer> upgradeLevels = new HashMap<>();
    // when a spin lands here it
    @Getter private Map<String, Integer> upgradeSpins = new HashMap<>();

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

        for (UpgradeType type : UpgradeType.values()) {
            upgradeLevels.put(type.name().toUpperCase(), 0);
            upgradeSpins.put(type.name().toUpperCase(), 0);
        }
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

    public void addAmountSpins(UpgradeType upgradeType) {
        upgradeSpins.put(upgradeType.name().toUpperCase(), getAmountSpins(upgradeType) + 1);
    }

    public void resetAmountSpins(UpgradeType upgradeType) {
        upgradeSpins.put(upgradeType.name().toUpperCase(), 0);
    }

    public int getAmountSpins(UpgradeType upgradeType) {
        return upgradeSpins.get(upgradeType.name().toUpperCase());
    }

    public void levelUpUpgrade(UpgradeType upgrade) {
        Integer currentLevel = upgradeLevels.getOrDefault(upgrade.name().toUpperCase(), 0);
        upgradeLevels.put(upgrade.name().toUpperCase(), currentLevel + 1);

        if(upgrade == UpgradeType.WORLD_BORDER) {
            setWorldBorderGrowing(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    setWorldBorderGrowing(false);
                }
            }.runTaskLaterAsynchronously(IslandSMP.getInstance(), 20 * 10);
        }
    }

    public int getLevel(UpgradeType upgrade) {
        return upgradeLevels.getOrDefault(upgrade.name().toUpperCase(), 0);
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

    public boolean isTrustedMember(Player player) {
        return trustedMembers.contains(player.getUniqueId());
    }

    public boolean isIslandMember(Player player) {
        return getIslandMembers().stream()
                .anyMatch(member -> member.getPlayerUuid().equals(player.getUniqueId()));
    }

    public IslandMember getIslandMemberByUUID(UUID uuid) {
        return getIslandMembers().stream()
                .filter(member -> member.getPlayerUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean isTrustedOrIslandMember(Player player) {
        return isTrustedMember(player) || isIslandMember(player);
    }

    public int getMaxWarps() {
        int level = getLevel(UpgradeType.WARP_SLOTS);
        return switch (level) {
            case 1 -> 5;
            case 2 -> 7;
            case 3 -> 10;
            default -> 3;
        };
    }

    public int getMaxTrusted() {
        int level = getLevel(UpgradeType.TRUSTED_SLOTS);
        return switch (level) {
            case 1 -> 5;
            case 2 -> 7;
            case 3 -> 10;
            default -> 3;
        };
    }

    public int getWorldBorderSize() {
        int level = getLevel(UpgradeType.WORLD_BORDER);
        return switch (level) {
            case 1 -> 200;
            case 2 -> 250;
            case 3 -> 300;
            case 4 -> 350;
            case 5 -> 368;
            default -> 150;
        };
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

    public void addIslandMember(Player player) {
        islandMembers.add(new IslandMember(player.getUniqueId()));
    }

    public void removeIslandMember(Player player) {
        IslandMember isM = getIslandMemberByUUID(player.getUniqueId());
        if(isM == null) return;
        islandMembers.remove(isM);
    }


    public Rank getPlayerRank(Player player) {
        if(getIslandOwner().equals(player.getUniqueId())) return Rank.OWNER;

        for(IslandMember islandMember : islandMembers) {
            if(islandMember.getPlayerUuid().equals(player.getUniqueId())) {
                return islandMember.getRank();
            }
        }

        for(UUID trusted : trustedMembers) {
            if(player.getUniqueId().equals(trusted)) {
                return Rank.TRUSTED;
            }
        }

        return null;
    }

    // Remove a permission from a specific rank
    public void revokePermission(Rank rank, String permission) {
        Set<String> permissions = this.rankPermissions.get(rank);
        if (permissions != null) {
            permissions.remove(permission.toUpperCase());
        }
    }
    public void grantPermission(Rank rank, String permission) {
        this.rankPermissions.computeIfAbsent(rank, k -> new HashSet<>()).add(permission);
    }


    public boolean hasPermission(Rank rank, String permission) {
        Set<String> permissions = this.rankPermissions.get(rank);
        return permissions != null && permissions.contains(permission.toUpperCase());
    }

    public boolean isSettingActive(Setting setting) {
        return settings.contains(setting);
    }

    public boolean isSettingActive(String setting) {
        Setting settingEnumValue = Setting.valueOf(setting.toUpperCase());
        return settings.contains(settingEnumValue);
    }

    public void toggleSetting(String setting) {
        Setting settingEnumValue = Setting.valueOf(setting.toUpperCase());
        if(settings.contains(settingEnumValue)) {
            settings.remove(settingEnumValue);
        } else {
            settings.add(settingEnumValue);
        }
    }

    public void toggleSetting(Setting setting) {
        if(settings.contains(setting)) {
            settings.remove(setting);
        } else {
            settings.add(setting);
        }
    }

    public int getNumberOfActivePermissions(Rank rank) {
        Set<String> permissions = this.rankPermissions.get(rank);
        if (permissions != null) {
            return permissions.size();
        } else {
            // No permissions found for rank
            return 0;
        }
    }

    // Check if a rank has a permission
    public boolean hasPermission(Player player, String permission) {
        if(getIslandOwner().equals(player.getUniqueId())) return true;

        Set<String> permissions = this.rankPermissions.get(getPlayerRank(player));
        return permissions != null && permissions.contains(permission.toUpperCase());
    }
}
