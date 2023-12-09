package lol.maltest.islandsmp.utils;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;

@Getter
public enum UpgradeType {
    ORE_DROPS(Material.DIAMOND),
    FARM_DROPS(Material.WHEAT_SEEDS),
    MOB_DROPS(Material.FEATHER),
    MOB_SPAWNS(Material.ROTTEN_FLESH),
    XP_DROPS(Material.EXPERIENCE_BOTTLE),
    KEEP_INVENTORY(Material.TOTEM_OF_UNDYING),
    KEEP_XP(Material.NETHER_STAR),
    SPEED(Material.SUGAR),
    HASTE(Material.GLOWSTONE_DUST),
    NIGHT_VISION(Material.REDSTONE_LAMP),
    NETHER_ACCESS(Material.OBSIDIAN),
    WARP_SLOTS(Material.ENDER_EYE),
    TRUSTED_SLOTS(Material.IRON_CHESTPLATE);

    private final Material material;

    UpgradeType(Material material) {
        this.material = material;
    }

    public static UpgradeType getByMaterial(Material material) {
        return Arrays.stream(values())
                .filter(upgradeType -> upgradeType.getMaterial().equals(material))
                .findFirst()
                .orElse(null);
    }
}
