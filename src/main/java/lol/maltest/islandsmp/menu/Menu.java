package lol.maltest.islandsmp.menu;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lol.maltest.islandsmp.utils.HexUtils;
import lol.maltest.islandsmp.utils.MenuUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public abstract class Menu {
    public abstract void open(Player player);

    public abstract void onClick(Player player, String key, ClickType clickType);

    public abstract int backButtonSlot();

    public abstract List<Menuable> getMenuItems(Player player);

    private Class<? extends Menu> previousMenu;

    public Class<? extends Menu> getPreviousMenu() {
        return previousMenu;
    }

    public void setPreviousMenu(Class<? extends Menu> previousMenu) {
        this.previousMenu = previousMenu;
    }

    public void openPreviousMenu(Player player) {
        if (previousMenu != null) {
            try {
                Menu menu = previousMenu.getDeclaredConstructor().newInstance();
                menu.open(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void openGui(Gui gui, Player player) {
        populateGui(gui, player);
        gui.open(player);
    }

    public void openPaginatedGui(PaginatedGui paginatedGui, Player player) {
        populatePaginatedGui(paginatedGui, player); // Assuming PaginatedGui has similar methods to Gui
        paginatedGui.open(player);
    }

    private GuiItem createGuiItem(Menuable item, Player player) {
        ItemBuilder itemBuilder = ItemBuilder.from(item.getMaterial());

        if (item.getMaterial().equals(Material.PLAYER_HEAD)) {
            itemBuilder = itemBuilder.setSkullOwner(item.getSkullOwner());
        }

        itemBuilder.name(HexUtils.colour(item.getName()));
        itemBuilder.lore(item.getColorLore());

        return itemBuilder.asGuiItem(event -> {
            event.setCancelled(true);
            onClick(player, item.getKeyPrefix(), event.getClick());
        });
    }

    public void populateGui(Gui gui, Player player) {
        for (Menuable item : getMenuItems(player)) {
            GuiItem guiItem = createGuiItem(item, player);
            gui.setItem(item.getSlot(), guiItem);
        }

        if(getPreviousMenu() != null || backButtonSlot() != -1) {
            gui.setItem(backButtonSlot(), ItemBuilder.from(Material.PAPER).model(5).name(MenuUtil.menuPageBackMenuName).lore(MenuUtil.menuPageBackMenuLore)
                    .asGuiItem(event -> {
                                event.setCancelled(true);
                                openPreviousMenu(player);
                            }
                    ));
        }

        gui.getFiller().fill(getFiller());
    }

    public void populatePaginatedGui(PaginatedGui gui, Player player) {

        for (Menuable item : getMenuItems(player)) {
            GuiItem guiItem = createGuiItem(item, player);
            gui.addItem(guiItem);
        }

        gui.getFiller().fillBorder(getFiller());

        // Previous item
        gui.setItem(3, 4, ItemBuilder.from(Material.PAPER).model(2).name(MenuUtil.menuPagePreviousName).lore(MenuUtil.menuPagePreviousLore)
                .asGuiItem(event -> {
                            event.setCancelled(true);
                            gui.previous();
                        }
                ));

        gui.setItem(3, 5, ItemBuilder.from(Material.PAPER).model(5).name(MenuUtil.menuPageBackMenuName).lore(MenuUtil.menuPageBackMenuLore)
                .asGuiItem(event -> {
                            event.setCancelled(true);
                            openPreviousMenu(player);
                        }
                ));

        gui.setItem(3, 6, ItemBuilder.from(Material.PAPER).model(3).name(MenuUtil.menuPageForwardName).lore(MenuUtil.menuPageForwardLore)
                .asGuiItem(event -> {
                    event.setCancelled(true);
                    gui.next();
                }));
    }

    public GuiItem getFiller() {
        return ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(HexUtils.colour("&7")).asGuiItem(e -> e.setCancelled(true));
    }
}