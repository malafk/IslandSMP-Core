package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.entities.sub.IslandMember;
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

public class IslandTrustedMenu extends Menu {

    @Override
    public void open(Player player) {

        setPreviousMenu(IslandMainMenu.class);

        PaginatedGui gui = Gui.paginated()
                .title(MenuUtil.menuTrustedTitle)
                .rows(3)
                .create();

        openPaginatedGui(gui, player);
    }

    @Override
    public void onClick(Player player, String key, ClickType clickType) {
        if(!key.startsWith("trusted.menu.")) return;

        UUID targetUuid = UUID.fromString(key.replace("trusted.menu.", ""));

        User user = UserCache.getUser(player.getUniqueId());

        Island island = user.getIsland();

        if(island == null) {
            player.closeInventory();
            return;
        }

        // todo check perms, then check the click type and do whatever
    }

    @Override
    public int backButtonSlot() {
        return 22;
    }

    @Override
    public List<Menuable> getMenuItems(Player player) {
        List<Menuable> items = new ArrayList<>();

        // Dynamic slot for positioning menu items
        int dynamicSlot = 0;

        User user = UserCache.getUser(player.getUniqueId());

        if(user == null) return null;

        Island island = IslandCache.getIsland(user.getIslandUUID());

        for(UUID member : island.getTrustedMembers()) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);

            ArrayList<Component> finalLore = new ArrayList<>();

            for(String lore : MenuUtil.menuTrustedButtonRawLore) {
                finalLore.add(HexUtils.colour(lore
                        .replace("%player%", offlinePlayer.getName())
                        .replace("%status%", (offlinePlayer.isOnline() ? "&aOnline" : "&cOffline"))
                ));
            }

            items.add(new DynamicMenuItem(
                    Material.PLAYER_HEAD,
                    MenuUtil.menuMembersButtonName.replace("%player%", offlinePlayer.getName()),
                    finalLore,
                    "trusted.menu." + member,
                    dynamicSlot++,
                    offlinePlayer
            ));
        }

        return items;
    }
}