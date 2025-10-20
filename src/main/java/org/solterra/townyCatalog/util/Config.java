package org.solterra.townyCatalog.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages configuration settings for TownyCatalog
 * Uses static fields pattern for better performance and cleaner access
 */
public class Config {

    // Plot display settings
    public static boolean SHOW_CUSTOM_PLOT_NAMES = true;

    // Filter settings
    public static boolean REQUIRE_TOWN_OPEN = true;
    public static boolean REQUIRE_TOWN_PUBLIC = true;
    public static boolean REQUIRE_AFFORDABLE = false;
    public static boolean RESIDENTIAL_ONLY = false;

    /**
     * Initialize configuration from plugin
     * Should be called once during plugin enable
     */
    public static void init(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // Add default values programmatically to ensure new options appear in existing configs
        config.addDefault("plots.show-custom-plot-name", true);
        config.addDefault("filters.require-town-open", true);
        config.addDefault("filters.require-town-public", true);
        config.addDefault("filters.require-affordable", false);
        config.addDefault("filters.residential-only", false);

        // Merge defaults into existing config without overwriting user values
        config.options().copyDefaults(true);
        plugin.saveConfig();

        // Load values into static fields
        loadConfig(config);
    }

    /**
     * Reload configuration from disk
     */
    public static void reload(JavaPlugin plugin) {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        loadConfig(config);
    }

    /**
     * Load configuration values into static fields
     */
    private static void loadConfig(FileConfiguration config) {
        SHOW_CUSTOM_PLOT_NAMES = config.getBoolean("plots.show-custom-plot-name", true);
        REQUIRE_TOWN_OPEN = config.getBoolean("filters.require-town-open", true);
        REQUIRE_TOWN_PUBLIC = config.getBoolean("filters.require-town-public", true);
        REQUIRE_AFFORDABLE = config.getBoolean("filters.require-affordable", false);
        RESIDENTIAL_ONLY = config.getBoolean("filters.residential-only", false);
    }
}
