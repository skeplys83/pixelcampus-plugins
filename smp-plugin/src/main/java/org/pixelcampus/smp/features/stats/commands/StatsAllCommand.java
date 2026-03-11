package org.pixelcampus.smp.features.stats.commands;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.pixelcampus.smp.features.stats.PlayerStatsHelper;

public class StatsAllCommand implements CommandExecutor, TabCompleter {

    private static final String PERM_ALL = "smp.stats.others";

    private enum SortMode {
        PLAYTIME("playtime", "Playtime"),
        DEATHS("deaths", "Deaths"),
        SURVIVAL_STREAK("streak", "Survival Streak"),
        PLAYER_KILLS("kills", "Player Kills"),
        BLOCKS_MINED("blocks", "Blocks Mined"),
        DISTANCE("distance", "Distance");

        private final String key;
        private final String displayName;

        SortMode(String key, String displayName) {
            this.key = key;
            this.displayName = displayName;
        }

        private static SortMode fromInput(@NotNull String input) {
            String normalized = input.toLowerCase(Locale.ROOT);
            for (SortMode mode : values()) {
                if (mode.key.equals(normalized)) {
                    return mode;
                }
            }
            return null;
        }
    }

    private record PlayerStatsRow(@NotNull OfflinePlayer player, @NotNull JsonObject stats) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {

        if (args.length > 1) {
            return false;
        }

        if (!sender.hasPermission(PERM_ALL)) {
            sender.sendMessage("You don't have permission to view all player stats.");
            return true;
        }

        SortMode sortMode = SortMode.PLAYTIME;
        if (args.length == 1) {
            SortMode parsed = SortMode.fromInput(args[0]);
            if (parsed == null) {
                return false;
            }
            sortMode = parsed;
        }

        List<PlayerStatsRow> allRows = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                continue;
            }
            allRows.add(new PlayerStatsRow(offlinePlayer, PlayerStatsHelper.queryStats(offlinePlayer)));
        }

        if (allRows.isEmpty()) {
            sender.sendMessage("No player stats found.");
            return true;
        }

        sender.sendMessage(Component.text(
                "StatsAll - Sort: " + sortMode.displayName,
                NamedTextColor.GOLD));

        allRows.sort(getComparator(sortMode));

        for (int i = 0; i < allRows.size(); i++) {
            PlayerStatsRow row = allRows.get(i);
            int rank = i + 1;

            Component line = Component.text()
                    .append(Component.text("#" + rank + " ", NamedTextColor.DARK_GRAY))
                    .append(PlayerStatsHelper.formatStatsMessage(row.player))
                    .build();

            sender.sendMessage(line);
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) {
            return List.of();
        }

        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();
        for (SortMode mode : SortMode.values()) {
            if (mode.key.startsWith(prefix)) {
                suggestions.add(mode.key);
            }
        }
        return suggestions;
    }

    private @NotNull Comparator<PlayerStatsRow> getComparator(@NotNull SortMode sortMode) {
        return (left, right) -> {
            int valueCompare = Double.compare(getSortValue(right.stats, sortMode), getSortValue(left.stats, sortMode));
            if (valueCompare != 0) {
                return valueCompare;
            }

            String leftName = left.player.getName() != null ? left.player.getName()
                    : left.player.getUniqueId().toString();
            String rightName = right.player.getName() != null ? right.player.getName()
                    : right.player.getUniqueId().toString();
            return leftName.compareToIgnoreCase(rightName);
        };
    }

    private double getSortValue(@NotNull JsonObject stats, @NotNull SortMode sortMode) {
        return switch (sortMode) {
            case PLAYTIME -> stats.get("playtimeHours").getAsDouble();
            case DEATHS -> stats.get("totalDeaths").getAsInt();
            case SURVIVAL_STREAK -> stats.get("timeSinceDeathHours").getAsDouble();
            case PLAYER_KILLS -> stats.get("totalPlayerKills").getAsInt();
            case BLOCKS_MINED -> stats.get("totalBlocksMined").getAsInt();
            case DISTANCE -> stats.get("totalDistanceTraveledMeters").getAsDouble();
        };
    }
}