package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import lol.maltest.islandsmp.entities.sub.IslandWarp;
import lol.maltest.islandsmp.menu.DynamicMenuItem;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.LanguageUtil;
import lol.maltest.islandsmp.utils.MenuUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandWarpsMenu extends Menu {

    @Override
    public void open(Player player) {

        setPreviousMenu(IslandMainMenu.class);

        PaginatedGui gui = Gui.paginated()
                .title(MenuUtil.menuWarpsTitle)
                .rows(3)
                .create();

        openPaginatedGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key, ClickType clickType) {
        if(!key.startsWith("warp.")) return;

        String warpName = key.replace("warp.", "");

        User user = UserCache.getUser(player.getUniqueId());

        Island island = user.getIsland();

        if(island == null) {
            player.closeInventory();
            return;
        }

        IslandWarp warp = island.getWarpByName(warpName);

        if(warp == null) {
            player.closeInventory();
            return;
        }

        player.teleport(warp.getWarpLocation());
    }

    @Override
    public int backButtonSlot() {
        return 22;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        List<Menuable> items = new ArrayList<>();

        int dynamicSlot = 0;

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return null;

        Island island = IslandCache.getIsland(user.getIslandUUID());

        for(IslandWarp warp : island.getIslandWarps()) {

            ArrayList<Component> finalLore = new ArrayList<>();

            for(String lore : MenuUtil.menuWarpsButtonRawLore) {
                finalLore.add(HexUtils.colour(lore
                        .replace("%x%", warp.getWarpLocation().getX() + "")
                        .replace("%y%", warp.getWarpLocation().getY() + "")
                        .replace("%z%", warp.getWarpLocation().getZ() + "")
                ));
            }

            items.add(new DynamicMenuItem(
                    Material.ENDER_PEARL,
                    MenuUtil.menuWarpsButtonName.replace("%warp%", warp.getWarpName()),
                    finalLore,
                    "warp." + warp.getWarpName(),
                    dynamicSlot++,
                    null
            ));
        }

        return items;
    }
}