package lol.maltest.islandsmp.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public interface Menuable {
    Material getMaterial();
    String getName();
    List<Component> getColorLore();
    String getKeyPrefix();
    int getSlot();
}