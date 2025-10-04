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
import org.solterra.townyCatalog.gui.CatalogGUI;
import org.solterra.townyCatalog.gui.CatalogInventoryHolder;
import org.solterra.townyCatalog.gui.TownSelectionGUI;
import org.solterra.townyCatalog.gui.TownSelectionHolder;

/**
 * Listens for inventory click events in the Catalog GUI
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
     *
     * @param player     The player who clicked
     * @param holder     The town selection holder
     * @param slot       The clicked slot
     */
    private void handleTownSelectionClick(Player player, TownSelectionHolder holder, int slot) {
        // Handle navigation clicks
        if (slot == 45 && holder.hasPreviousPage()) {
            TownSelectionGUI.populatePage(holder, holder.getCurrentPage() - 1);
            player.playSound(player.getLocation(), "ui.button.click", 1.0f, 1.0f);
            return;
        }

        if (slot == 53 && holder.hasNextPage()) {
            TownSelectionGUI.populatePage(holder, holder.getCurrentPage() + 1);
            player.playSound(player.getLocation(), "ui.button.click", 1.0f, 1.0f);
            return;
        }

        if (slot == 49) {
            // Info slot - do nothing
            return;
        }

        // Handle town clicks
        Town town = TownSelectionGUI.getTownFromSlot(holder, slot);
        if (town != null) {
            player.playSound(player.getLocation(), "ui.button.click", 1.0f, 1.0f);
            CatalogGUI.openCatalog(player, town);
        }
    }

    /**
     * Handles clicks in the catalog (plot display) inventory
     *
     * @param player     The player who clicked
     * @param holder     The catalog holder
     * @param slot       The clicked slot
     */
    private void handleCatalogClick(Player player, CatalogInventoryHolder holder, int slot) {
        // Handle back button (slot 45)
        if (slot == 45) {
            player.playSound(player.getLocation(), "ui.button.click", 1.0f, 1.0f);
            TownSelectionGUI.openTownSelection(player);
            return;
        }

        // Handle mayor head (slot 46) - decorative only
        if (slot == 46) {
            return;
        }

        // Handle previous page navigation (slot 47)
        if (slot == 47 && holder.hasPreviousPage()) {
            CatalogGUI.populatePage(holder, holder.getCurrentPage() - 1);
            player.playSound(player.getLocation(), "ui.button.click", 1.0f, 1.0f);
            return;
        }

        // Handle town info (slot 48) - informational only
        if (slot == 48) {
            return;
        }

        // Handle catalog info (slot 49) - informational only
        if (slot == 49) {
            return;
        }

        // Handle tax info (slot 50) - informational only
        if (slot == 50) {
            return;
        }

        // Handle next page navigation (slot 51)
        if (slot == 51 && holder.hasNextPage()) {
            CatalogGUI.populatePage(holder, holder.getCurrentPage() + 1);
            player.playSound(player.getLocation(), "ui.button.click", 1.0f, 1.0f);
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
     *
     * @param player The player who clicked
     * @param plot   The plot that was clicked
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
}
