package org.solterra.townyCatalog.model;

import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Location;

/**
 * Data holder class for plot information to be displayed in the catalog GUI
 */
public class PlotInfo {
    private final TownBlock plotBlock;
    private final String plotName;
    private final String townName;
    private final double price;
    private final TownBlockType plotType;
    private final Location plotLocation;
    private final int plotX;
    private final int plotZ;
    private final String worldName;

    public PlotInfo(TownBlock plotBlock,String plotName, String townName, double price, TownBlockType plotType,
                    Location plotLocation, int plotX, int plotZ, String worldName) {
        this.plotBlock = plotBlock;
        this.plotName = plotName;
        this.townName = townName;
        this.price = price;
        this.plotType = plotType;
        this.plotLocation = plotLocation;
        this.plotX = plotX;
        this.plotZ = plotZ;
        this.worldName = worldName;
    }

    public String getTownName() {
        return townName;
    }

    public TownBlockType getPlotType() {
        return plotType;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getPlotName() {
        if (plotName == null || plotName.isEmpty()) {
            return townName + " Plot";
        }

        return plotName;
    }

    public String getFormattedPrice() {
        return String.format("%.2f", price);
    }

    public String getCoordinates() {
        return String.format("X: %d, Z: %d", plotX, plotZ);
    }

    public double getPrice() {
        return price;
    }
}
