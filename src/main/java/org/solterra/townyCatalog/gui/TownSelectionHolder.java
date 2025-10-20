package org.solterra.townyCatalog.gui;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Custom InventoryHolder for the Town Selection GUI
 * Extends PaginatedInventoryHolder to eliminate pagination code duplication
 */
public class TownSelectionHolder extends PaginatedInventoryHolder<Town> {

    public TownSelectionHolder(Player player, List<Town> allTowns) {
        super(player, allTowns);
    }

    /**
     * Convenience method to get towns with proper type
     * @return List of all towns
     */
    public List<Town> getAllTowns() {
        return getAllItems();
    }
}
