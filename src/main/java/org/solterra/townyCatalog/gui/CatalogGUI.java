package org.solterra.townyCatalog.gui;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.solterra.townyCatalog.api.TownyCatalogAPI;
import org.solterra.townyCatalog.util.GUIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Catalog GUI display and interaction
 */
public class CatalogGUI {

    /**
     * Opens the catalog GUI for a player showing plots from a specific town
     *
     * @param player The player to show the catalog to
     * @param town   The town to show plots from
     */
    public static void openCatalog(Player player, Town town) {
        // Get purchasable plots for the player from this town
        List<TownBlock> plots = TownyCatalogAPI.getAllPurchasablePlotsIn(town, player);

        if (plots.isEmpty()) {
            player.sendMessage(Component.text("No plots available for purchase in this town!", NamedTextColor.YELLOW));
            return;
        }

        // Sort plots by price (lowest to highest)
        plots = TownyCatalogAPI.sortPlotsByPrice(plots, true);

        // Create holder first
        CatalogInventoryHolder holder = new CatalogInventoryHolder(player, plots, town);

        // Create inventory with custom holder
        Inventory inventory = Bukkit.createInventory(
                holder,
                GUISlots.INVENTORY_SIZE,
                Component.text(town.getName() + " - Plots", NamedTextColor.DARK_GREEN, TextDecoration.BOLD)
        );

        // Link inventory to holder
        holder.setInventory(inventory);

        // Populate the first page
        populatePage(holder, 0);

        // Open the inventory
        player.openInventory(inventory);
    }

    /**
     * Populates a specific page of the catalog
     *
     * @param holder The inventory holder
     * @param page   The page number (0-indexed)
     */
    public static void populatePage(CatalogInventoryHolder holder, int page) {
        Inventory inventory = holder.getInventory();
        List<TownBlock> allPlots = holder.getAllPlots();

        // Clear the inventory
        inventory.clear();

        // Calculate start and end indices
        int startIndex = page * GUISlots.ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + GUISlots.ITEMS_PER_PAGE, allPlots.size());

        // Add plot items (fill slots 0-44)
        for (int i = startIndex; i < endIndex; i++) {
            TownBlock plot = allPlots.get(i);
            ItemStack plotItem = createPlotItem(plot, holder.getPlayer());
            inventory.setItem(i - startIndex, plotItem);
        }

        // Add bottom row items
        inventory.setItem(GUISlots.CATALOG_BACK_BUTTON, createBackButton());
        inventory.setItem(GUISlots.CATALOG_TOWN_INFO, createTownInfoItem(holder.getSelectedTown()));
        inventory.setItem(GUISlots.CATALOG_TAX_INFO, createTaxInfoItem(holder.getSelectedTown()));
        inventory.setItem(GUISlots.CATALOG_MAYOR_HEAD, createMayorHead(holder.getSelectedTown()));
        // Add navigation items
        if (holder.hasPreviousPage()) {
            inventory.setItem(GUISlots.CATALOG_PREVIOUS_PAGE, GUIUtils.createNavigationItem(
                    Material.ARROW,
                    "Previous Page",
                    "Click to go to page " + page
            ));
        }

        // Add info item
        inventory.setItem(GUISlots.CATALOG_INFO, createInfoItem(page + 1, holder.getTotalPages(), allPlots.size()));

        if (holder.hasNextPage()) {
            inventory.setItem(GUISlots.CATALOG_NEXT_PAGE, GUIUtils.createNavigationItem(
                    Material.ARROW,
                    "Next Page",
                    "Click to go to page " + (page + 2)
            ));
        }

