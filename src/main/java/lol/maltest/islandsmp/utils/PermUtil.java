package lol.maltest.islandsmp.utils;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PermUtil {

    public static boolean hasPermission(User user, Permission permission) {
        Player player = Bukkit.getPlayer(user.getPlayer());
        if(player == null) return false;

        boolean hasPerm =  user.getIsland().hasPermission(player, permission.name());

        if(!hasPerm) {
            player.sendMessage(HexUtils.colour(LanguageUtil.publicNeedPermission.replace("%permission%", Permission.PERMISSIONS.name())));
        }

        return hasPerm;
    }

    public static boolean hasPermissionInCurrentIsland(Player player, Permission permission) {
        User user = UserCache.getUser(player.getUniqueId());
        Island island = IslandSMP.getInstance().getBorderManager().getPlayerIsland(player);

        if(island == null) return false;

        if(!island.hasPermission(player, permission.name()) || user == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.publicNeedPermission.replace("%permission%", permission.name())));
            return false;
        }


        return true;
    }

    @Deprecated(forRemoval = true)
    public static Island getIslandByLocation(Location location) {
        for(Island island : IslandCache.activeIslands) {
            if(island.isLocationInsideIsland(location)) {
                return island;
            }
        }
        return null;
    }

    public static boolean canModifyRank(User user, Rank selectedRank) {
        Player player = Bukkit.getPlayer(user.getPlayer());
        if(player == null) return false;

        Rank userRank = user.getIsland().getPlayerRank(player);
        if(selectedRank == userRank) return false;
        return !userRank.isLowerThan(selectedRank);
    }

    public static boolean isAdminOrHigher(User user) {
        Player player = Bukkit.getPlayer(user.getPlayer());

        if(player == null) return false;

        Rank userRank = user.getIsland().getPlayerRank(player);

        return userRank == Rank.ADMINISTRATOR || userRank == Rank.OWNER;
    }

    public static boolean isModOrHigher(User user) {
        Player player = Bukkit.getPlayer(user.getPlayer());

        if(player == null) return false;

        Rank userRank = user.getIsland().getPlayerRank(player);

        return userRank == Rank.ADMINISTRATOR || userRank == Rank.OWNER || userRank == Rank.MODERATOR;
    }

}
