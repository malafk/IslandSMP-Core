package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.MenuItem;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class IslandMainMenu extends Menu {

    @Override
    public void open(Player player) {

        setPreviousMenu(null);

        Gui gui = Gui.gui()
                .title(MenuUtil.menuMainTitle)
                .rows(MenuUtil.menuMainRows)
                .create();

        openGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key, ClickType clickType) {
        switch (key) {
            case "main.buttons.members":
                new IslandMembersMenu().open(player);
                break;
            case "main.buttons.warps":
                new IslandWarpsMenu().open(player);
                break;
            case "main.buttons.permissions":
                new IslandPermissionsMenu().open(player);
                break;
            case "main.buttons.home":
                User user = UserCache.getUser(player.getUniqueId());
                player.closeInventory();
                player.teleport(user.getIsland().getIslandLocation().getSpawnLocation());
                break;
            case "main.buttons.trusted":
                new IslandTrustedMenu().open(player);
                break;
            case "main.buttons.settings":
                new IslandSettingsMenu().open(player);
                break;
            case "main.buttons.upgrades":
                new IslandUpgradeMenu().open(player);
                break;
        }
    }

    @Override
    public int backButtonSlot() {
        return -1;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        return MenuUtil.menuMainButtons;
    }
}