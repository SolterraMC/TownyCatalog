package org.solterra.townyCatalog.gui;

import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Custom InventoryHolder for the Town Selection GUI
 * Allows identification of town selection inventories in events and stores pagination state
 */
public class TownSelectionHolder implements InventoryHolder {

    private Inventory inventory;
    private final UUID playerUUID;
    private final List<Town> allTowns;
    private int currentPage;

    public TownSelectionHolder(Player player, List<Town> allTowns) {
        this.playerUUID = player.getUniqueId();
        this.allTowns = allTowns;
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

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public List<Town> getAllTowns() {
        return allTowns;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    public int getTotalPages() {
        return (int) Math.ceil(allTowns.size() / 45.0);
    }

    public boolean hasNextPage() {
        return currentPage < getTotalPages() - 1;
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }
}
