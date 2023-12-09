package lol.maltest.islandsmp.upgrade.requirement.type;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.upgrade.requirement.Requirement;
import lol.maltest.islandsmp.utils.UpgradeType;
import org.bukkit.entity.Player;

public class SpinRequirement implements Requirement {

    private int successfulAmount;

    public SpinRequirement(int successfulAmount) {
        this.successfulAmount = successfulAmount;
    }

    @Override
    public boolean isMet(UpgradeType upgradeType, Player player, Island island) {
        return island.getAmountSpins(upgradeType) >= successfulAmount;
    }
}
