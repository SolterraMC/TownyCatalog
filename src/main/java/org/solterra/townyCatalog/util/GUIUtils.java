package org.solterra.townyCatalog.util;

import com.palmergames.bukkit.towny.object.TownBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for GUI creation and text formatting
 */
public class GUIUtils {

    /**
     * Creates a navigation button item
     *
     * @param material    The material for the button
     * @param displayName The display name
     * @param loreText    The lore text
     * @return ItemStack navigation button
     */
    public static ItemStack createNavigationItem(Material material, String displayName, String loreText) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(displayName, NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(loreText, NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Formats a price as a colored component
     *
     * @param price     The price to format
     * @param canAfford Whether the player can afford it (affects color)
     * @return Formatted price component
     */
    public static Component formatPrice(double price, boolean canAfford) {
        NamedTextColor color = canAfford ? NamedTextColor.GOLD : NamedTextColor.RED;
        return Component.text("$" + String.format("%.2f", price), color);
    }

    /**
     * Formats coordinates as a component
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return Formatted coordinates component
     */
    public static Component formatCoordinates(int x, int z) {
        return Component.text(String.format("X: %d, Z: %d", x, z), NamedTextColor.AQUA);
    }

    /**
     * Gets the display name for a plot
     * Returns custom plot name if available, otherwise "TownName Plot"
     *
     * @param plot     The plot block
     * @param townName The town name
     * @return Plot display name
     */
    public static String getPlotDisplayName(TownBlock plot, String townName) {
        if (Config.SHOW_CUSTOM_PLOT_NAMES && plot.getName() != null && !plot.getName().isEmpty()) {
            return plot.getName();
        }
        return townName + " Plot";
    }

    /**
     * Creates a Component with no italic decoration (common pattern in GUIs)
     *
     * @param text  The text
     * @param color The color
     * @return Component with italic disabled
     */
    public static Component noItalic(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }
}
