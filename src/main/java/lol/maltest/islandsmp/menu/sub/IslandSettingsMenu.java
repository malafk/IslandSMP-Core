package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.menu.DynamicMenuItem;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.*;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class IslandSettingsMenu extends Menu {

    Gui gui;

    @Override
    public void open(Player player) {

        setPreviousMenu(IslandMainMenu.class);

        gui = Gui.gui()
                .title(MenuUtil.menuSettingsTitle)
                .rows(MenuUtil.menuSettingsRows)
                .create();

        openGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key, ClickType clickType) {
        if(!key.startsWith("settings.buttons.")) return;

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return;

        String setting = key.replace("settings.buttons.", "").toUpperCase();

        if(!PermUtil.hasPermission(user, Permission.SETTINGS)) return;

        user.getIsland().toggleSetting(setting);

        new IslandSettingsMenu().open(player);

    }

    @Override
    public int backButtonSlot() {
        return 22;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        ArrayList<Menuable> settingsMenu = new ArrayList<>(MenuUtil.menuSettingsButtons);

        ArrayList<Menuable> buttonToReturn = new ArrayList<>();

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return null;

        for(Menuable menuable : settingsMenu) {

            String settingName = menuable.getKeyPrefix().substring(menuable.getKeyPrefix().lastIndexOf(".") + 1);
            ArrayList<Component> newMen = new ArrayList<>();

            for(String lore : MenuUtil._menuFile.getStringList(menuable.getKeyPrefix() + ".lore")) {
                newMen.add(HexUtils.colour(lore.replace("%status%", (user.getIsland().isSettingActive(settingName) ? "&aEnabled" : "&cDisabled"))));
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