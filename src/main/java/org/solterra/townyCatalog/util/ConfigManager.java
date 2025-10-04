package org.solterra.townyCatalog.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages configuration settings for TownyCatalog
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    /**
     * Reload the configuration from disk
     */
    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    /**
     * @return true if only plots from open towns should be shown
     */
    public boolean requireTownOpen() {
        return config.getBoolean("filters.require-town-open", true);
    }

    /**
     * @return true if only plots from public towns should be shown
     */
    public boolean requireTownPublic() {
        return config.getBoolean("filters.require-town-public", true);
    }

    /**
     * @return true if only affordable plots should be shown
     */
    public boolean requireAffordable() {
        return config.getBoolean("filters.require-affordable", true);
    }

    /**
     * @return true if only residential plots should be shown
     */
    public boolean residentialOnly() {
        return config.getBoolean("filters.residential-only", false);
    }
}
