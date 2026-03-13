package org.pixelcampus.smp.features.speedladder;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SpeedLadderListener implements Listener {

    private static final double UPWARD_BOOST = 0.67;
    private static final int SOUND_TICK_INTERVAL = 4;

    private static final Component CLIMB_HINT = Component.text("Hold sneak to boost", NamedTextColor.GRAY);

    private final Set<UUID> activeBoostPlayers = ConcurrentHashMap.newKeySet();

    public final JavaPlugin plugin;

    public SpeedLadderListener(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> tickBoostPlayers(), 1L, 1L);
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (SpeedLadderHelper.canBoostWithoutSneak(player) && !player.isSneaking()) {
            player.sendActionBar(CLIMB_HINT);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(@NotNull PlayerToggleSneakEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (event.isSneaking()) {
            activeBoostPlayers.add(playerId);
        } else {
            activeBoostPlayers.remove(playerId);
        }
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        activeBoostPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(@NotNull PlayerKickEvent event) {
        activeBoostPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        activeBoostPlayers.remove(event.getPlayer().getUniqueId());
    }

    private void tickBoostPlayers() {
        activeBoostPlayers.forEach(playerUUID -> {
            Player player = Bukkit.getPlayer(playerUUID);

            if (player == null) {
                activeBoostPlayers.remove(playerUUID);
                return;
            }

            player.getScheduler().run(plugin, t -> {
                if (!SpeedLadderHelper.canBoost(player)) {
                    activeBoostPlayers.remove(playerUUID);
                    return;
                }

                Vector v = player.getVelocity();
                v.setY(Math.max(v.getY(), UPWARD_BOOST));
                player.setVelocity(v);
                player.setFallDistance(0.0f);
                SpeedLadderHelper.spawnBoostParticles(player);
                if (player.getTicksLived() % SOUND_TICK_INTERVAL == 0) {
                    SpeedLadderHelper.playBoostSound(player);
                }
            }, () -> {
                activeBoostPlayers.remove(playerUUID);
            });
        });
    }

}