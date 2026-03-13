package org.pixelcampus.smp.features.stats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class StatsAllCommand implements CommandExecutor, TabCompleter {

    private static final String PERM_ALL = "smp.stats.all";
    private static final int MIN_PLAYTIME_TICKS = 7200; // 0.1h * 20 * 60 * 60
    private final Plugin plugin;

    public StatsAllCommand(@NotNull Plugin plugin) {
        this.plugin = plugin;
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

        StatsHelper.SortMode sortMode = StatsHelper.SortMode.PLAYTIME;
        if (args.length == 1) {
            StatsHelper.SortMode parsed = StatsHelper.SortMode.fromInput(args[0]);
            if (parsed == null) {
                return false;
            }
            sortMode = parsed;
        }

        sendLeaderboardAsync(sender, sortMode);
        return true;
    }

    /**
     * Builds and sends the full leaderboard (online + offline players)
     * asynchronously.
     *
     * <p>
     * The method iterates through all known offline players from Bukkit, skips
     * players
     * that have never joined, queries stats, applies the selected sort mode, and
     * sends
     * ranked output to the command sender.
     * </p>
     *
     * @param sender   recipient of loading/status and leaderboard messages
     * @param sortMode stat field used for leaderboard ordering
     */
    private void sendLeaderboardAsync(@NotNull CommandSender sender, @NotNull StatsHelper.SortMode sortMode) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            List<StatsHelper.PlayerStatsRow> allRows = new ArrayList<>();

            sender.sendMessage("Loading all player stats.. This may take a while.");
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                int playtimeTicks = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
                if ((!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline())
                        || playtimeTicks < MIN_PLAYTIME_TICKS) {
                    continue;
                }
                allRows.add(new StatsHelper.PlayerStatsRow(offlinePlayer, StatsHelper.queryStats(offlinePlayer)));
            }

            if (allRows.isEmpty()) {
                sender.sendMessage("No player stats found.");
                return;
            }

            allRows.sort(getComparator(sortMode));

            sender.sendMessage(Component.text(
                    "Statistics - Sort: " + sortMode.displayName(),
                    NamedTextColor.GOLD));

            for (int i = 0; i < allRows.size(); i++) {
                StatsHelper.PlayerStatsRow row = allRows.get(i);
                int rank = i + 1;

                Component line = Component.text()
                        .append(Component.text("#" + rank + " ", NamedTextColor.DARK_GRAY))
                        .append(StatsHelper.formatStatsMessage(row.player(), row.stats()))
                        .build();

                sender.sendMessage(line);
            }
        });
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) {
            return List.of();
        }

        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();

        for (StatsHelper.SortMode mode : StatsHelper.SortMode.values()) {
            if (mode.key().startsWith(prefix)) {
                suggestions.add(mode.key());
            }
        }

        return suggestions;
    }

    private @NotNull Comparator<StatsHelper.PlayerStatsRow> getComparator(@NotNull StatsHelper.SortMode sortMode) {
        return (left, right) -> {
            int valueCompare = Double.compare(sortMode.extractValue(right.stats()),
                    sortMode.extractValue(left.stats()));
            if (valueCompare != 0) {
                return valueCompare;
            }

            String leftName = left.player().getName() != null ? left.player().getName()
                    : left.player().getUniqueId().toString();
            String rightName = right.player().getName() != null ? right.player().getName()
                    : right.player().getUniqueId().toString();
            return leftName.compareToIgnoreCase(rightName);
        };
    }
}
