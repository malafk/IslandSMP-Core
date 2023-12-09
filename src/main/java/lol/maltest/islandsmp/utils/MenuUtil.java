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
    public static YamlDocument _menuFile;

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

    public static Component menuTrustedTitle;
    public static String menuTrustedButtonName;
    public static List<String> menuTrustedButtonRawLore;


    public static Component menuPermissionsTitle;
    public static int menuPermissionsRows;
    public static ArrayList<Menuable> menuPermissionsButtons = new ArrayList<>();

    public static Component menuRankPermissionsTitle;
    public static int menuRankPermissionsRows;
    public static ArrayList<Menuable> menuRankPermissionsButtons = new ArrayList<>();

    public static Component menuSettingsTitle;
    public static int menuSettingsRows;
    public static ArrayList<Menuable> menuSettingsButtons = new ArrayList<>();

    public static Component menuUpgradesTitle;
    public static int menuUpgradesRows;
    public static ArrayList<Menuable> menuUpgradesButtons = new ArrayList<>();

    public static Component menuWarpsTitle;
    public static String menuWarpsButtonName;
    public static List<String> menuWarpsButtonRawLore;


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

        menuPermissionsTitle = HexUtils.colour(_menuFile.getString("permissions.title"));
        menuPermissionsRows = _menuFile.getInt("permissions.rows");

        menuRankPermissionsTitle = HexUtils.colour(_menuFile.getString("rankpermissions.title"));
        menuRankPermissionsRows = _menuFile.getInt("rankpermissions.rows");

        menuSettingsTitle = HexUtils.colour(_menuFile.getString("settings.title"));
        menuSettingsRows = _menuFile.getInt("settings.rows");

        menuWarpsTitle = HexUtils.colour(_menuFile.getString("warps.title"));
        menuWarpsButtonName = _menuFile.getString("warps.buttons.warp.name");
        menuWarpsButtonRawLore = _menuFile.getStringList("warps.buttons.warp.lore");

        menuMembersTitle = HexUtils.colour(_menuFile.getString("members.title"));
        menuMembersButtonName = _menuFile.getString("members.buttons.member.name");
        menuMembersButtonRawLore = _menuFile.getStringList("members.buttons.member.lore");


        menuTrustedTitle = HexUtils.colour(_menuFile.getString("trusted.title"));
        menuTrustedButtonName = _menuFile.getString("trusted.buttons.member.name");
        menuTrustedButtonRawLore = _menuFile.getStringList("trusted.buttons.member.lore");

        menuUpgradesTitle = HexUtils.colour(_menuFile.getString("upgrades.title"));
        menuUpgradesRows = _menuFile.getInt("upgrades.rows");

        // spin button
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.spin"));

        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.ore_drops"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.farm_drops"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.mob_drops"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.mob_spawns"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.xp_drops"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.keep_inventory"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.keep_xp"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.speed"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.haste"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.night_vision"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.nether_access"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.warp_slots"));
        menuUpgradesButtons.add(new MenuItem(_menuFile, "upgrades.buttons.trusted_slots"));

        menuSettingsButtons.add(new MenuItem(_menuFile, "settings.buttons.visit"));
        menuSettingsButtons.add(new MenuItem(_menuFile, "settings.buttons.entity_interact"));
        menuSettingsButtons.add(new MenuItem(_menuFile, "settings.buttons.phantoms"));
        menuSettingsButtons.add(new MenuItem(_menuFile, "settings.buttons.mob_spawning"));
        menuSettingsButtons.add(new MenuItem(_menuFile, "settings.buttons.animal_spawning"));
        menuSettingsButtons.add(new MenuItem(_menuFile, "settings.buttons.explosions"));


        menuPermissionsButtons.add(new MenuItem(_menuFile, "permissions.buttons.trusted"));
        menuPermissionsButtons.add(new MenuItem(_menuFile, "permissions.buttons.member"));
        menuPermissionsButtons.add(new MenuItem(_menuFile, "permissions.buttons.moderator"));
        menuPermissionsButtons.add(new MenuItem(_menuFile, "permissions.buttons.administrator"));

        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.place"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.break"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.container"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.invite"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.kick"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.promote"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.demote"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.settings"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.upgrade"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.permissions"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.sethome"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.home"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.setwarp"));
        menuRankPermissionsButtons.add(new MenuItem(_menuFile, "rankpermissions.buttons.warp"));

        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.members"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.trusted"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.warps"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.settings"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.upgrades"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.home"));
        menuMainButtons.add(new MenuItem(_menuFile, "main.buttons.permissions"));
    }
}
