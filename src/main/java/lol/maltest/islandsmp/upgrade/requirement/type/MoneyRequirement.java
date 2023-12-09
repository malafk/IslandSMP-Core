package lol.maltest.islandsmp.upgrade.requirement.type;

import lol.maltest.islandsmp.IslandSMP;
import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.upgrade.requirement.Requirement;
import lol.maltest.islandsmp.utils.UpgradeType;
import org.bukkit.entity.Player;

public class MoneyRequirement implements Requirement {
    private final double amountRequired;

    public MoneyRequirement(double amountRequired) {
        this.amountRequired = amountRequired;
    }

    @Override
    public boolean isMet(UpgradeType upgradeType, Player player, Island island) {
        double money = IslandSMP.getInstance().getEconomy().getBalance(player);

        return money >= amountRequired;
    }
}