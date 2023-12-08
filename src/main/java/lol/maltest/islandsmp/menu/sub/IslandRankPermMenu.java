package lol.maltest.islandsmp.menu.sub;

import com.sun.jdi.ArrayReference;
import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.menu.DynamicMenuItem;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.MenuItem;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.*;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class IslandRankPermMenu extends Menu {

    private final Rank selectedRank;

    public IslandRankPermMenu(Rank selectedRank) {
        this.selectedRank = selectedRank;
    }

    Gui gui;

    @Override
    public void open(Player player) {

        setPreviousMenu(IslandPermissionsMenu.class);

        gui = Gui.gui()
                .title(MenuUtil.menuRankPermissionsTitle)
                .rows(MenuUtil.menuRankPermissionsRows)
                .create();

        openGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key, ClickType clickType) {
        if(!key.startsWith("rankpermissions.buttons.")) return;

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return;

        String permission = key.replace("rankpermissions.buttons.", "").toUpperCase();

        // TODO: CHECK PERMISSIONS
        if(!PermUtil.canGrantPermissionRankCheck(user, selectedRank)) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorHigherRank));
            return;
        }

        if(!PermUtil.hasPermission(user, Permission.PERMISSIONS)) {
            return;
        }

        if(user.getIsland().hasPermission(selectedRank, permission)) {
            user.getIsland().revokePermission(selectedRank, permission);
        } else {
            user.getIsland().grantPermission(selectedRank, permission);
        }

        new IslandRankPermMenu(selectedRank).open(player);
    }

    @Override
    public int backButtonSlot() {
        return 31;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        ArrayList<Menuable> menuRankPermissionsButtons = new ArrayList<>(MenuUtil.menuRankPermissionsButtons);

        ArrayList<Menuable> buttonToReturn = new ArrayList<>();

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return null;

        for(Menuable menuable : menuRankPermissionsButtons) {

            String permissionName = menuable.getKeyPrefix().substring(menuable.getKeyPrefix().lastIndexOf(".") + 1);
            ArrayList<Component> newMen = new ArrayList<>();

            for(String lore : MenuUtil._menuFile.getStringList(menuable.getKeyPrefix() + ".lore")) {
                newMen.add(HexUtils.colour(lore.replace("%status%", (user.getIsland().hasPermission(selectedRank, permissionName) ? "&aEnabled" : "&cDisabled"))));
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