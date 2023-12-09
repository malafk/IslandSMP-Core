package lol.maltest.islandsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.cache.UserCache;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.entities.User;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.LanguageUtil;
import lol.maltest.islandsmp.utils.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IslandDisbandCommand extends BaseCommand {

    private IslandSMP plugin;

    private final Set<UUID> confirmDisband = new HashSet<>();

    public IslandDisbandCommand(IslandSMP plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("disband")
    @Default
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
}