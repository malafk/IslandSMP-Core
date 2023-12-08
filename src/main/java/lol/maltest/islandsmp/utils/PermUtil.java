package lol.maltest.islandsmp.utils;

import lol.maltest.islandsmp.entities.User;
import org.bukkit.Bukkit;
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

    public static boolean canGrantPermissionRankCheck(User user, Rank selectedRank) {
        Player player = Bukkit.getPlayer(user.getPlayer());
        if(player == null) return false;

        Rank userRank = user.getIsland().getPlayerRank(player);
        if(selectedRank == userRank) return false;
        System.out.println("User is " + userRank);
        return !userRank.isLowerThan(selectedRank);
    }

}
