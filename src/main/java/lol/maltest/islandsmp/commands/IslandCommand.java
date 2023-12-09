package lol.maltest.islandsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.sub.*;
import lol.maltest.islandsmp.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;

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
    public void onIslandInviteCommand(Player player, @Name("Player") String target) {
        User user = checkIslandExistence(player);

        if (user == null) {
            return;
        }

        if(!PermUtil.hasPermission(user, Permission.INVITE)) return;

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
    public void onIslandJoinCommand(@NotNull Player player, @Name("Player") String target) {
        UUID inviterUniqueId = plugin.invites.get(player.getUniqueId());

        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() != null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandCantJoin));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(target);

        if(targetPlayer == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantFindPlayer.replace("%player%", target)));
            plugin.invites.remove(player.getUniqueId());
            return;
        }

        if (targetPlayer.getUniqueId().equals(inviterUniqueId)) {
            plugin.invites.remove(player.getUniqueId()); // Remove the invite as it has been responded to.
            player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandJoined.replaceAll("%player%", targetPlayer.getName())));
            targetPlayer.sendMessage(HexUtils.colour(LanguageUtil.messageIslandInviteAccepted.replaceAll("%player%", player.getName())));

            User userInviter = UserCache.getUser(targetPlayer.getUniqueId());

            user.setIslandUUID(userInviter.getIslandUUID());
            userInviter.getIsland().addIslandMember(player);

        } else {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorNoInvite.replace("%player%", target)));
        }
    }

    @Subcommand("leave")
    public void onLeaveCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        Island island = user.getIsland();

        if(island.getIslandOwner().equals(player.getUniqueId())) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantLeaveIsland));
            return;
        }

        island.removeIslandMember(player);
        user.setIslandUUID(null);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "espawn " + player.getName());
    }

    @Subcommand("lock")
    public void onLockCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }


        if(!PermUtil.isModOrHigher(user)) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantLockIsland.replace("%status%", "lock")));
            return;
        }

        for(Player p : plugin.getBorderManager().getPlayersOnIsland(user.getIsland())) {
            if(!user.getIsland().isIslandMember(p)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "espawn " + p.getName());
            }
        }

        user.getIsland().setLocked(true);
        player.sendMessage(HexUtils.colour(LanguageUtil.messageLockedIslandSuccess.replace("%status%", "locked")));
    }

    @Subcommand("unlock")
    public void onUnlockCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        if(!PermUtil.isModOrHigher(user)) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantLockIsland.replace("%status%", "unlock")));
            return;
        }

        user.getIsland().setLocked(false);
        player.sendMessage(HexUtils.colour(LanguageUtil.messageLockedIslandSuccess.replace("%status%", "unlocked")));
    }

    @Subcommand("settings")
    public void onSettingsCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        new IslandSettingsMenu().open(player);
    }

    @Subcommand("warps")
    public void onWarpsCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        new IslandWarpsMenu().open(player);
    }

    @Subcommand("members")
    public void onMembersCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        new IslandMembersMenu().open(player);
    }

    @Subcommand("trusted")
    public void onTrustedCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        new IslandTrustedMenu().open(player);
    }

    @Subcommand("upgrades")
    public void onUpgradesCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        // todo change to upgrades
        new IslandTrustedMenu().open(player);
    }

    @Subcommand("setwarp")
    public void onSetWarpCommand(Player player, @Name("Warp name") String warpName) {
    User user = checkIslandExistence(player);

        if(user == null) {
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

    @Subcommand("setvisit")
    public void onSetVisitCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        if(!PermUtil.hasPermission(user, Permission.SETHOME)) {
            return;
        }

        Island island = plugin.getBorderManager().getIsland(player.getLocation());

        if(island == null || !island.isIslandMember(player)) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantSetVisitLocation));
            return;
        }

        user.getIsland().getIslandLocation().setVisitorLocation(player.getLocation());
        player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandSetVisitorLoc));
    }

    @Subcommand("trust")
    @CommandCompletion("@players")
    public void onIslandGoCommand(Player player, @Name("Player") String target) {
        User user = checkIslandExistence(player);

        if (user == null) {
            return;
        }

        if(!PermUtil.isAdminOrHigher(user)) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorMembersCantTrustPlayer));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(target);


        if(targetPlayer == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantFindPlayer.replace("%player%", target)));
            return;
        }

        user.getIsland().addTrustedMember(targetPlayer);
        player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandTrustedPlayer.replace("%player%", target)));
    }

    @Subcommand("home|go")
    public void onIslandGoCommand(Player player) {
    User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        player.teleport(user.getIsland().getIslandLocation().getSpawnLocation());
    }

    @Subcommand("create")
    public void onCreateCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() != null) {
            player.sendMessage(HexUtils.colour("&dcʏᴏᴜ ᴀʟʀᴇᴀᴅʏ ʜᴀᴠᴇ ᴀɴ ɪsʟᴀɴᴅ!"));
            return;
        }

        plugin.getGridManager().createIsland(player);
    }

    @Subcommand("visit")
    @CommandCompletion("@players")
    public void onVisitCommand(Player player, @Name("Player") String target) {
        Player targetPlayer = Bukkit.getPlayer(target);


        if(targetPlayer == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantFindPlayer.replace("%player%", target)));
            return;
        }

        User targetUser = UserCache.getUser(targetPlayer.getUniqueId());
        if(targetUser == null) return;

        Island targetIsland = targetUser.getIsland();

        if(targetIsland == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantVisitIsland.replace("%player%", target)));
            return;
        }

        if(targetIsland.isLocked()) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantVisitLocked.replace("%player%", target)));
            return;
        }

        if(!targetIsland.getIslandLocation().hasVisitorLocation()) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorCantVisitNoVisitorLocation.replace("%player%", target)));
            return;
        }

        player.teleport(targetIsland.getIslandLocation().getVisitorLocation());
    }

    /**
     * Admin commands below
     */

    @Subcommand("forcesave")
    @CommandPermission("minecraft.operator")
    public void onForceSaveCommand(Player player) {
        for (Island island : IslandCache.activeIslands) {
            plugin.getIslandCache().islandStorage.saveAsync(island);
        }
        player.sendMessage("done");
    }


    @Subcommand("tesgui")
    @CommandPermission("minecraft.operator")
    public void onTestGuiCommand(Player player) {
        new IslandUpgradeMenu().open(player);
    }


    private User checkIslandExistence(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.publicNeedIsland));
            return null;
        }

        return user;
    }
    
}
