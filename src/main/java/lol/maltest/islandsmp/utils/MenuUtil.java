package lol.maltest.islandsmp.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lol.maltest.islandsmp.menu.MenuItem;
import lol.maltest.islandsmp.menu.Menuable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuUtil {
    private YamlDocument _menuFile;

    public static Component menuPageForwardName;
    public static List<Component> menuPageForwardLore;

    public static Component menuPagePreviousName;
    public static List<Component> menuPagePreviousLore;

    public static Component menuPageBackMenuName;
    public static List<Component> menuPageBackMenuLore;


    public static Component menuMainTitle;
    public static int menuMainRows;
    public static ArrayList<Menuable> menuMainButtons = new ArrayList<>();

    public static Component menuMembersTitle;
    public static String menuMembersButtonName;
    public static List<String> menuMembersButtonRawLore;


    public MenuUtil(JavaPlugin plugin) {
        try {
            _menuFile = YamlDocument.create(new File(plugin.getDataFolder(), "menu.yml"), getClass().getResourceAsStream("/menu.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        menuPageBackMenuName = HexUtils.colour(_menuFile.getString("pages.buttons.backmenu.name"));
        menuPageBackMenuLore = HexUtils.colorListComponent(_menuFile.getStringList("pages.buttons.backmenu.lore"));

        menuPageForwardName = HexUtils.colour(_menuFile.getString("pages.buttons.forward.name"));
        menuPageForwardLore = HexUtils.colorListComponent(_menuFile.getStringList("pages.buttons.forward.lore"));

        menuPagePreviousName = HexUtils.colour(_menuFile.getString("pages.buttons.back.name"));
        menuPagePreviousLore = HexUtils.colorListComponent(_menuFile.getStringList("pages.buttons.back.lore"));

        menuMainTitle = HexUtils.colour(_menuFile.getString("main.title"));
        menuMainRows = _menuFile.getInt("main.rows");

        menuMembersTitle = HexUtils.colour(_menuFile.getString("members.title"));
        menuMembersButtonName = _menuFile.getString("members.buttons.member.name");
        menuMembersButtonRawLore = _menuFile.getStringList("members.buttons.member.lore");

        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.members"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.trusted"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.warps"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.settings"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.upgrades"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.home"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.permissions"));
    }
}
