package lol.maltest.islandsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.IslandCache;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.QueuePlayer;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.menu.Menu;
import lol.maltest.islandsmp.menu.sub.*;
import lol.maltest.islandsmp.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@CommandAlias("island|is|team")
public class IslandCommand extends BaseCommand {

    private IslandSMP plugin;

    private final Set<UUID> confirmDisband = new HashSet<>();


    public IslandCommand(IslandSMP plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onIslandCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        if(user.getIsland() == null) {
            QueuePlayer qP = new QueuePlayer(player.getUniqueId());
            plugin.getQueueManager().addPlayer(qP);
            return;
        }

        Menu menu = new IslandMainMenu();
        menu.open(player);
    }

    @Subcommand("invite")
    @CommandAlias("invite")
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
    @CommandAlias("join|accept")
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
    @CommandAlias("leave")
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
    @CommandAlias("lock")
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
    @CommandAlias("unlock")
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
    @CommandAlias("settings")
    public void onSettingsCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        new IslandSettingsMenu().open(player);
    }

    @Subcommand("warps")
    @CommandAlias("warps")
    public void onWarpsCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        new IslandWarpsMenu().open(player);
    }

    @Subcommand("members")
    @CommandAlias("members")
    public void onMembersCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        new IslandMembersMenu().open(player);
    }

    @Subcommand("trusted")
    @CommandAlias("trusted")
    public void onTrustedCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        new IslandTrustedMenu().open(player);
    }

    @Subcommand("upgrades")
    @CommandAlias("upgrades")
    public void onUpgradesCommand(Player player) {
        User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }

        if(!PermUtil.hasPermission(user, Permission.UPGRADE)) return;

        new IslandUpgradeMenu().open(player);
    }

    @Subcommand("setwarp|addwarp")
    @CommandAlias("setwarp|addwarp")
    public void onSetWarpCommand(Player player, @Name("Warp name") String warpName) {
    User user = checkIslandExistence(player);

        if(user == null) {
            return;
        }


        if(!PermUtil.hasPermission(user, Permission.SETWARP)) {
            return;
        }

        if(user.getIsland().getMaxWarps() == 0) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorWarpsMax));
            return;
        }

        user.getIsland().setNewWarp(warpName, player);
        player.sendMessage(HexUtils.colour(LanguageUtil.messageWarpCreated.replace("%warp%", warpName)));

        // Todo: alert all island members that they put a warp
    }

    @Subcommand("setvisit")
    @CommandAlias("setvisit")
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

    @Subcommand("trust|coop")
    @CommandAlias("trust|coop")
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

        if (user.getIsland().getTrustedMembers().size() >= user.getIsland().getMaxTrusted()) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorTrustedMaxLimit));
            return;
        }

        user.getIsland().addTrustedMember(targetPlayer);
        player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandTrustedPlayer.replace("%player%", target)));
    }

    @Subcommand("home|go")
    @CommandAlias("home|go")
    public void onIslandGoCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        if (user.getIsland() == null) {
            QueuePlayer qP = new QueuePlayer(player.getUniqueId());
            plugin.getQueueManager().addPlayer(qP);
            return;
        }

        if(!PermUtil.hasPermission(user, Permission.HOME)) return;

        player.teleport(user.getIsland().getIslandLocation().getSpawnLocation());
    }

    @Subcommand("create")
    @CommandAlias("create")
    public void onCreateCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());

        // Check if the player has an island
        if (user.getIsland() != null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorAlreadyHaveIsland));
            return;
        }

        QueuePlayer qP = new QueuePlayer(player.getUniqueId());
        plugin.getQueueManager().addPlayer(qP);
    }

    @Subcommand("visit")
    @CommandAlias("visit")
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

    @Subcommand("disband")
    @CommandAlias("disband")
    public void onDisbandCommand(Player player) {
        User user = UserCache.getUser(player.getUniqueId());
        UUID playerId = player.getUniqueId();

        // Check if the player has an island
        if (user.getIsland() == null) {
            player.sendMessage(HexUtils.colour(LanguageUtil.publicNeedIsland));
            return;
        }

        if(user.getIsland().getPlayerRank(player) != Rank.OWNER) {
            player.sendMessage(HexUtils.colour(LanguageUtil.errorOnlyOwner));
            return;
        }

        // If player isn't in the confirm set
        if (!confirmDisband.contains(playerId)) {
            player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandDisbandSure));
            confirmDisband.add(playerId);

            // schedule to remove the player from the confirm set after 30 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> confirmDisband.remove(playerId), 600L);
        } else {
            // Player is in the confirm set, go ahead with disbanding
            confirmDisband.remove(playerId);

            UUID islanduuid = user.getIslandUUID();

            plugin.getGridManager().disbandIsland(islanduuid);
//            plugin.getIslandCache().deleteIslandFromDatabase(islanduuid);

            // We dont delete as that would mess up getFreeLocation

            // The disband logic comes here
            player.sendMessage(HexUtils.colour(LanguageUtil.messageIslandDisbanded));

            confirmDisband.remove(playerId);
        }
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

    @Subcommand("queuealldebug")
    @CommandPermission("minecraft.operator")
    public void onTestCommand(Player player) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);
        for(Player player1 : players) {
            player1.chat("/is create");
        }
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
