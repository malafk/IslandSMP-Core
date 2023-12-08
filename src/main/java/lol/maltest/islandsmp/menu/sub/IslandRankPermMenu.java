package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.MenuItem;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.MenuUtil;
import lol.maltest.islandsmp.utils.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class IslandRankPermMenu extends Menu {

    private final Rank selectedRank;

    public IslandRankPermMenu(Rank selectedRank) {
        this.selectedRank = selectedRank;
    }

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
    }

    @Override
    public int backButtonSlot() {
        return 31;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        ArrayList<Menuable> menuRankPermissionsButtons = new ArrayList<>();

        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.place"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.break"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.container"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.invite"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.kick"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.promote"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.demote"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.settings"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.upgrade"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.permissions"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.sethome"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.home"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.setwarp"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.warp"));

        return MenuUtil.menuRankPermissionsButtons;
    }
}