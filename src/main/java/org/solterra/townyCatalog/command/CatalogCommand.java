package org.solterra.townyCatalog.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.solterra.townyCatalog.gui.TownSelectionGUI;

/**
 * Command to open the Towny Catalog GUI via /town catalog
 */
public class CatalogCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
            return true;
        }

        if (!sender.hasPermission("townycatalog.use")) {
            sender.sendMessage(Component.text("You don't have permission to use the catalog!", NamedTextColor.RED));
            return true;
        }

        // Open the town selection GUI
        TownSelectionGUI.openTownSelection(player);
        return true;
    }
}
