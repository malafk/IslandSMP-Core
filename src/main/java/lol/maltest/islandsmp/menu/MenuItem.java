package lol.maltest.islandsmp.menu;

import dev.dejvokep.boostedyaml.YamlDocument;
import lol.maltest.islandsmp.utils.HexUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MenuItem {
    private String name;
    private Material material;
    private List<String> lore;
    private int slot;

    public MenuItem(YamlDocument yamlDocument, String keyPrefix) {
        this.name = yamlDocument.getString(keyPrefix + ".name");
        this.material = Material.valueOf(yamlDocument.getString(keyPrefix + ".material"));
        this.lore = yamlDocument.getStringList(keyPrefix + ".lore");
        this.slot = yamlDocument.getInt(keyPrefix + ".slot");
    }

    public List<Component> getColorLore() {
            return getLore().stream()
                    .map(HexUtils::colour)
                    .collect(Collectors.toCollection(ArrayList::new));
    }
}