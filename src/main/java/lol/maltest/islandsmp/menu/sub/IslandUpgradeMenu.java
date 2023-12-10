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
import lol.maltest.islandsmp.utils.LanguageUtil;
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
    private static final int REWARD_SLOT = 13;
    private List<GuiItem> POSSIBLE_REWARDS;

    private boolean firstSpin = true;


    Player player;

    private Gui gui;
    private User user;
    private int spinCount = 0;
    private boolean isSpinning = false;

    @Override
    public void open(Player player) {

        setPreviousMenu(IslandMainMenu.class);

        gui = Gui.gui()
                .title(MenuUtil.menuUpgradesTitle)
                .rows(MenuUtil.menuUpgradesRows)
                .create();

        populateGui(gui, player);
        gui.open(player);
    }

    private List<GuiItem> generatePossibleRewards() {
        List<GuiItem> rewards = new ArrayList<>();
        ArrayList<Menuable> upgradeButtons = new ArrayList<>(MenuUtil.menuUpgradesButtons);

        for (Menuable menuable : upgradeButtons) {
            Material material = menuable.getMaterial();
            if(menuable.getKeyPrefix().equalsIgnoreCase("upgrades.buttons.spin")) continue;

            UpgradeType upgradeType = UpgradeType.getByMaterial(material);

            int currentLevel = IslandSMP.getInstance().getUpgradeManager().getCurrentLevel(upgradeType, user.getIsland());
            Tier rewardTier = IslandSMP.getInstance().getUpgradeManager().getTier(upgradeType, currentLevel);

            if (rewardTier != null && rewardTier.getRequiredSpins() > 0) {
                ArrayList<Component> newMen = new ArrayList<>();

                String require = IslandSMP.getInstance().getUpgradeManager().getRequirements(upgradeType, (currentLevel + 1), user.getIsland(), player);

                for (String lore : MenuUtil._menuFile.getStringList(menuable.getKeyPrefix() + ".lore")) {
                    newMen.add(HexUtils.colour(
                            lore.replace("%tier%", currentLevel + "")
                                    .replace("%next_tier%", (currentLevel + 1) + "")
                                    .replace("%requirements%", (require == null ? "None" : require))
                    ));
                }

                GuiItem item = ItemBuilder.from(menuable.getMaterial()).name(HexUtils.colour(menuable.getName())).asGuiItem(e -> e.setCancelled(true));


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

            if (currentLevel == 0 && getAmountSpins == 0) {
                player.sendMessage(HexUtils.colour(LanguageUtil.messagesUpgradeDiscovered.replace("%upgrade%", upgradeType.name())));
                IslandSMP.getInstance().getUpgradeManager().applyUpgradeToTier(user.getIsland(), upgradeType, currentLevel);
            }

            if (currentLevel > 0 && getAmountSpins == 0) {
                user.getIsland().addAmountSpins(upgradeType);
                player.sendMessage(HexUtils.colour(LanguageUtil.messagesUpgradeFoundSpin.replace("%upgrade%", upgradeType.name())));
            }

            boolean hasReachRequirements = IslandSMP.getInstance().getUpgradeManager().canUpgradeToTier(player, user.getIsland(), upgradeType, (currentLevel + 1));

            if(hasReachRequirements) {
                player.sendMessage(HexUtils.colour(LanguageUtil.messagesUpgradeCanUpgrade.replace("%upgrade%", upgradeType.name())));
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
                GuiItem guiItem = ItemBuilder.from(currentItem).asGuiItem(e -> e.setCancelled(true));
                gui.setItem(i, guiItem);
            }
        }
    }

    private void addNewReward(Gui gui) {
        if(POSSIBLE_REWARDS.isEmpty()) return;
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
        if ("upgrades.buttons.spin".equals(key)) {
            if(POSSIBLE_REWARDS.isEmpty()) {
                player.sendMessage(HexUtils.colour(LanguageUtil.messagesUpgradeMaxedOut));
                return;
            }

            // take 15k or whatever
            double playerBalance = IslandSMP.getInstance().getEconomy().getBalance(player);

            if(playerBalance < 15000) {
                player.sendMessage(HexUtils.colour(LanguageUtil.messagesUpgradeNotEnoughPs));
                return;
            }

            IslandSMP.getInstance().getEconomy().withdrawPlayer(player, 15000);
            player.sendMessage(HexUtils.colour("&aSpin &c[$15,000]"));
            spinCrate(gui, player);
        }

        if (!key.startsWith("upgrades.buttons.")) return;

        User user = UserCache.getUser(player.getUniqueId());

        if (user == null) return;

        if(key.equalsIgnoreCase("upgrades.buttons.spin")) return;

        UpgradeType upgradeType = UpgradeType.valueOf(key.replace("upgrades.buttons.", "").toUpperCase());

        int tier = IslandSMP.getInstance().getUpgradeManager().getCurrentLevel(upgradeType, user.getIsland());


        boolean canUpgrade = IslandSMP.getInstance().getUpgradeManager().canUpgradeToTier(player, user.getIsland(), upgradeType, tier);

        Tier tierObject = IslandSMP.getInstance().getUpgradeManager().getTier(upgradeType, tier);

        System.out.println("Getting " + (tier + 1) + " level of " + upgradeType.name());

        if(!canUpgrade) return;

        int moneyToTake = tierObject.getRequiredMoney();
        int spinsToTake = tierObject.getRequiredSpins();

        user.getIsland().resetAmountSpins(upgradeType);
        IslandSMP.getInstance().getEconomy().withdrawPlayer(player, moneyToTake);

        IslandSMP.getInstance().getUpgradeManager().applyUpgradeToTier(user.getIsland(), upgradeType,  tier);


        player.sendMessage(HexUtils.colour(LanguageUtil.messagesUpgradeTierUp.replace("%level%", (tier + 1) + "")));


        populateGui(gui, player);
        gui.update();

        // take moeny then said u upgraded


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
        if (user == null) return null;
        this.user = user;

        POSSIBLE_REWARDS = generatePossibleRewards();


        List<Menuable> items = new ArrayList<>();

        // Add spinner item

//        items.add(new DynamicMenuItem(Material.LEVER, "Spin", new ArrayList<>(), "spin", 22, null));

        // Add possible rewards in the row
        if(firstSpin) {
            for (int i = 0; i < 7; i++) {
                items.add(new DynamicMenuItem(Material.YELLOW_STAINED_GLASS_PANE, "Reward", new ArrayList<>(), "reward", 10 + i, null));
            }
            firstSpin = false;
        }

        ArrayList<Menuable> upgradeButtons = new ArrayList<>(MenuUtil.menuUpgradesButtons);


        for (Menuable menuable : upgradeButtons) {
            if(menuable.getKeyPrefix().equalsIgnoreCase("upgrades.buttons.spin")) {
                items.add(
                        new DynamicMenuItem(
                                menuable.getMaterial(),
                                menuable.getName(),
                                menuable.getColorLore(),
                                menuable.getKeyPrefix(),
                                menuable.getSlot(),
                                null
                        )
                );
                continue;
            }


            UpgradeType upgradetype = UpgradeType.valueOf(menuable.getKeyPrefix().substring(menuable.getKeyPrefix().lastIndexOf(".") + 1).toUpperCase());
            ArrayList<Component> newMen = new ArrayList<>();

            int tier = IslandSMP.getInstance().getUpgradeManager().getCurrentLevel(upgradetype, user.getIsland());

            String require = IslandSMP.getInstance().getUpgradeManager().getRequirements(upgradetype, tier, user.getIsland(), player);
            boolean canUpgrade = IslandSMP.getInstance().getUpgradeManager().canUpgradeToTier(player, user.getIsland(), upgradetype, tier);

            boolean isMaxTier = tier == IslandSMP.getInstance().getUpgradeManager().getMaxTier(upgradetype);

            for (String lore : MenuUtil._menuFile.getStringList(menuable.getKeyPrefix() + ".lore")) {
                newMen.add(HexUtils.colour(
                        lore.replace("%tier%", String.valueOf(tier))
                                .replace("%next_tier%", isMaxTier ? "None" : String.valueOf(tier + 1))
                                .replace("%requirements%", isMaxTier ? "None (You're at the max level!)" : (require == null ? "None" : require))
                                .replace("%status%", (canUpgrade ? "&aClick to upgrade to the next tier!" : (isMaxTier ? "&cYou're at the maximum tier!" : "&cYou don't meet the requirements to upgrade!")))
                ));
            }

            if (tier == 0) {
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
