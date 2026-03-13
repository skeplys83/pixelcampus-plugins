package org.pixelcampus.smp.features.stats;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

public final class StatsHelper {

    private static final double TICKS_PER_HOUR = 20.0 * 60 * 60;
    private static final double METERS_PER_CENTIMETER = 0.01;
    private static final List<Material> MINEABLE_BLOCKS = buildMineableBlockList();

    private StatsHelper() {
    }

    public enum SortMode {
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

        public @NotNull String key() {
            return key;
        }

        public @NotNull String displayName() {
            return displayName;
        }

        public double extractValue(@NotNull JsonObject stats) {
            return switch (this) {
                case PLAYTIME -> stats.get("playtimeHours").getAsDouble();
                case DEATHS -> stats.get("totalDeaths").getAsInt();
                case SURVIVAL_STREAK -> stats.get("timeSinceDeathHours").getAsDouble();
                case PLAYER_KILLS -> stats.get("totalPlayerKills").getAsInt();
                case BLOCKS_MINED -> stats.get("totalBlocksMined").getAsInt();
                case DISTANCE -> stats.get("totalDistanceTraveledMeters").getAsDouble();
            };
        }

        public static SortMode fromInput(@NotNull String input) {
            String normalized = input.toLowerCase(Locale.ROOT);
            for (SortMode mode : values()) {
                if (mode.key.equals(normalized)) {
                    return mode;
                }
            }
            return null;
        }
    }

    public record PlayerStatsRow(@NotNull OfflinePlayer player, @NotNull JsonObject stats) {
    }

    public record PlayerPlaytimeRow(@NotNull OfflinePlayer player, int playtimeTicks) {
    }

    public static @NotNull JsonObject queryStats(@NotNull OfflinePlayer player) {
        JsonObject stats = new JsonObject();

        stats.addProperty("playtimeHours", player.getStatistic(Statistic.PLAY_ONE_MINUTE) / TICKS_PER_HOUR);
        stats.addProperty("totalDeaths", player.getStatistic(Statistic.DEATHS));
        stats.addProperty("timeSinceDeathHours", player.getStatistic(Statistic.TIME_SINCE_DEATH) / TICKS_PER_HOUR);
        stats.addProperty("totalPlayerKills", player.getStatistic(Statistic.PLAYER_KILLS));
        stats.addProperty("totalBlocksMined", getTotalBlocksMined(player));
        stats.addProperty("totalDistanceTraveledMeters", getTotalDistanceTraveledMeters(player));

        return stats;
    }

    public static @NotNull Component formatStatsMessage(@NotNull OfflinePlayer player, @NotNull JsonObject stats) {
        String playerName = player.getName() != null ? player.getName() : "Unknown";
        String playtimeHours = formatNumber(stats.get("playtimeHours").getAsDouble()) + "h";
        String totalDeaths = formatNumber(stats.get("totalDeaths").getAsInt());
        String timeSinceDeath = formatElapsedHours(stats.get("timeSinceDeathHours").getAsDouble());
        String totalPlayerKills = formatNumber(stats.get("totalPlayerKills").getAsInt());
        String totalBlocksMined = formatNumber(stats.get("totalBlocksMined").getAsInt());
        String totalDistanceTraveled = formatDistance(stats.get("totalDistanceTraveledMeters").getAsDouble());

        return Component.text()
                .append(player.isOnline()
                        ? Component.text("● ", NamedTextColor.GREEN)
                        : Component.text("○ ", NamedTextColor.DARK_GRAY))
                .append(Component.text(playerName, NamedTextColor.WHITE))
                .append(separator())
                .append(Component.text("⏱ ", NamedTextColor.GRAY))
                .append(Component.text(playtimeHours, NamedTextColor.WHITE))
                .append(separator())
                .append(Component.text("☠ ", NamedTextColor.GRAY))
                .append(Component.text(totalDeaths, NamedTextColor.WHITE))
                .append(separator())
                .append(Component.text("☠-⏱ ", NamedTextColor.GRAY))
                .append(Component.text(timeSinceDeath, NamedTextColor.WHITE))
                .append(separator())
                .append(Component.text("⚔ ", NamedTextColor.GRAY))
                .append(Component.text(totalPlayerKills, NamedTextColor.WHITE))
                .append(separator())
                .append(Component.text("⛏ ", NamedTextColor.GRAY))
                .append(Component.text(totalBlocksMined, NamedTextColor.WHITE))
                .append(separator())
                .append(Component.text("🦶 ", NamedTextColor.GRAY))
                .append(Component.text(totalDistanceTraveled, NamedTextColor.WHITE))
                .build();
    }

    private static int getTotalBlocksMined(@NotNull OfflinePlayer player) {
        int totalBlocksMined = 0;

        for (Material material : MINEABLE_BLOCKS) {
            totalBlocksMined += player.getStatistic(Statistic.MINE_BLOCK, material);
        }

        return totalBlocksMined;
    }

    private static @NotNull List<Material> buildMineableBlockList() {
        List<Material> blocks = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                blocks.add(material);
            }
        }
        return List.copyOf(blocks);
    }

    private static double getTotalDistanceTraveledMeters(@NotNull OfflinePlayer player) {
        long totalDistanceCentimeters = 0;

        totalDistanceCentimeters += player.getStatistic(Statistic.WALK_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.CROUCH_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.SPRINT_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.SWIM_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.FALL_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.CLIMB_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.FLY_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.WALK_ON_WATER_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.MINECART_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.BOAT_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.PIG_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.HORSE_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.AVIATE_ONE_CM);
        totalDistanceCentimeters += player.getStatistic(Statistic.STRIDER_ONE_CM);

        return totalDistanceCentimeters * METERS_PER_CENTIMETER;
    }

    private static @NotNull Component separator() {
        return Component.text(" | ", NamedTextColor.DARK_GRAY);
    }

    private static @NotNull String formatNumber(double value) {
        if (value >= 1_000_000) {
            return trimTrailingZero(value / 1_000_000) + "m";
        }
        if (value >= 1_000) {
            return trimTrailingZero(value / 1_000) + "k";
        }
        if (value == Math.floor(value)) {
            return Long.toString((long) value);
        }
        return trimTrailingZero(value);
    }

    private static @NotNull String formatDistance(double meters) {
        if (meters >= 1_000) {
            return trimTrailingZero(meters / 1_000) + "km";
        }
        return trimTrailingZero(meters) + "m";
    }

    private static @NotNull String formatElapsedHours(double hours) {
        if (hours >= 24) {
            return trimTrailingZero(hours / 24) + "d";
        }
        if (hours >= 1) {
            return trimTrailingZero(hours) + "h";
        }
        return Math.round(hours * 60) + "m";
    }

    private static @NotNull String trimTrailingZero(double value) {
        if (value >= 100 || value == Math.floor(value)) {
            return Long.toString(Math.round(value));
        }
        double rounded = Math.round(value * 10.0) / 10.0;
        if (rounded == Math.floor(rounded)) {
            return Long.toString((long) rounded);
        }
        return Double.toString(rounded);
    }
}