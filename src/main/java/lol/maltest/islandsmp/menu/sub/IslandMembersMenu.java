package lol.maltest.islandsmp.menu.sub;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.entities.sub.IslandMember;
import lol.maltest.islandsmp.menu.DynamicMenuItem;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.Menuable;
import lol.maltest.islandsmp.utils.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public void onClick(Player player, String key, ClickType clickType) {
        if(!key.startsWith("members.menu.")) return;

        UUID targetUuid = UUID.fromString(key.replace("members.menu.", ""));

        User user = UserCache.getUser(player.getUniqueId());

        Island island = user.getIsland();

        if(island == null) {
            player.closeInventory();
            return;
        }

        if(island.getIslandOwner() == targetUuid) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorMembersCantModifyOwner));
            return;
        }

        if(player.getUniqueId() == targetUuid) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorMembersCantModifySelf));
            return;
        }

        User targetPlayer = UserCache.getUser(targetUuid);

        if(targetPlayer == null) {
            IslandSMP.getInstance().getUserCache().cacheProfileFromDatabase(targetUuid);
            targetPlayer = UserCache.getUser(targetUuid);
        }

        switch (clickType) {
            case LEFT -> {
                if (!PermUtil.hasPermission(user, Permission.PROMOTE)) return;

                Rank targetRank = user.getIsland().getIslandMemberByUUID(targetUuid).getRank();

                if (!PermUtil.canModifyRank(user, targetRank)) {
                    player.sendMessage(HexUtils.colour(LanguageUtil.errorMembersCantModifyPlayer));
                    return;
                }

                Rank rankToBePromotedTo;
                switch (targetRank) {
                    case MEMBER -> rankToBePromotedTo = Rank.MODERATOR;
                    case MODERATOR -> rankToBePromotedTo = Rank.ADMINISTRATOR;
                    default -> rankToBePromotedTo = null;
                }

                if (rankToBePromotedTo == null) {
                    player.sendMessage(HexUtils.colour(LanguageUtil.errorCantPromoteMember));
                    return;
                }

                targetPlayer.getIsland().getIslandMemberByUUID(targetUuid).setRank(rankToBePromotedTo);
                player.sendMessage(HexUtils.colour(LanguageUtil.messagesMembersUpdated.replace("%status%", "promoted").replace("%rank%", rankToBePromotedTo.getDisplay().replace("%player%", Bukkit.getOfflinePlayer(targetUuid).getName()))));
            }
            case RIGHT -> {
                if(!PermUtil.hasPermission(user, Permission.DEMOTE)) return;

                Rank targetRank = user.getIsland().getIslandMemberByUUID(targetUuid).getRank();

                if(!PermUtil.canModifyRank(user, targetRank)) {
                    player.sendMessage(HexUtils.colour(LanguageUtil.errorMembersCantModifyPlayer));
                    return;
                }

                Rank rankToBeDemotedTo;
                switch (targetRank) {
                    case ADMINISTRATOR -> rankToBeDemotedTo = Rank.MODERATOR;
                    case MODERATOR ->  rankToBeDemotedTo = Rank.MEMBER;
                    default -> rankToBeDemotedTo = null;
                }

                if(rankToBeDemotedTo == null) {
                    player.sendMessage(HexUtils.colour(LanguageUtil.errorCantDemoteMember));
                    return;
                }

                targetPlayer.getIsland().getIslandMemberByUUID(targetUuid).setRank(rankToBeDemotedTo);
                player.sendMessage(HexUtils.colour(LanguageUtil.messagesMembersUpdated.replace("%status%", "demoted").replace("%rank%", rankToBeDemotedTo.getDisplay().replace("%player%", Bukkit.getOfflinePlayer(targetUuid).getName()))));
            }
            case MIDDLE -> {
                if(!PermUtil.hasPermission(user, Permission.KICK)) return;

                Rank targetRank = user.getIsland().getIslandMemberByUUID(targetUuid).getRank();

                if(!PermUtil.canModifyRank(user, targetRank)) {
                    player.sendMessage(HexUtils.colour(LanguageUtil.errorMembersCantModifyPlayer));
                    return;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUuid);

                targetPlayer.getIsland().getIslandMembers().remove(targetPlayer.getIsland().getIslandMemberByUUID(target.getUniqueId()));

                if(target.isOnline()) {
                    targetPlayer.setIslandUUID(null);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "espawn " + target.isOnline());
                    target.getPlayer().sendMessage(HexUtils.colour(LanguageUtil.messageIslandKicked));
                } else {
                    targetPlayer.setIslandUUID(IslandSMP.getInstance().getNullUuid());
                }
            }
        }

        new IslandMembersMenu().open(player);
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