        holder.setCurrentPage(page);
    }

    /**
     * Creates an ItemStack representing a plot
     *
     * @param plot   The plot block
     * @param player The player viewing the catalog
     * @return ItemStack with plot details
     */
    private static ItemStack createPlotItem(TownBlock plot, Player player) {
        ItemStack item = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = item.getItemMeta();

        String townName = plot.getTownOrNull() != null ? plot.getTownOrNull().getName() : "Unknown";
        String plotName = GUIUtils.getPlotDisplayName(plot, townName);

        // Set display name
        meta.displayName(GUIUtils.noItalic(plotName, NamedTextColor.GREEN));

        // Check if player can afford the plot
        boolean canAfford = TownyCatalogAPI.canAffordPlot(plot.getPlotPrice(), player);

        // Create lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Price: ", NamedTextColor.GRAY)
                .append(GUIUtils.formatPrice(plot.getPlotPrice(), canAfford))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Type: ", NamedTextColor.GRAY)
                .append(Component.text(plot.getType().toString(), NamedTextColor.YELLOW))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Location: ", NamedTextColor.GRAY)
                .append(GUIUtils.formatCoordinates(plot.getX(), plot.getZ()))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Click to view location", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Creates the info item showing current page and plot count
     *
     * @param currentPage Current page number (1-indexed for display)
     * @param totalPages  Total number of pages
     * @param totalPlots  Total number of plots
     * @return ItemStack info item
     */
    private static ItemStack createInfoItem(int currentPage, int totalPages, int totalPlots) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Catalog Info", NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Page: ", NamedTextColor.GRAY)
                .append(Component.text(currentPage + "/" + totalPages, NamedTextColor.WHITE))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Total Plots: ", NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(totalPlots), NamedTextColor.WHITE))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Showing affordable plots only", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Creates a player head item for the town's mayor
     *
     * @param town The town whose mayor to display
     * @return ItemStack with mayor's head
     */
    private static ItemStack createMayorHead(Town town) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        String mayorName = town.hasMayor() ? town.getMayor().getName() : "No Mayor";

        meta.displayName(Component.text("Mayor: " + mayorName, NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Town: ", NamedTextColor.GRAY)
                .append(Component.text(town.getName(), NamedTextColor.YELLOW))
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);

        // Set the skull owner if mayor exists
        if (town.hasMayor()) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(town.getMayor().getUUID()));
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a back button to return to town selection
     *
     * @return ItemStack back button
     */
    private static ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Back to Town Selection", NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Click to return", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a town info item showing residents and nation
     *
     * @param town The town to display info for
     * @return ItemStack with town information
     */
    private static ItemStack createTownInfoItem(Town town) {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(town.getName(), NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());

        // Resident count
        int residentCount = town.getResidents().size();
        lore.add(Component.text("Residents: ", NamedTextColor.GRAY)
                .append(Component.text(residentCount, NamedTextColor.WHITE))
                .decoration(TextDecoration.ITALIC, false));

        // Nation affiliation
        if (town.hasNation() && town.getNationOrNull() != null) {
            lore.add(Component.text("Nation: ", NamedTextColor.GRAY)
                    .append(Component.text(town.getNationOrNull().getName(), NamedTextColor.GOLD))
                    .decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("Nation: ", NamedTextColor.GRAY)
                    .append(Component.text("None", NamedTextColor.DARK_GRAY))
                    .decoration(TextDecoration.ITALIC, false));
        }

        // Town status
        if (town.isOpen()) {
            lore.add(Component.text("Status: ", NamedTextColor.GRAY)
                    .append(Component.text("Open", NamedTextColor.GREEN))
                    .decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("Status: ", NamedTextColor.GRAY)
                    .append(Component.text("Invite Only", NamedTextColor.YELLOW))
                    .decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a tax info item showing town taxes
     *
     * @param town The town to display tax info for
     * @return ItemStack with tax information
     */
    private static ItemStack createTaxInfoItem(Town town) {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Town Taxes", NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());

        // Plot tax
        double plotTax = town.getTaxes();
        lore.add(Component.text("Plot Tax: ", NamedTextColor.GRAY)
                .append(Component.text("$" + String.format("%.2f", plotTax), NamedTextColor.YELLOW))
                .decoration(TextDecoration.ITALIC, false));

        // Tax percentage (if applicable)
        if (town.isTaxPercentage()) {
            lore.add(Component.text("Type: ", NamedTextColor.GRAY)
                    .append(Component.text("Percentage", NamedTextColor.AQUA))
                    .decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("Type: ", NamedTextColor.GRAY)
                    .append(Component.text("Flat Rate", NamedTextColor.AQUA))
                    .decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.empty());
        lore.add(Component.text("Taxes are paid daily", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Gets the plot from a clicked slot
     *
     * @param holder The inventory holder
     * @param slot   The clicked slot
     * @return The TownBlock at that slot, or null
     */
    public static TownBlock getPlotFromSlot(CatalogInventoryHolder holder, int slot) {
        // Skip special slots in bottom row
        if (slot == GUISlots.CATALOG_BACK_BUTTON || slot == GUISlots.CATALOG_MAYOR_HEAD ||
            slot == GUISlots.CATALOG_PREVIOUS_PAGE || slot == GUISlots.CATALOG_TOWN_INFO ||
            slot == GUISlots.CATALOG_INFO || slot == GUISlots.CATALOG_TAX_INFO || slot == GUISlots.CATALOG_NEXT_PAGE) {
            return null;
        }

        // Only slots 0-44 are valid for plots
        if (slot < 0 || slot >= GUISlots.ITEMS_PER_PAGE) {
            return null;
        }

        int plotIndex = (holder.getCurrentPage() * GUISlots.ITEMS_PER_PAGE) + slot;
        List<TownBlock> plots = holder.getAllPlots();

        if (plotIndex >= plots.size()) {
            return null;
        }

        return plots.get(plotIndex);
    }
}
