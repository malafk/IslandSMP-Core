package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class IslandPermissionsMenu extends Menu {

    @Override
    public void open(Player player) {

        setPreviousMenu(IslandMainMenu.class);

        Gui gui = Gui.gui()
                .title(MenuUtil.menuPermissionsTitle)
                .rows(MenuUtil.menuPermissionsRows)
                .create();

        openGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key, ClickType clickType) {
        player.sendMessage("Opening");

        new IslandRankPermMenu().open(player);
    }

    @Override
    public int backButtonSlot() {
        return 22;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        return MenuUtil.menuPermissionsButtons;
    }
}