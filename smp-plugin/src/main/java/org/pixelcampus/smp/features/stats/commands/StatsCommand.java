package org.pixelcampus.smp.features.stats.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.pixelcampus.smp.features.stats.PlayerStatsHelper;

public class StatsCommand implements CommandExecutor {

    private static final String PERM_SELF = "smp.stats.self";
    private static final String PERM_OTHERS = "smp.stats.others";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {

        if (args.length > 1) {
            return false;
        }

        if (args.length == 0) {
            // Viewing own stats
            if (!sender.hasPermission(PERM_SELF)) {
                sender.sendMessage("You don't have permission to view your own stats.");
                return true;
            }

            OfflinePlayer target = sender instanceof OfflinePlayer ? (OfflinePlayer) sender
                    : Bukkit.getOfflinePlayer("Notch");

            sender.sendMessage(PlayerStatsHelper.formatStatsMessage(target, PlayerStatsHelper.queryStats(target)));
            return true;
        }

        // Viewing another player's stats
        if (!sender.hasPermission(PERM_OTHERS)) {
            sender.sendMessage("You don't have permission to view other players' stats.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage("Player '" + args[0] + "' has never played on this server.");
            return true;
        }

        sender.sendMessage(PlayerStatsHelper.formatStatsMessage(target, PlayerStatsHelper.queryStats(target)));
        return true;
    }
}