package lol.maltest.islandsmp.menu;

import dev.dejvokep.boostedyaml.YamlDocument;
import lol.maltest.islandsmp.utils.HexUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MenuItem implements Menuable {
    private String keyPrefix;

    private String name;
    private Material material;
    private List<String> lore;
    private int slot;

    public MenuItem(YamlDocument yamlDocument, String keyPrefix) {
        this.keyPrefix = keyPrefix;
        this.name = yamlDocument.getString(keyPrefix + ".name");
        this.material = Material.valueOf(yamlDocument.getString(keyPrefix + ".material"));
        this.lore = yamlDocument.getStringList(keyPrefix + ".lore");
        this.slot = yamlDocument.getInt(keyPrefix + ".slot");
    }

    // Assuming you want to represent coloured lore as a simple string list for Menuable
    public ArrayList<Component> getColorLore() {
        return getLore().stream()
                .map(HexUtils::colour)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public OfflinePlayer getSkullOwner() {
        return null;
    }
    // The getters are automatically fulfilled by the use of Lombok's @Getter annotation at the class level
}