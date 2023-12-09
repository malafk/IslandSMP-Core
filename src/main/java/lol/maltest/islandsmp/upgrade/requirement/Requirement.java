package lol.maltest.islandsmp.upgrade.requirement;

import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.utils.UpgradeType;
import org.bukkit.entity.Player;

public interface Requirement {
    boolean isMet(UpgradeType upgradeType, Player player, Island island);
}