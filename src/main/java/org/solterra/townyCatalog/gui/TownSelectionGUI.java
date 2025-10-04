package org.solterra.townyCatalog.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
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
import org.solterra.townyCatalog.api.TownyCatalogAPI;
import org.solterra.townyCatalog.util.GUIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Town Selection GUI display
 */
public class TownSelectionGUI {

    private static final int INVENTORY_SIZE = 54;
    private static final int TOWNS_PER_PAGE = 45;
    private static final int PREVIOUS_PAGE_SLOT = 48;
    private static final int INFO_SLOT = 49;
    private static final int NEXT_PAGE_SLOT = 50;

    /**
     * Opens the town selection GUI for a player
     *
     * @param player The player to show the town selection to
     */
    public static void openTownSelection(Player player) {
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) {
            player.sendMessage(Component.text("You must be a Towny resident to use the catalog!", NamedTextColor.RED));
            return;
        }

        // Get all towns with purchasable plots
        List<Town> towns = TownyCatalogAPI.getTownsWithPurchasablePlots(resident);

        if (towns.isEmpty()) {
            player.sendMessage(Component.text("No towns have plots available for purchase!", NamedTextColor.YELLOW));
            return;
        }

        // Sort towns alphabetically
        towns.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        // Create holder first
        TownSelectionHolder holder = new TownSelectionHolder(player, towns);

        // Create inventory with custom holder
        Inventory inventory = Bukkit.createInventory(
                holder,
                INVENTORY_SIZE,
                Component.text("Select a Town", NamedTextColor.DARK_GREEN, TextDecoration.BOLD)
        );

        // Link inventory to holder
        holder.setInventory(inventory);

        // Populate the first page
        populatePage(holder, 0);

        // Open the inventory
        player.openInventory(inventory);
    }

    /**
     * Populates a specific page of the town selection
     *
     * @param holder The inventory holder
     * @param page   The page number (0-indexed)
     */
    public static void populatePage(TownSelectionHolder holder, int page) {
        Inventory inventory = holder.getInventory();
        List<Town> allTowns = holder.getAllTowns();

        // Clear the inventory
        inventory.clear();

        // Calculate start and end indices
        int startIndex = page * TOWNS_PER_PAGE;
        int endIndex = Math.min(startIndex + TOWNS_PER_PAGE, allTowns.size());

        // Get player to check affordability
        Player player = Bukkit.getPlayer(holder.getPlayerUUID());
        if (player == null) return;

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        // Add town items
        for (int i = startIndex; i < endIndex; i++) {
            Town town = allTowns.get(i);
            ItemStack townItem = createTownItem(town, resident);
            inventory.setItem(i - startIndex, townItem);
        }

        // Add navigation items
        if (holder.hasPreviousPage()) {
            inventory.setItem(PREVIOUS_PAGE_SLOT, GUIUtils.createNavigationItem(
                    Material.ARROW,
                    "Previous Page",
                    "Click to go to page " + page
            ));
        }

        // Add info item
        inventory.setItem(INFO_SLOT, createInfoItem(page + 1, holder.getTotalPages(), allTowns.size()));

        if (holder.hasNextPage()) {
            inventory.setItem(NEXT_PAGE_SLOT, GUIUtils.createNavigationItem(
                    Material.ARROW,
                    "Next Page",
                    "Click to go to page " + (page + 2)
            ));
        }

        holder.setCurrentPage(page);
    }

    /**
     * Creates an ItemStack representing a town
     *
     * @param town     The town
     * @param resident The resident viewing the catalog
     * @return ItemStack with town details
     */
    private static ItemStack createTownItem(Town town, Resident resident) {
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();

        // Set display name

        meta.displayName(Component.text(town.getName(), NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));

        // Get plot information
        List<TownBlock> plots = TownyCatalogAPI.getAllPurchasablePlotsIn(town, resident);
        int plotCount = plots.size();

        // Calculate price range
        double minPrice = plots.stream()
                .mapToDouble(TownBlock::getPlotPrice)
                .min()
                .orElse(0.0);
        double maxPrice = plots.stream()
                .mapToDouble(TownBlock::getPlotPrice)
                .max()
                .orElse(0.0);

        // Create lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Plots Available: ", NamedTextColor.GRAY)
                .append(Component.text(plotCount, NamedTextColor.GREEN))
                .decoration(TextDecoration.ITALIC, false));

        if (minPrice == maxPrice) {
            lore.add(Component.text("Price: ", NamedTextColor.GRAY)
                    .append(Component.text("$" + String.format("%.2f", minPrice), NamedTextColor.GOLD))
                    .decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("Price Range: ", NamedTextColor.GRAY)
                    .append(Component.text("$" + String.format("%.2f", minPrice), NamedTextColor.GOLD))
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(Component.text("$" + String.format("%.2f", maxPrice), NamedTextColor.GOLD))
                    .decoration(TextDecoration.ITALIC, false));
        }

        lore.add(Component.empty());
        lore.add(Component.text("Click to view plots", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Creates the info item showing current page and town count
     *
     * @param currentPage Current page number (1-indexed for display)
     * @param totalPages  Total number of pages
     * @param totalTowns  Total number of towns
     * @return ItemStack info item
     */
    private static ItemStack createInfoItem(int currentPage, int totalPages, int totalTowns) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Town Selection", NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Page: ", NamedTextColor.GRAY)
                .append(Component.text(currentPage + "/" + totalPages, NamedTextColor.WHITE))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Total Towns: ", NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(totalTowns), NamedTextColor.WHITE))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Select a town to browse plots", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Gets the town from a clicked slot
     *
     * @param holder The inventory holder
     * @param slot   The clicked slot
     * @return The Town at that slot, or null
     */
    public static Town getTownFromSlot(TownSelectionHolder holder, int slot) {
        if (slot < 0 || slot >= TOWNS_PER_PAGE) {
            return null;
        }

        int townIndex = (holder.getCurrentPage() * TOWNS_PER_PAGE) + slot;
        List<Town> towns = holder.getAllTowns();

        if (townIndex >= towns.size()) {
            return null;
        }

        return towns.get(townIndex);
    }
}
