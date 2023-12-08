package lol.maltest.islandsmp.menu;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lol.maltest.islandsmp.utils.HexUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public interface Menu {
    void open(Player player);
    List<MenuItem> getMenuItems();

    default void setItems(Gui gui, Player player) {
        gui.getFiller().fill(ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).asGuiItem(e -> e.setCancelled(true)));

        for(MenuItem item : getMenuItems()) {

            ItemBuilder itemBuilder = ItemBuilder.from(item.getMaterial());

            if(item.getMaterial().equals(Material.PLAYER_HEAD)) {
                itemBuilder = itemBuilder.setSkullOwner(player);
            }

            itemBuilder.name(HexUtils.colour(item.getName()));

            itemBuilder.lore(item.getColorLore());

            GuiItem guiItem = itemBuilder.asGuiItem(event -> {
                event.setCancelled(true);
                player.sendMessage("click");
            });

            gui.setItem(item.getSlot(), guiItem);

        }
    }
}