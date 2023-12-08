package lol.maltest.islandsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.sub.IslandMainMenu;
import lol.maltest.islandsmp.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("island|is")
public class IslandCommand extends BaseCommand {

    private IslandSMP plugin;

    public IslandCommand(IslandSMP plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onIslandCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        if(user.getIsland() == null) {
            plugin.getGridManager().createIsland(player);
            return;
        }

        Menu menu = new IslandMainMenu();
        menu.open(player);
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void onIslandInviteCommand(Player player, String target) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() == null) {
            player.sendActionBar(HexUtils.colour(LanguageUtil.publicNeedIsland));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(target);

        if(targetPlayer == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantFindPlayer.replace("%player%", target)));
            return;
        }

        plugin.invites.put(targetPlayer.getUniqueId(), player.getUniqueId());
        player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandInvited.replace("%player%", targetPlayer.getName())));
        targetPlayer.sendMessage(HexUtils.colour(LanguageUtil.messageIslandReceivedInvite.replaceAll("%invitee%", player.getName())));
    }


    @Subcommand("join|accept")
    @CommandCompletion("@players")
    public void onIslandJoinCommand(Player player, String target) {
        UUID inviterUniqueId = plugin.invites.get(player.getUniqueId());

        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() != null) {
            player.sendActionBar(HexUtils.colour(LanguageUtil.messageIslandCantJoin));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(target);

        if(targetPlayer == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantFindPlayer.replace("%player%", target)));
            plugin.invites.remove(player.getUniqueId());
            return;
        }

        // Check if player has an invite from the target player
        if (targetPlayer.getUniqueId().equals(inviterUniqueId)) {
            plugin.invites.remove(player.getUniqueId()); // Remove the invite as it has been responded to.
            player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandJoined.replaceAll("%player%", targetPlayer.getName())));
            targetPlayer.sendMessage(HexUtils.colour(LanguageUtil.messageIslandInviteAccepted.replaceAll("%player%", player.getName())));
            // todo: Add code to handle the player joining the island

            User userInviter = UserCache.getUser(targetPlayer.getUniqueId());

            user.setIslandUUID(userInviter.getIslandUUID());
            userInviter.getIsland().addIslandMember(player);

        } else {
            player.sendMessage("You do not have an invite from " + targetPlayer.getName() + ".");
        }
    }

    @Subcommand("setwarp")
    public void onSetWarpCommand(Player player, String warpName) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() == null) {
            player.sendActionBar(HexUtils.colour(LanguageUtil.publicNeedIsland));
            return;
        }

        if(!PermUtil.hasPermission(user, Permission.SETWARP)) {
            return;
        }

        if(user.getIsland().getFreeWarps() == 0) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorWarpsMax));
            return;
        }

        user.getIsland().setNewWarp(warpName, player);
        player.sendMessage(HexUtils.colour(LanguageUtil.messageWarpCreated.replace("%warp%", warpName)));

        // Todo: alert all island members that they put a warp
    }

    @Subcommand("go")
    public void onIslandGoCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() == null) {
            player.sendActionBar(HexUtils.colour(LanguageUtil.publicNeedIsland));
            return;
        }

        player.teleport(user.getIsland().getIslandLocation().getSpawnLocation());
    }

    @Subcommand("create")
    public void onCreateCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() != null) {
            player.sendActionBar(HexUtils.colour("&dcʏᴏᴜ ᴀʟʀᴇᴀᴅʏ ʜᴀᴠᴇ ᴀɴ ɪsʟᴀɴᴅ!"));
            return;
        }

        plugin.getGridManager().createIsland(player);
    }

    @Subcommand("forcesave")
    @CommandPermission("minecraft.operator")
    public void onForceSaveCommand(Player player) {
        for (Island island : IslandCache.activeIslands) {
            plugin.getIslandCache().islandStorage.saveAsync(island);
        }
        player.sendMessage("done");
    }

}
