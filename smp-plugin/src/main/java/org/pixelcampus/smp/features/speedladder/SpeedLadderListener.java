package org.pixelcampus.smp.features.speedladder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class SpeedLadderListener implements Listener {

    private static final String PERM_USE = "smp.speedladder.use";
    private static final float MAX_LOOK_UP_PITCH = -20.0f;
    private static final double UPWARD_BOOST = 0.42;
    private static final BlockFace[] CARDINAL_FACES = {
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST
    };

    private final Set<UUID> activeBoostPlayers = new HashSet<>();

    public SpeedLadderListener(@NotNull JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tickBoostPlayers, 1L, 1L);
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
        Iterator<UUID> iterator = activeBoostPlayers.iterator();
        while (iterator.hasNext()) {
            UUID playerId = iterator.next();
            Player player = Bukkit.getPlayer(playerId);

            if (player == null || !canBoost(player)) {
                iterator.remove();
                continue;
            }

            Vector velocity = player.getVelocity();
            velocity.setY(Math.max(velocity.getY(), UPWARD_BOOST));
            player.setVelocity(velocity);
            player.setFallDistance(0.0f);
            spawnBoostParticles(player);
        }
    }

    private void spawnBoostParticles(@NotNull Player player) {
        player.getWorld().spawnParticle(
                Particle.CLOUD,
                player.getLocation().add(0.0, 0.8, 0.0),
                1,
                0.0,
                0.1,
                0.0,
                0.01);
    }

    private boolean canBoost(@NotNull Player player) {
        return player.isOnline()
                && !player.isDead()
                && player.getGameMode() != GameMode.SPECTATOR
                && !player.isInsideVehicle()
                && player.hasPermission(PERM_USE)
                && player.isSneaking()
                && isLookingUp(player)
                && isTouchingClimbable(player);
    }

    private boolean isLookingUp(@NotNull Player player) {
        return player.getLocation().getPitch() <= MAX_LOOK_UP_PITCH;
    }

    private boolean isTouchingClimbable(@NotNull Player player) {
        Block feet = player.getLocation().getBlock();
        Block eye = player.getEyeLocation().getBlock();

        if (isClimbable(feet) || isClimbable(eye) || isClimbable(feet.getRelative(BlockFace.DOWN))) {
            return true;
        }

        for (BlockFace face : CARDINAL_FACES) {
            if (isClimbable(feet.getRelative(face)) || isClimbable(eye.getRelative(face))) {
                return true;
            }
        }

        return false;
    }

    private boolean isClimbable(@NotNull Block block) {
        return Tag.CLIMBABLE.isTagged(block.getType());
    }
}