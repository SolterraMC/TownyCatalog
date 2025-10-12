package org.solterra.townyCatalog.api;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.solterra.townyCatalog.model.PlotInfo;
import org.solterra.townyCatalog.util.Config;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API for the Towny Catalog GUI system
 * Provides methods for retrieving, filtering, and organizing plots for sale
 */
public class TownyCatalogAPI {

    /**
     * Checks if a town has any plots for sale that the player can afford
     *
     * @param town   The town to check
     * @param player The player checking affordability
     * @return true if there are affordable plots for sale
     */
    public static boolean hasPurchasablePlots(Town town, Player player) {
        return town.getTownBlocks().stream()
                .filter(TownBlock::isForSale)
                .anyMatch(plotBlock -> canAffordPlot(plotBlock.getPlotPrice(), player));
    }

    /**
     * Checks if a player can afford a given price using Towny's economy system
     *
     * @param plotPrice The price to check
     * @param player    The player to check
     * @return true if the player can afford the price
     */
    public static boolean canAffordPlot(double plotPrice, Player player) {
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) {
            // Non-Towny residents cannot afford plots
            return false;
        }
        // If economy is not active, assume player can afford
        return !TownyEconomyHandler.isActive() || resident.getAccount().canPayFromHoldings(plotPrice);
    }

    /**
     * Gets all plots for sale in a town
     *
     * @param town   The town to search
     * @param player The player checking affordability
     * @return List of all affordable plots
     */
    public static List<TownBlock> getAllPurchasablePlotsIn(Town town, Player player) {
        return town.getTownBlocks().stream()
                .filter(TownBlock::isForSale)
                .filter(plotBlock -> {
                    // Filter by residential type if configured
                    if (Config.RESIDENTIAL_ONLY && !plotBlock.getType().equals(TownBlockType.RESIDENTIAL)) {
                        return false;
                    }
                    // Filter by affordability if configured
                    return !Config.REQUIRE_AFFORDABLE || canAffordPlot(plotBlock.getPlotPrice(), player);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets all towns that have plots the player can afford
     *
     * @param player The player to check
     * @return List of towns with affordable plots
     */
    public static List<Town> getTownsWithPurchasablePlots(Player player) {
        return TownyAPI.getInstance().getTowns().stream()
                .filter(town -> {
                    // Filter by town open status if configured
                    if (Config.REQUIRE_TOWN_OPEN && !town.isOpen()) {
                        return false;
                    }
                    // Filter by town public status if configured
                    return (!Config.REQUIRE_TOWN_PUBLIC || town.isPublic()) && hasPurchasablePlots(town, player);
                })
                .collect(Collectors.toList());
    }

    /**
     * Converts a TownBlock to a PlotInfo object for GUI display
     *
     * @param plotBlock The TownBlock to convert
     * @return PlotInfo object with display data
     */
    public static PlotInfo getPlotDisplayInfo(TownBlock plotBlock) {
        if (plotBlock == null) {
            return null;
        }

        String townName = plotBlock.getTownOrNull() != null ? plotBlock.getTownOrNull().getName() : "Unknown";
        double price = plotBlock.getPlotPrice();
        TownBlockType plotType = plotBlock.getType();
        Location location = getPlotCenterLocation(plotBlock);

        // Custom plot name
        String plotName = plotBlock.getName() != null ? plotBlock.getName() : "";

        if (!Config.SHOW_CUSTOM_PLOT_NAMES) {
            plotName = "";
        }

        int plotX = plotBlock.getX();
        int plotZ = plotBlock.getZ();
        String worldName = plotBlock.getWorldCoord().getBukkitWorld() != null
                ? plotBlock.getWorldCoord().getBukkitWorld().getName()
                : "Unknown";

        return new PlotInfo(plotBlock,plotName, townName, price, plotType, location, plotX, plotZ, worldName);
    }

    /**
     * Gets the center location of a plot for display purposes
     *
     * @param plotBlock The plot to get the location for
     * @return Location at the center of the plot
     */
    public static Location getPlotCenterLocation(TownBlock plotBlock) {
        if (plotBlock == null) {
            return null;
        }

        int blockSize = TownySettings.getTownBlockSize();
        World world = plotBlock.getWorldCoord().getBukkitWorld();
        if (world == null) {
            return null;
        }
        int halfBlockSize = blockSize / 2;
        double centerX = plotBlock.getX() * blockSize;
        double centerZ = plotBlock.getZ() * blockSize;
        Location centerLocation = new Location(world, centerX + halfBlockSize, 64.0, centerZ + halfBlockSize);
        centerLocation.setY(world.getHighestBlockYAt(centerLocation) + 1);

        return centerLocation;
    }

    /**
     * Sorts plots by price
     *
     * @param plots     List of plots to sort
     * @param ascending true for lowest to highest, false for highest to lowest
     * @return Sorted list of plots
     */
    public static List<TownBlock> sortPlotsByPrice(List<TownBlock> plots, boolean ascending) {
        return plots.stream()
                .sorted(ascending
                        ? Comparator.comparingDouble(TownBlock::getPlotPrice)
                        : Comparator.comparingDouble(TownBlock::getPlotPrice).reversed())
                .collect(Collectors.toList());
    }

}
