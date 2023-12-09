package lol.maltest.islandsmp.upgrade;

import lol.maltest.islandsmp.entities.Island;
import lol.maltest.islandsmp.upgrade.requirement.Requirement;
import lol.maltest.islandsmp.upgrade.requirement.type.MoneyRequirement;
import lol.maltest.islandsmp.upgrade.requirement.type.SpinRequirement;
import lol.maltest.islandsmp.utils.UpgradeType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Tier {

    @Getter private final UpgradeType upgradeType;
    @Getter private final int requiredSpins;
    @Getter private final int requiredMoney;
    private List<Requirement> requirements = new ArrayList<>();

    public Tier(UpgradeType upgradeType, int requiredSpins, int requiredMoney) {
        this.upgradeType = upgradeType;
        this.requiredSpins = requiredSpins;
        this.requiredMoney = requiredMoney;

        requirements.add(new SpinRequirement(requiredSpins));
        requirements.add(new MoneyRequirement(requiredMoney));
    }

    public boolean canUpgrade(Player requester, Island island) {
        return requirements.stream().allMatch(req -> req.isMet(upgradeType, requester, island));
    }



}