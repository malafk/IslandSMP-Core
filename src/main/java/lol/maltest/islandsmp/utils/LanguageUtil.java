package lol.maltest.islandsmp.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LanguageUtil {

    private YamlDocument _languageFile;

    public static String PREFIX;

    public static String publicNeedIsland;
    public static String publicNeedPermission;

    public static String errorMembersCantModifyOwner;
    public static String errorMembersCantModifySelf;
    public static String errorMembersCantTrustPlayer;
    public static String errorMembersCantModifyPlayer;

    public static String errorOnlyOwner;
    public static String errorWarpsMax;
    public static String errorHigherRank;
    public static String errorNoInvite;
    public static String errorCantLockIsland;
    public static String errorCantVisitIsland;
    public static String errorCantVisitNoVisitorLocation;
    public static String errorCantSetVisitLocation;
    public static String errorCantLeaveIsland;
    public static String errorCantDemoteMember;
    public static String errorCantPromoteMember;

    public static String errorCantFindPlayer;
    public static String errorCantVisitLocked;
    public static String errorTrustedMaxLimit;
    public static String errorAlreadyHaveIsland;

    public static String messageIslandKicked;
    public static String messageIslandCreated;
    public static String messageIslandCreating;
    public static String messageIslandDisbanded;
    public static String messageIslandDisbandSure;
    public static String messageWarpCreated;
    public static String messageIslandInvited;
    public static String messageIslandReceivedInvite;
    public static String messageIslandCantJoin;
    public static String messageIslandJoined;
    public static String messageIslandInviteAccepted;
    public static String messageIslandCantInterEntity;
    public static String messageIslandSetVisitorLoc;
    public static String messageIslandTrustedPlayer;
    public static String messageLockedIslandSuccess;
    public static String messagesUpgradeTierUp;
    public static String messagesUpgradeDiscovered;
    public static String messagesUpgradeMaxedOut;
    public static String messagesUpgradeNotEnoughPs;

    public static String messagesUpgradeFoundSpin;
    public static String messagesUpgradeCanUpgrade;

    public static String messagesMembersUpdated;

    public LanguageUtil(JavaPlugin plugin) {
        try {
            _languageFile = YamlDocument.create(new File(plugin.getDataFolder(), "language.yml"), getClass().getResourceAsStream("/language.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PREFIX = _languageFile.getString("prefix");

        publicNeedIsland = PREFIX + _languageFile.getString("errors.global.need-island");
        publicNeedPermission = PREFIX + _languageFile.getString("errors.global.need-permission");

        errorMembersCantModifyOwner = PREFIX + _languageFile.getString("errors.members.cant-modify-owner");
        errorMembersCantModifySelf = PREFIX + _languageFile.getString("errors.members.cant-modify-owner");
        errorMembersCantTrustPlayer = PREFIX + _languageFile.getString("errors.members.cant-trust-player");
        errorOnlyOwner = PREFIX + _languageFile.getString("errors.global.only-owner-disband");
        errorWarpsMax = PREFIX + _languageFile.getString("errors.warps.max-warps");
        errorHigherRank = PREFIX + _languageFile.getString("errors.rankpermissions.higher-rank");
        errorNoInvite = PREFIX + _languageFile.getString("errors.global.no-invite");
        errorCantFindPlayer = PREFIX + _languageFile.getString("errors.global.cant-find-player");
        errorCantLockIsland = PREFIX + _languageFile.getString("errors.global.cant-lock-island");
        errorCantVisitLocked = PREFIX + _languageFile.getString("errors.global.island-locked");
        errorCantVisitIsland = PREFIX + _languageFile.getString("errors.global.cant-visit-island");
        errorCantVisitNoVisitorLocation = PREFIX + _languageFile.getString("errors.global.cant-visit-no-visitor");
        errorCantSetVisitLocation = PREFIX + _languageFile.getString("errors.global.cant-set-visitor-location");
        errorCantLeaveIsland = PREFIX + _languageFile.getString("errors.global.cant-leave");
        errorTrustedMaxLimit = PREFIX + _languageFile.getString("errors.trusted.max-trusted");
        errorAlreadyHaveIsland = PREFIX + _languageFile.getString("errors.global.already-have-island");
        errorMembersCantModifyPlayer = PREFIX + _languageFile.getString("errors.members.cant-modify-player");
        errorCantDemoteMember = PREFIX + _languageFile.getString("errors.members.cant-demote-member");
        errorCantPromoteMember = PREFIX + _languageFile.getString("errors.members.cant-promote-member");

        messageIslandCreated = PREFIX + _languageFile.getString("messages.island.created");
        messageIslandCreating = PREFIX + _languageFile.getString("messages.island.creating");

        messageWarpCreated = PREFIX + _languageFile.getString("messages.warps.created");
        messageIslandKicked = PREFIX + _languageFile.getString("messages.island.kicked-from-island");
        messageIslandDisbanded = PREFIX + _languageFile.getString("messages.island.disbanded");
        messageIslandDisbandSure = PREFIX + _languageFile.getString("messages.island.are-you-sure-disband");
        messageIslandInvited = PREFIX + _languageFile.getString("messages.island.invited");
        messageIslandReceivedInvite = PREFIX + _languageFile.getString("messages.island.received-invite");
        messageIslandCantJoin = PREFIX + _languageFile.getString("messages.island.cant-join-island");
        messageIslandJoined = PREFIX + _languageFile.getString("messages.island.joined-island");
        messageIslandInviteAccepted = PREFIX + _languageFile.getString("messages.island.accepted-invite");
        messageIslandCantInterEntity = PREFIX + _languageFile.getString("messages.island.cant-interact-entities");
        messageIslandSetVisitorLoc  = PREFIX + _languageFile.getString("messages.island.set-visitor-location");
        messageIslandTrustedPlayer  = PREFIX + _languageFile.getString("messages.island.trusted-player");
        messageLockedIslandSuccess = PREFIX + _languageFile.getString("messages.island.locked-island");

        messagesUpgradeTierUp = PREFIX + _languageFile.getString("messages.upgrades.tier-up");
        messagesUpgradeDiscovered = PREFIX + _languageFile.getString("messages.upgrades.discovered");

        messagesUpgradeFoundSpin = PREFIX + _languageFile.getString("messages.upgrades.found-spin");
        messagesUpgradeCanUpgrade = PREFIX + _languageFile.getString("messages.upgrades.can-upgrade");

        messagesUpgradeMaxedOut = PREFIX + _languageFile.getString("messages.upgrades.maxed-out");
        messagesUpgradeNotEnoughPs = PREFIX + _languageFile.getString("messages.upgrades.not-enough-ps");

        messagesMembersUpdated = PREFIX + _languageFile.getString("messages.member.updated-player");
    }
}
