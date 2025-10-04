package org.solterra.townyCatalog.command;

import com.palmergames.bukkit.towny.TownyAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solterra.townyCatalog.TownyCatalog;
import org.solterra.townyCatalog.util.ConfigManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command handler for /tcatalog with reload and info subcommands
 */
public class TCatalogCommand implements CommandExecutor, TabCompleter {

    private final TownyCatalog plugin;

    public TCatalogCommand(TownyCatalog plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        // No arguments - show help
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "reload":
                handleReload(sender);
                break;
            case "info":
                handleInfo(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    /**
     * Handles the reload subcommand
     */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("townycatalog.admin")) {
            sender.sendMessage(Component.text("You don't have permission to reload the config!", NamedTextColor.RED));
            return;
        }

        try {
            plugin.getConfigManager().reload();
            sender.sendMessage(Component.text("TownyCatalog configuration reloaded successfully!", NamedTextColor.GREEN));
        } catch (Exception e) {
            sender.sendMessage(Component.text("Failed to reload configuration: " + e.getMessage(), NamedTextColor.RED));
            plugin.getLogger().severe("Failed to reload config: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                plugin.getLogger().severe("  at " + element.toString());
            }
        }
    }

    /**
     * Handles the info subcommand
     */
    private void handleInfo(CommandSender sender) {
        ConfigManager config = plugin.getConfigManager();

        sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GREEN));
        sender.sendMessage(Component.text("TownyCatalog", NamedTextColor.GOLD, TextDecoration.BOLD)
                .append(Component.text(" v" + plugin.getPluginMeta().getVersion(), NamedTextColor.YELLOW)));
        sender.sendMessage(Component.empty());

        // Current filter settings
        sender.sendMessage(Component.text("Filter Settings:", NamedTextColor.AQUA, TextDecoration.BOLD));
        sender.sendMessage(formatConfigLine("Require Town Open", config.requireTownOpen()));
        sender.sendMessage(formatConfigLine("Require Town Public", config.requireTownPublic()));
        sender.sendMessage(formatConfigLine("Require Affordable", config.requireAffordable()));
        sender.sendMessage(formatConfigLine("Residential Only", config.residentialOnly()));

        sender.sendMessage(Component.empty());

        // Statistics
        int totalTowns = TownyAPI.getInstance().getTowns().size();
        sender.sendMessage(Component.text("Statistics:", NamedTextColor.AQUA, TextDecoration.BOLD));
        sender.sendMessage(Component.text("  Total Towns: ", NamedTextColor.GRAY)
                .append(Component.text(totalTowns, NamedTextColor.WHITE)));

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("Use ", NamedTextColor.GRAY)
                .append(Component.text("/tcatalog reload", NamedTextColor.YELLOW))
                .append(Component.text(" to reload config", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GREEN));
    }

    /**
     * Sends help/usage message
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GREEN));
        sender.sendMessage(Component.text("TownyCatalog Commands", NamedTextColor.GOLD, TextDecoration.BOLD));
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("/tcatalog info", NamedTextColor.YELLOW)
                .append(Component.text(" - Show plugin info and settings", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/tcatalog reload", NamedTextColor.YELLOW)
                .append(Component.text(" - Reload configuration", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/town catalog", NamedTextColor.YELLOW)
                .append(Component.text(" - Open the plot catalog", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GREEN));
    }

    /**
     * Formats a config line with color coding for boolean values
     */
    private Component formatConfigLine(String key, boolean value) {
        Component valueComponent = value
                ? Component.text("✓ Enabled", NamedTextColor.GREEN)
                : Component.text("✗ Disabled", NamedTextColor.RED);

        return Component.text("  " + key + ": ", NamedTextColor.GRAY)
                .append(valueComponent);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("info", "reload"));
            String partial = args[0].toLowerCase();
            completions.removeIf(s -> !s.startsWith(partial));
            return completions;
        }
        return new ArrayList<>();
    }
}
