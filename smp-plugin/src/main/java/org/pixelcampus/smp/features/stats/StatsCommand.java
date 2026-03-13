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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class StatsCommand implements CommandExecutor, TabCompleter {

    private static final String PERM_SELF = "smp.stats.self";
    private static final String PERM_OTHERS = "smp.stats.others";
    private static final int MIN_PLAYTIME_TICKS = 7200; // 0.1h * 20 * 60 * 60
    private static final int MAX_PLAYERS = 20;
    private final Plugin plugin;

    public StatsCommand(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {

        if (args.length > 1) {
            return false;
        }

        if (args.length == 0) {
            if (!sender.hasPermission(PERM_OTHERS)) {
                sender.sendMessage("You don't have permission to view all player stats.");
                return true;
            }

            sendLeaderboardAsync(sender, StatsHelper.SortMode.PLAYTIME);
            return true;
        }

        String arg = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(arg);
        if (target.hasPlayedBefore() || target.isOnline()) {
            boolean isSelf = sender instanceof Player playerSender
                    && playerSender.getUniqueId().equals(target.getUniqueId());
            if (isSelf) {
                if (!sender.hasPermission(PERM_SELF)) {
                    sender.sendMessage("You don't have permission to view your own stats.");
                    return true;
                }
            } else if (!sender.hasPermission(PERM_OTHERS)) {
                sender.sendMessage("You don't have permission to view other players' stats.");
                return true;
            }

            sender.sendMessage(StatsHelper.formatStatsMessage(target, StatsHelper.queryStats(target)));
            return true;
        }

        StatsHelper.SortMode sortMode = StatsHelper.SortMode.fromInput(arg);
        if (sortMode == null) {
            sender.sendMessage(
                    "Unknown player or sort mode. Use /stats [player|playtime|deaths|streak|kills|blocks|distance]");
            return true;
        }

        if (!sender.hasPermission(PERM_OTHERS)) {
            sender.sendMessage("You don't have permission to view all player stats.");
            return true;
        }

        sendLeaderboardAsync(sender, sortMode);
        return true;
    }

    /**
     * Builds and sends the top player stats leaderboard without blocking command
     * execution.
     *
     * <p>
     * The method loads all offline players asynchronously, keeps only players with
     * at least {@code MIN_PLAYTIME_TICKS}, trims the candidate set to
     * {@code MAX_PLAYERS} by playtime, queries full stats for that subset, then
     * applies the requested sort mode before sending formatted lines to the sender.
     * </p>
     *
     * @param sender   recipient of loading/status and leaderboard messages
     * @param sortMode stat field used for final leaderboard ordering
     */
    private void sendLeaderboardAsync(@NotNull CommandSender sender, @NotNull StatsHelper.SortMode sortMode) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            List<StatsHelper.PlayerStatsRow> allRows = new ArrayList<>();

            sender.sendMessage("Loading player stats...");
            OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

            List<StatsHelper.PlayerPlaytimeRow> topPlaytimeRows = new ArrayList<>();
            for (OfflinePlayer offlinePlayer : offlinePlayers) {
                int playtimeTicks = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
                if (playtimeTicks < MIN_PLAYTIME_TICKS) {
                    continue;
                }
                topPlaytimeRows.add(new StatsHelper.PlayerPlaytimeRow(offlinePlayer, playtimeTicks));
            }

            topPlaytimeRows.sort(Comparator.comparingInt(StatsHelper.PlayerPlaytimeRow::playtimeTicks).reversed());
            if (topPlaytimeRows.size() > MAX_PLAYERS) {
                topPlaytimeRows = new ArrayList<>(topPlaytimeRows.subList(0, MAX_PLAYERS));
            }

            for (StatsHelper.PlayerPlaytimeRow row : topPlaytimeRows) {
                allRows.add(new StatsHelper.PlayerStatsRow(row.player(), StatsHelper.queryStats(row.player())));
            }

            allRows.sort(getComparator(sortMode));

            sender.sendMessage(Component.text(
                    "Stats - Sort: " + sortMode.displayName(),
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

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String name = onlinePlayer.getName();
            if (name.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                suggestions.add(name);
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