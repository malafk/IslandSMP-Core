package lol.maltest.islandsmp.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lol.maltest.islandsmp.IslandSMP;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LanguageUtil {

    private YamlDocument _languageFile;

    public static String PREFIX;

    public static String publicNeedIsland;

    public static String errorMembersCantModifyOwner;

    public static String errorWarpsMax;

    public static String messageIslandCreated;
    public static String messageWarpCreated;

    public LanguageUtil(JavaPlugin plugin) {
        try {
            _languageFile = YamlDocument.create(new File(plugin.getDataFolder(), "language.yml"), getClass().getResourceAsStream("/language.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PREFIX = _languageFile.getString("prefix");

        publicNeedIsland = PREFIX + _languageFile.getString("errors.public.need-island");

        errorMembersCantModifyOwner = PREFIX + _languageFile.getString("errors.members.cant-modify-owner");

        errorWarpsMax = PREFIX + _languageFile.getString("errors.warps.max-warps");

        messageIslandCreated = PREFIX + _languageFile.getString("messages.island.created");
        messageWarpCreated = PREFIX + _languageFile.getString("messages.warps.created");
    }
}
