package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import lol.maltest.islandsmp.menu.DynamicMenuItem;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.MenuItem;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.MenuUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class IslandMembersMenu extends Menu {

    @Override
    public void open(Player player) {

        setPreviousMenu(IslandMainMenu.class);

        PaginatedGui gui = Gui.paginated()
                .title(MenuUtil.menuMembersTitle)
                .rows(3)
                .create();

        openPaginatedGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key) {
        // TODO: Provide your implementation
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        List<Menuable> items = new ArrayList<>();

        // Dynamic slot for positioning menu items
        int dynamicSlot = 0;

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return null;

        Island island = IslandCache.getIsland(user.getIslandUUID());

        for(IslandMember member : island.getIslandMembers()) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member.getPlayerUuid());

            ArrayList<Component> finalLore = new ArrayList<>();

            for(String lore : MenuUtil.menuMembersButtonRawLore) {
                finalLore.add(HexUtils.colour(lore
                        .replace("%player%", member.getPlayerName())
                        .replace("%status%", (offlinePlayer.isOnline() ? "&aOnline" : "&cOffline"))
                        .replace("%rank%", member.getRank().getDisplay())
                ));
            }

            items.add(new DynamicMenuItem(
                    Material.PLAYER_HEAD,
                    MenuUtil.menuMembersButtonName.replace("%player%", member.getPlayerName()),
                    finalLore,
                    "members.menu." + member.getPlayerUuid(),
                    dynamicSlot++,
                    offlinePlayer
            ));
        }

        return items;
    }
}