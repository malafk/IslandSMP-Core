package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.MenuUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class IslandRankPermMenu extends Menu {

    @Override
    public void open(Player player) {

        setPreviousMenu(IslandPermissionsMenu.class);

        Gui gui = Gui.gui()
                .title(MenuUtil.menuRankPermissionsTitle)
                .rows(MenuUtil.menuRankPermissionsRows)
                .create();

        openGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key, ClickType clickType) {
        player.sendMessage("You just clicked " + key);
        switch (key) {
            case "main.buttons.members":
                new IslandMembersMenu().open(player);
                break;
            case "main.buttons.warps":
                new IslandWarpsMenu().open(player);
                break;
        }
    }

    @Override
    public int backButtonSlot() {
        return 31;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        return MenuUtil.menuRankPermissionsButtons;
    }
}