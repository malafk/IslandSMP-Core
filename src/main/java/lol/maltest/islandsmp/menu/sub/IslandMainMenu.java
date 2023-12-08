package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.MenuItem;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.MenuUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IslandMainMenu extends Menu {

    @Override
    public void open(Player player) {
        Gui gui = Gui.gui()
                .title(MenuUtil.menuMainTitle)
                .rows(MenuUtil.menuMainRows)
                .create();

        openGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key) {
        player.sendMessage("You just clicked " + key);
        switch (key) {
            case "main.buttons.members":
                player.sendMessage("Opening island member!");
                new IslandMembersMenu().open(player);
                break;
        }
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        return MenuUtil.menuMainButtons;
    }
}