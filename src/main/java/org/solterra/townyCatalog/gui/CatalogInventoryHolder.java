package org.solterra.townyCatalog.gui;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Custom InventoryHolder for the Towny Catalog GUI
 * Allows identification of catalog inventories in events and stores pagination state
 */
public class CatalogInventoryHolder implements InventoryHolder {

    private Inventory inventory;
    private final UUID playerUUID;
    private final List<TownBlock> allPlots;
    private final Town selectedTown;
    private int currentPage;

    public CatalogInventoryHolder(Player player, List<TownBlock> allPlots, Town selectedTown) {
        this.playerUUID = player.getUniqueId();
        this.allPlots = allPlots;
        this.selectedTown = selectedTown;
        this.currentPage = 0;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    public List<TownBlock> getAllPlots() {
        return allPlots;
    }

    public Town getSelectedTown() {
        return selectedTown;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    public int getTotalPages() {
        return (int) Math.ceil(allPlots.size() / 45.0);
    }

    public boolean hasNextPage() {
        return currentPage < getTotalPages() - 1;
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }
}
