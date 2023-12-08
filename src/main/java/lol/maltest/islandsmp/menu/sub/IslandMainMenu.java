package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.MenuItem;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.MenuUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IslandMainMenu implements Menu {
    @Override
    public void open(Player player) {
        Gui gui = Gui.gui()
                .title(HexUtils.colour(MenuUtil.menuMainTitle))
                .rows(MenuUtil.menuMainRows)
                .create();

        setItems(gui, player);

        gui.open(player);
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return MenuUtil.menuMainButtons;
    }
}
