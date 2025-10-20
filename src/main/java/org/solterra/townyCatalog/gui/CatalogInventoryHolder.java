package org.solterra.townyCatalog.gui;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Custom InventoryHolder for the Towny Catalog GUI
 * Extends PaginatedInventoryHolder to eliminate pagination code duplication
 */
public class CatalogInventoryHolder extends PaginatedInventoryHolder<TownBlock> {

    private final Town selectedTown;

    public CatalogInventoryHolder(Player player, List<TownBlock> allPlots, Town selectedTown) {
        super(player, allPlots);
        this.selectedTown = selectedTown;
    }

    /**
     * Convenience method to get plots with proper type
     * @return List of all plots
     */
    public List<TownBlock> getAllPlots() {
        return getAllItems();
    }

    public Town getSelectedTown() {
        return selectedTown;
    }
}
