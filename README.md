# TownyCatalog

A GUI catalog plugin for browsing and purchasing Towny plots. TownyCatalog provides players with an inventory-based interface to view all available plots across towns without needing to explore in-game.

## Features

- **Two-level GUI navigation** - Browse by town, then view individual plots
- **Smart filtering** - Configurable filters for town status (public/open), plot affordability, and plot types
- **Plot information** - View prices, town details, mayor, tax rates, and more
- **Quick access** - One command (`/town catalog`) opens the catalog
- **Teleportation** - Click any plot to teleport to its location
- **Lightweight** - Only depends on Towny and Paper
- **Pagination support** - Handles towns with large numbers of plots

By default, only plots from public and open towns are shown.

## Installation

1. Ensure Towny is installed and running on your server
2. Download the latest TownyCatalog JAR from releases
3. Place the JAR file in your server's `plugins/` directory
4. Restart the server

The plugin will automatically generate a `config.yml` file on first run.

## Commands

**Player Command:**
- `/town catalog` - Opens the plot catalog GUI

**Admin Commands:**
- `/tcatalog reload` - Reload configuration
- `/tcatalog info` - Display plugin information

Players use `/town catalog` to open the town selection menu, which displays all towns with available plots (filtered by configuration settings). Clicking a town opens a second GUI showing that town's plots. Clicking a plot teleports the player to its location.

## Configuration

The plugin generates a `config.yml` file with the following options:

```yaml
filters:
  # Only show plots from towns marked as "open"
  require-town-open: true

  # Only show plots from towns marked as "public"
  require-town-public: true

  # Only show plots the player can afford
  require-affordable: false

  # Only show residential plots
  residential-only: false
```

**Default behavior:** Shows all for-sale plots from public and open towns, regardless of price or plot type.

Use `/tcatalog reload` to apply configuration changes without restarting the server.

## Requirements

- Paper 1.21+ (or compatible fork)
- Java 21+
- Towny 0.101.2.0+

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `townycatalog.use` | Access the plot catalog | All players |
| `townycatalog.admin` | Reload plugin configuration | Operators only |

## Building from Source

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/TownyCatalog-1.0-SNAPSHOT.jar`.

## License

MIT License. See `LICENSE` for details.


