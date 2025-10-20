package org.solterra.townyCatalog.listener;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.solterra.townyCatalog.api.TownyCatalogAPI;
import org.solterra.townyCatalog.gui.*;

/**
 * Listens for inventory click events in the Catalog GUI
 * Uses centralized slot constants and action handlers for cleaner code
 */
public class CatalogListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        InventoryHolder holder = topInventory.getHolder();

        // Get the player
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Only handle clicks in our custom inventories
        if (event.getClickedInventory() != topInventory) {
            return;
        }

        // Handle Town Selection GUI
        if (holder instanceof TownSelectionHolder townHolder) {
            event.setCancelled(true);
            handleTownSelectionClick(player, townHolder, event.getSlot());
            return;
        }

        // Handle Catalog (Plot Display) GUI
        if (holder instanceof CatalogInventoryHolder catalogHolder) {
            event.setCancelled(true);
            handleCatalogClick(player, catalogHolder, event.getSlot());
        }
    }

    /**
     * Handles clicks in the town selection inventory
     */
    private void handleTownSelectionClick(Player player, TownSelectionHolder holder, int slot) {
        // Handle previous page navigation
        if (slot == GUISlots.TOWN_SELECTION_PREVIOUS_PAGE && holder.hasPreviousPage()) {
            TownSelectionGUI.populatePage(holder, holder.getCurrentPage() - 1);
            playClickSound(player);
            return;
        }

        // Handle next page navigation
        if (slot == GUISlots.TOWN_SELECTION_NEXT_PAGE && holder.hasNextPage()) {
            TownSelectionGUI.populatePage(holder, holder.getCurrentPage() + 1);
            playClickSound(player);
            return;
        }

        // Handle info slot (no action)
        if (slot == GUISlots.TOWN_SELECTION_INFO) {
            return;
        }

        // Handle town selection
        Town town = TownSelectionGUI.getTownFromSlot(holder, slot);
        if (town != null) {
            playClickSound(player);
            CatalogGUI.openCatalog(player, town);
        }
    }

    /**
     * Handles clicks in the catalog (plot display) inventory
     */
    private void handleCatalogClick(Player player, CatalogInventoryHolder holder, int slot) {
        // Handle back button
        if (slot == GUISlots.CATALOG_BACK_BUTTON) {
            playClickSound(player);
            TownSelectionGUI.openTownSelection(player);
            return;
        }

        // Handle previous page navigation
        if (slot == GUISlots.CATALOG_PREVIOUS_PAGE && holder.hasPreviousPage()) {
            CatalogGUI.populatePage(holder, holder.getCurrentPage() - 1);
            playClickSound(player);
            return;
        }

        // Handle next page navigation
        if (slot == GUISlots.CATALOG_NEXT_PAGE && holder.hasNextPage()) {
            CatalogGUI.populatePage(holder, holder.getCurrentPage() + 1);
            playClickSound(player);
            return;
        }

        // Handle info slots (no action) - consolidated check
        if (slot == GUISlots.CATALOG_TOWN_INFO ||
            slot == GUISlots.CATALOG_INFO ||
            slot == GUISlots.CATALOG_TAX_INFO ||
            slot == GUISlots.CATALOG_MAYOR_HEAD) {
            return;
        }

        // Handle plot clicks
        TownBlock plot = CatalogGUI.getPlotFromSlot(holder, slot);
        if (plot != null) {
            handlePlotClick(player, plot);
        }
    }

    /**
     * Handles when a player clicks on a plot item
     */
    private void handlePlotClick(Player player, TownBlock plot) {
        Location plotLocation = TownyCatalogAPI.getPlotCenterLocation(plot);

        if (plotLocation == null) {
            player.sendMessage(Component.text("Unable to get plot location!", NamedTextColor.RED));
            return;
        }

        String townName = plot.getTownOrNull() != null ? plot.getTownOrNull().getName() : "Unknown";

        // Teleport the player to the plot
        player.teleport(plotLocation);
        player.playSound(player.getLocation(), "entity.enderman.teleport", 1.0f, 1.0f);

        // Send confirmation message
        player.sendMessage(Component.text("Teleported to plot in ", NamedTextColor.GREEN)
                .append(Component.text(townName, NamedTextColor.GOLD))
                .append(Component.text("!", NamedTextColor.GREEN)));

        player.sendMessage(Component.text("Price: ", NamedTextColor.GRAY)
                .append(Component.text("$" + String.format("%.2f", plot.getPlotPrice()), NamedTextColor.GOLD)));

        player.sendMessage(Component.text("Use ", NamedTextColor.GRAY)
                .append(Component.text("/plot claim", NamedTextColor.YELLOW))
                .append(Component.text(" to purchase this plot", NamedTextColor.GRAY)));

        // Close the inventory
        player.closeInventory();
    }

    /**
     * Plays a click sound for the player
     */
    private void playClickSound(Player player) {
        player.playSound(player.getLocation(), "ui.button.click", 1.0f, 1.0f);
    }
}
