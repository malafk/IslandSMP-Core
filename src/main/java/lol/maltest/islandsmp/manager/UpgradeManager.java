package lol.maltest.islandsmp.manager;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import lol.maltest.islandsmp.upgrade.Tier;
import lol.maltest.islandsmp.utils.UpgradeType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class UpgradeManager {

    private IslandSMP plugin;

    private final Map<UpgradeType, List<Tier>> upgradeTiers = new HashMap<>();

    public UpgradeManager(IslandSMP plugin) {
        this.plugin = plugin;
        setupDefaults();

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Island island : IslandCache.islandsWithPlayersOnline) { // Custom array so its faster
                    boolean hasNightVision = island.getLevel(UpgradeType.NIGHT_VISION) != 0;
                    int hasteLevel = island.getLevel(UpgradeType.HASTE);
                    int speedLevel = island.getLevel(UpgradeType.SPEED);

                    for(IslandMember islandPlayer : island.getIslandMembers()) {
                        Player player = Bukkit.getPlayer(islandPlayer.getPlayerUuid());
                        if(player == null) continue;
                        if(hasNightVision) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60, 0, true, false));
                        }
                        if(hasteLevel != 0) {
                            int amplifier = hasteLevel -1;
                            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 60, hasteLevel, true, false));
                        }
                        if(speedLevel != 0) {
                            int amplifier = speedLevel -1;
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, speedLevel, true, false));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 3 * 20);
    }

    private void addTierToUpgradeTiers(Tier tier) {
        UpgradeType upgradeType = tier.getUpgradeType();

        if (!upgradeTiers.containsKey(upgradeType)) {
            upgradeTiers.put(upgradeType, new ArrayList<>());
        }

        upgradeTiers.get(upgradeType).add(tier);
    }

    public int getCurrentLevel(UpgradeType upgradeType, Island island) {
        return island.getUpgradeLevels().get(upgradeType.name().toUpperCase());
    }

    public Tier getTier(UpgradeType upgradeType, int tierIndex) {
        List<Tier> tiers = upgradeTiers.get(upgradeType);
        if (tiers != null && tierIndex >= 0 && tierIndex < tiers.size()) {
            return tiers.get(tierIndex);
        }
        return null; //or throw exception depending upon your requirements
    }

    public boolean canUpgradeToTier(Player requestr, Island island, UpgradeType upgradeType, int tierIndex) {
        if (tierIndex < 0 || tierIndex > upgradeTiers.size()) {
            return false;
        }
        List<Tier> tiers = upgradeTiers.get(upgradeType);
        if (tiers != null && tierIndex < tiers.size()) {
            return tiers.get(tierIndex).canUpgrade(requestr, island);
        }
        return false;
    }

    public void applyUpgradeToTier(Island island, UpgradeType upgradeType, int tierIndex) {
        if (tierIndex < 0 || tierIndex > upgradeTiers.size()) {
            return;
        }
        List<Tier> tiers = upgradeTiers.get(upgradeType);
        if (tiers != null && tierIndex < tiers.size()) {
            island.levelUpUpgrade(upgradeType);
        }
    }

    public int getMaxTier(UpgradeType upgradeType) {
        List<Tier> tiers = upgradeTiers.get(upgradeType);
        if (tiers != null) {
            return tiers.size();
        }
        return 0;
    }

    public String getRequirements(UpgradeType upgradeType, int tierIndex, Island island, Player player) {
        if (tierIndex < 0 || tierIndex > upgradeTiers.size()) {
            return null; // or return a suitable default value or perhaps throw an exception depending upon your requirements
        }

        List<Tier> tiers = upgradeTiers.get(upgradeType);
        if (tiers != null && tierIndex < tiers.size()) {
            Tier tier = tiers.get(tierIndex);
            int cost = tier.getRequiredMoney();
            int spins = tier.getRequiredSpins();

            // Assume that Island has methods getMoney() and getSpins(UpgradeType upgradeType)
            int existingMoney = (int) plugin.getEconomy().getBalance(player);
            int existingSpins = island.getAmountSpins(upgradeType);

            String spinColor = existingSpins >= spins ? "&a" : "&c";
            String moneyColor = existingMoney >= cost ? "&a" : "&c";

            String spinCost = spins > 0 ? spinColor + spins + " Spin" + (spins > 1 ? "(s)" : "") : "";
            String costStr = cost > 0 ? moneyColor + cost + "$" : "";

            return (spinCost.isEmpty() ? "" : spinCost + ", ") + costStr;
        }
        return null; // or return a suitable default value or perhaps throw an exception depending upon your requirements
    }
    public void setupDefaults() {
        // Increased Ore Drops
        Tier oreTier1 = new Tier(UpgradeType.ORE_DROPS, 1, 0); // First Tier (Fortune 1)
        Tier oreTier2 = new Tier(UpgradeType.ORE_DROPS, 1, 50000); // Second tier (Fortune 2)
        Tier oreTier3 = new Tier(UpgradeType.ORE_DROPS, 1, 250000); // Third Tier (Fortune 3)
        addTierToUpgradeTiers(oreTier1);
        addTierToUpgradeTiers(oreTier2);
        addTierToUpgradeTiers(oreTier3);

        // Increased Farm Drops
        Tier farmTier1 = new Tier(UpgradeType.FARM_DROPS, 1, 0); // First Tier (25% chance)
        Tier farmTier2 = new Tier(UpgradeType.FARM_DROPS, 1, 50000); // Second Tier (50% chance)
        Tier farmTier3 = new Tier(UpgradeType.FARM_DROPS, 1, 250000); // Third Tier (75% chance)
        Tier farmTier4 = new Tier(UpgradeType.FARM_DROPS, 1, 0); // Fourth Tier (100% chance)
        addTierToUpgradeTiers(farmTier1);
        addTierToUpgradeTiers(farmTier2);
        addTierToUpgradeTiers(farmTier3);
        addTierToUpgradeTiers(farmTier4);

        // Increased Mob Drops
        Tier mobTier1 = new Tier(UpgradeType.MOB_DROPS, 1, 0); // First Tier (Looting 1)
        Tier mobTier2 = new Tier(UpgradeType.MOB_DROPS, 1, 50000); // Second Tier (Looting 2)
        Tier mobTier3 = new Tier(UpgradeType.MOB_DROPS, 1, 250000); // Third Tier (Looting 3)
        addTierToUpgradeTiers(mobTier1);
        addTierToUpgradeTiers(mobTier2);
        addTierToUpgradeTiers(mobTier3);

        // Increased Mob Spawns
        Tier mobSpawnTier1 = new Tier(UpgradeType.MOB_SPAWNS, 1, 0); // First Tier (10% spawn rate)
        Tier mobSpawnTier2 = new Tier(UpgradeType.MOB_SPAWNS, 1, 50000); // Second Tier (50% spawn rate)
        Tier mobSpawnTier3 = new Tier(UpgradeType.MOB_SPAWNS, 1, 300000); // Second Tier (100% spawn rate)
        addTierToUpgradeTiers(mobSpawnTier1);
        addTierToUpgradeTiers(mobSpawnTier2);
        addTierToUpgradeTiers(mobSpawnTier3);

        // Increased XP Drops
        Tier xpTier1 = new Tier(UpgradeType.XP_DROPS, 1, 0); // First Tier (1.2x xp rate)
        Tier xpTier2 = new Tier(UpgradeType.XP_DROPS, 1, 50000); // Second Tier (1.4x xp rate)
        Tier xpTier3 = new Tier(UpgradeType.XP_DROPS, 1, 250000); // Third Tier (1.6x xp rate)
        Tier xpTier4 = new Tier(UpgradeType.XP_DROPS, 1, 0); // Fourth Tier (2x xp rate)
        addTierToUpgradeTiers(xpTier1);
        addTierToUpgradeTiers(xpTier2);
        addTierToUpgradeTiers(xpTier3);
        addTierToUpgradeTiers(xpTier4);

        // Keep Inventory
        Tier keepInventoryTier1 = new Tier(UpgradeType.KEEP_INVENTORY, 1, 0); // First Tier (Keep Inventory)
        addTierToUpgradeTiers(keepInventoryTier1);

        // Keep XP
        Tier keepXPTier1 = new Tier(UpgradeType.KEEP_XP, 1, 0); // First Tier (Keep XP)
        addTierToUpgradeTiers(keepXPTier1);
        // Permanent Speed
        Tier speedTier1 = new Tier(UpgradeType.SPEED, 1, 0); // First Tier (Speed 1)
        Tier speedTier2 = new Tier(UpgradeType.SPEED, 1, 50000); // Second Tier (Speed 2)
        Tier speedTier3 = new Tier(UpgradeType.SPEED, 1, 250000); // Third Tier (Speed 3)
        addTierToUpgradeTiers(speedTier1);
        addTierToUpgradeTiers(speedTier2);
        addTierToUpgradeTiers(speedTier3);

        // Permanent Haste
        Tier hasteTier1 = new Tier(UpgradeType.HASTE, 1, 0); // First Tier (Haste 1)
        Tier hasteTier2 = new Tier(UpgradeType.HASTE, 1, 50000); // Second Tier (Haste 2)
        Tier hasteTier3 = new Tier(UpgradeType.HASTE, 1, 250000); // Third Tier (Haste 3)
        addTierToUpgradeTiers(hasteTier1);
        addTierToUpgradeTiers(hasteTier2);
        addTierToUpgradeTiers(hasteTier3);

        // Permanent Night Vision
        Tier nightVisionTier1 = new Tier(UpgradeType.NIGHT_VISION, 1, 0); // First Tier (Night Vision 1)
        addTierToUpgradeTiers(nightVisionTier1);

        // Nether Access
        Tier netherAccessTier1 = new Tier(UpgradeType.NETHER_ACCESS, 1, 0); // First Tier (Nether Access)
        addTierToUpgradeTiers(netherAccessTier1);

        // Increased Warp Slots
        Tier warpSlotsTier1 = new Tier(UpgradeType.WARP_SLOTS, 1, 0); // First Tier (5 Warp Slots)
        Tier warpSlotsTier2 = new Tier(UpgradeType.WARP_SLOTS, 1, 50000); // Second Tier (7 Warp Slots)
        Tier warpSlotsTier3 = new Tier(UpgradeType.WARP_SLOTS, 1, 250000); // Third Tier (10 Warp Slots)

        addTierToUpgradeTiers(warpSlotsTier1);
        addTierToUpgradeTiers(warpSlotsTier2);
        addTierToUpgradeTiers(warpSlotsTier3);

        // Increased Trusted Slots
        Tier trustedSlotsTier1 = new Tier(UpgradeType.TRUSTED_SLOTS, 1, 0); // First Tier (5 Trusted Slots)
        Tier trustedSlotsTier2 = new Tier(UpgradeType.TRUSTED_SLOTS, 1, 50000); // Second Tier (7 Trusted Slots)
        Tier trustedSlotsTier3 = new Tier(UpgradeType.TRUSTED_SLOTS, 1, 250000); // Third Tier (10 Trusted Slots)
        addTierToUpgradeTiers(trustedSlotsTier1);
        addTierToUpgradeTiers(trustedSlotsTier2);
        addTierToUpgradeTiers(trustedSlotsTier3);

        Tier worldBorderTier1 = new Tier(UpgradeType.WORLD_BORDER, 1, 0);
        Tier worldBorderTier2 = new Tier(UpgradeType.WORLD_BORDER, 1, 50000);
        Tier worldBorderTier3 = new Tier(UpgradeType.WORLD_BORDER, 1, 200000);
        Tier worldBorderTier4 = new Tier(UpgradeType.WORLD_BORDER, 1, 350000);
        Tier worldBorderTier5 = new Tier(UpgradeType.WORLD_BORDER, 1, 0);
        addTierToUpgradeTiers(worldBorderTier1);
        addTierToUpgradeTiers(worldBorderTier2);
        addTierToUpgradeTiers(worldBorderTier3);
        addTierToUpgradeTiers(worldBorderTier4);
        addTierToUpgradeTiers(worldBorderTier5);
    }
}
