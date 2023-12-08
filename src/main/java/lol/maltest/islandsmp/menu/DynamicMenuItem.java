package lol.maltest.islandsmp.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class DynamicMenuItem implements Menuable {
    private Material material;
    private String name;
    private List<Component> lore;
    private String keyPrefix;
    private int slot;
    private OfflinePlayer skullOwner;  // To retain the skull owner attribute

    public DynamicMenuItem(Material material, String name, List<Component> lore, String keyPrefix, int slot, OfflinePlayer skullOwner) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.keyPrefix = keyPrefix;
        this.slot = slot;
        this.skullOwner = skullOwner;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Component> getColorLore() {
        return lore;
    }

    @Override
    public String getKeyPrefix() {
        return keyPrefix;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public OfflinePlayer getSkullOwner() {
        return skullOwner;
    }

}