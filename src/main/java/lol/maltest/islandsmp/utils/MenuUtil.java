package lol.maltest.islandsmp.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lol.maltest.islandsmp.menu.MenuItem;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MenuUtil {
    private YamlDocument _menuFile;

    public static MenuItem menuMainMembers;
    public static MenuItem menuMainTrusted;
    public static MenuItem menuMainWarps;
    public static MenuItem menuMainSettings;
    public static MenuItem menuMainUpgrades;
    public static MenuItem menuMainHome;
    public static MenuItem menuMainPermissions;

    public MenuUtil(JavaPlugin plugin) {
        try {
            _menuFile = YamlDocument.create(new File(plugin.getDataFolder(), "menu.yml"), getClass().getResourceAsStream("/menu.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        menuMainMembers = new MenuItem(_menuFile, "main.buttons.members");
        menuMainTrusted = new MenuItem(_menuFile, "main.buttons.trusted");
        menuMainWarps = new MenuItem(_menuFile, "main.buttons.warps");
        menuMainSettings = new MenuItem(_menuFile, "main.buttons.settings");
        menuMainUpgrades = new MenuItem(_menuFile, "main.buttons.upgrades");
        menuMainHome = new MenuItem(_menuFile, "main.buttons.home");
        menuMainPermissions = new MenuItem(_menuFile, "main.buttons.permissions");
    }
}
