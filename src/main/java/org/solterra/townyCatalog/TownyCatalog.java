package org.solterra.townyCatalog;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import org.bukkit.plugin.java.JavaPlugin;
import org.solterra.townyCatalog.command.CatalogCommand;
import org.solterra.townyCatalog.command.TCatalogCommand;
import org.solterra.townyCatalog.listener.CatalogListener;
import org.solterra.townyCatalog.util.Config;

public final class TownyCatalog extends JavaPlugin {

    private static TownyCatalog instance;

    @Override
    public void onEnable() {
        // Set instance
        instance = this;

        // Check if Towny is installed
        if (!getServer().getPluginManager().isPluginEnabled("Towny")) {
            getLogger().severe("Towny is not installed! This plugin requires Towny to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load configuration
        Config.init(this);

        // Register event listener
        getServer().getPluginManager().registerEvents(new CatalogListener(), this);

        // Register /town catalog subcommand
        TownyCommandAddonAPI.addSubCommand(CommandType.TOWN, "catalog", new CatalogCommand());

        // Register /tcatalog command
        TCatalogCommand tcatalogCommand = new TCatalogCommand(this);
        var tcatalogCmd = getCommand("tcatalog");
        if (tcatalogCmd != null) {
            tcatalogCmd.setExecutor(tcatalogCommand);
            tcatalogCmd.setTabCompleter(tcatalogCommand);
        } else {
            getLogger().warning("Failed to register /tcatalog command - command not found in plugin.yml");
        }

        getLogger().info("TownyCatalog has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TownyCatalog has been disabled!");
    }

    /**
     * @return The plugin instance
     */
    public static TownyCatalog getInstance() {
        return instance;
    }
}
