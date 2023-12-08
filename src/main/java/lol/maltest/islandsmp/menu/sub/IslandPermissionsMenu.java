package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.menu.DynamicMenuItem;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.MenuUtil;
import lol.maltest.islandsmp.utils.Rank;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class IslandPermissionsMenu extends Menu {

    private Rank lastSelectedRank;

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
        this.lastSelectedRank = Rank.valueOf(key.split("\\.")[2].toUpperCase());
        new IslandRankPermMenu(lastSelectedRank).open(player);
    }

    @Override
    public int backButtonSlot() {
        return 22;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        ArrayList<Menuable> buttons = new ArrayList<>(MenuUtil.menuPermissionsButtons);

        ArrayList<Menuable> buttonToReturn = new ArrayList<>();

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return null;

        for(Menuable menuable : buttons) {

            ArrayList<Component> newMen = new ArrayList<>();

            String rankName = menuable.getKeyPrefix().substring(menuable.getKeyPrefix().lastIndexOf(".") + 1);


            for(String lore : MenuUtil._menuFile.getStringList(menuable.getKeyPrefix() + ".lore")) {
                newMen.add(HexUtils.colour(lore.replace("%permissions%", user.getIsland().getNumberOfActivePermissions(Rank.valueOf(rankName.toUpperCase())) + "")));
            }

            buttonToReturn.add(
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


        return buttonToReturn;
    }
}