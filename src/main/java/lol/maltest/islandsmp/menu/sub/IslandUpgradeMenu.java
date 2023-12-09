package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.manager.UpgradeManager;
import lol.maltest.islandsmp.menu.DynamicMenuItem;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.upgrade.Tier;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.MenuUtil;
import lol.maltest.islandsmp.utils.UpgradeType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class IslandUpgradeMenu extends Menu {

    private static final Random RANDOM = new Random();
    private static final int TOTAL_INCREMENTS = 20;
    private static final int TOTAL_DURATION_TICKS = 100; // 5 seconds in ticks
    private static final int REWARD_SLOT = 13;
    private List<GuiItem> POSSIBLE_REWARDS;


    private Gui gui;
    private User user;
    private Player player;
    private int spinCount = 0;
    private boolean isSpinning = false;

    @Override
    public void open(Player player) {
        gui = Gui.gui()
                .title(HexUtils.colour("Test Spinning"))
                .rows(6)
                .create();

        populateGui(gui, player);
        gui.open(player);
    }

    private List<GuiItem> generatePossibleRewards() {
        List<GuiItem> rewards = new ArrayList<>();
        ArrayList<Menuable> upgradeButtons = new ArrayList<>(MenuUtil.menuUpgradesButtons);

        for(Menuable menuable : upgradeButtons) {
            Material material = menuable.getMaterial();
            UpgradeType upgradeType = UpgradeType.getByMaterial(material);

            int currentLevel = IslandSMP.getInstance().getUpgradeManager().getCurrentLevel(upgradeType, user.getIsland());
            Tier rewardTier = IslandSMP.getInstance().getUpgradeManager().getTier(upgradeType, currentLevel);

            if(rewardTier != null && rewardTier.getRequiredSpins() > 0) {
                ArrayList<Component> newMen = new ArrayList<>();

                String require = IslandSMP.getInstance().getUpgradeManager().getRequirements(upgradeType, (currentLevel + 1));

                for(String lore : MenuUtil._menuFile.getStringList(menuable.getKeyPrefix() + ".lore")) {
                    newMen.add(HexUtils.colour(
                            lore.replace("%tier%", currentLevel + "")
                                    .replace("%next_tier%", (currentLevel + 1) + "")
                                    .replace("%requirements%", (require == null ? "None" : require))
                    ));
                }

                GuiItem item = ItemBuilder.from(menuable.getMaterial()).name(HexUtils.colour(menuable.getName())).asGuiItem();


                rewards.add(
                        item
                );
            }
        }

        return rewards;
    }

    public void spinCrate(Gui gui, Player player) {
        if (isSpinning) return;

        isSpinning = true;
        POSSIBLE_REWARDS = generatePossibleRewards();
        scheduleNextSpin(gui, player);
    }

    private void scheduleNextSpin(Gui gui, Player player) {
        if (spinCount >= TOTAL_INCREMENTS) {
            isSpinning = false;
            Material rewardMaterial = gui.getInventory().getItem(REWARD_SLOT).getType();
            UpgradeType upgradeType = UpgradeType.getByMaterial(rewardMaterial);

            int currentLevel = IslandSMP.getInstance().getUpgradeManager().getCurrentLevel(upgradeType, user.getIsland());

            int getAmountSpins = user.getIsland().getAmountSpins(upgradeType);

            if(currentLevel == 0 && getAmountSpins == 0) {
                player.sendMessage("You have revealed and got tier 1 of " + upgradeType.name());

                if (IslandSMP.getInstance().getUpgradeManager().canUpgradeToTier(player, user.getIsland(), upgradeType, currentLevel)) {
                    IslandSMP.getInstance().getUpgradeManager().applyUpgradeToTier(user.getIsland(), upgradeType, currentLevel);
                    player.sendMessage(upgradeType.name() + " upgraded to level " + (currentLevel + 1));
                }
            }

            if(currentLevel > 0 && getAmountSpins == 0) {
                user.getIsland().addAmountSpins(upgradeType);
                player.sendMessage(upgradeType.name() + " you found a spin again.. adding it");
            }

            populateGui(gui, player);
            gui.update();
            spinCount = 0;

            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                shiftItems(gui);
                addNewReward(gui);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                gui.update();
                scheduleNextSpin(gui, player);
            }
        }.runTaskLater(IslandSMP.getInstance(), calculateDelay(spinCount));
        spinCount++;
    }

    private void shiftItems(Gui gui) {
        for (int i = 10; i < 16; i++) {
            ItemStack currentItem = gui.getInventory().getItem(i + 1);
            if (currentItem != null) {
                GuiItem guiItem = ItemBuilder.from(currentItem).asGuiItem();
                gui.setItem(i, guiItem);
            }
        }
    }

    private void addNewReward(Gui gui) {
        GuiItem nextReward = POSSIBLE_REWARDS.get(RANDOM.nextInt(POSSIBLE_REWARDS.size()));
        gui.setItem(16, nextReward);
    }

    private long calculateDelay(int count) {
        double fraction = (double) count / TOTAL_INCREMENTS;
        double delay = 1 + 39 * Math.pow(fraction, 4);
        return Math.min(40, Math.round(delay));
    }

    @Override
    public void onClick(Player player, String key, ClickType clickType) {
        if ("spin".equals(key)) {
            spinCrate(gui, player);
        }
        // Other click handling
    }

    @Override
    public int backButtonSlot() {
        return 49; // Placeholder
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        this.player = player;
        User user = UserCache.getUser(player.getUniqueId());
        if(user == null) return null;
        this.user = user;

        POSSIBLE_REWARDS = generatePossibleRewards();


        List<Menuable> items = new ArrayList<>();

        // Add spinner item
        items.add(new DynamicMenuItem(Material.LEVER, "Spin", new ArrayList<>(), "spin", 22, null));

        // Add possible rewards in the row
//        for (int i = 0; i < 7; i++) {
//            GuiItem rewardMaterial = POSSIBLE_REWARDS.get(RANDOM.nextInt(POSSIBLE_REWARDS.size()));
//
//            Material material = rewardMaterial.getItemStack().getType();
//            UpgradeType upgradeType = UpgradeType.getByMaterial(material);
//
//            int currentLevel = IslandSMP.getInstance().getUpgradeManager().getCurrentLevel(upgradeType, user.getIsland());
//
//            Tier rewardTier = IslandSMP.getInstance().getUpgradeManager().getTier(upgradeType, currentLevel);
//
//            if(rewardTier == null || rewardTier.getRequiredSpins() > 0) {
//                items.add(new DynamicMenuItem(rewardMaterial.getItemStack().getType(), "Reward", new ArrayList<>(), "reward", 10 + i, null));
//            }
//        }

        ArrayList<Menuable> upgradeButtons = new ArrayList<>(MenuUtil.menuUpgradesButtons);



        for(Menuable menuable : upgradeButtons) {

            UpgradeType upgradetype = UpgradeType.valueOf(menuable.getKeyPrefix().substring(menuable.getKeyPrefix().lastIndexOf(".") + 1).toUpperCase());
            ArrayList<Component> newMen = new ArrayList<>();

            int tier = IslandSMP.getInstance().getUpgradeManager().getCurrentLevel(upgradetype, user.getIsland());


            String require = IslandSMP.getInstance().getUpgradeManager().getRequirements(upgradetype, (tier + 1));
            boolean canUpgrade = IslandSMP.getInstance().getUpgradeManager().canUpgradeToTier(player, user.getIsland(), upgradetype, tier);

            for(String lore : MenuUtil._menuFile.getStringList(menuable.getKeyPrefix() + ".lore")) {
                newMen.add(HexUtils.colour(
                        lore.replace("%tier%", tier + "")
                            .replace("%next_tier%", (tier + 1) + "")
                            .replace("%requirements%", (require == null ? "None" : require))
                                .replace("%status%", (canUpgrade ? "&aClick to upgrade to the next tier!" : "&cYou don't meet the requirements to upgrade this!"))
                ));
            }

            if(tier == 0) {
                DynamicMenuItem filler = new DynamicMenuItem(Material.BLACK_WOOL, "?", new ArrayList<>(), "", menuable.getSlot(), null);
                items.add(filler);
                continue;
            }

            items.add(
                    new DynamicMenuItem(
                            menuable.getMaterial(),
                            menuable.getName(),
                            newMen,
                            menuable.getKeyPrefix(),
                            menuable.getSlot(),
                            null
                    )
            );
        }


        return items;
    }
}
