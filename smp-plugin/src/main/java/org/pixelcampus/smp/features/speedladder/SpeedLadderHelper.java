package org.pixelcampus.smp.features.speedladder;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SpeedLadderHelper {

    private static final String PERM_USE = "smp.speedladder.use";
    private static final float MAX_LOOK_UP_PITCH = -20.0f;
    private static final BlockFace[] CARDINAL_FACES = {
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST
    };

    private SpeedLadderHelper() {
    }

    public static void spawnBoostParticles(@NotNull Player player) {
        player.getWorld().spawnParticle(
                Particle.CLOUD,
                player.getLocation().add(0.0, 0.8, 0.0),
                1,
                0.0,
                0.1,
                0.0,
                0.01);
    }

    public static void playBoostSound(@NotNull Player player) {
        // Player#playSound only sends this sound packet to that player.
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.PLAYERS, 1.0f,
                1.35f);
    }

    public static boolean canBoost(@NotNull Player player) {
        return canBoostWithoutSneak(player) && player.isSneaking();
    }

    public static boolean canBoostWithoutSneak(@NotNull Player player) {
        return player.isOnline()
                && !player.isDead()
                && player.getGameMode() != GameMode.SPECTATOR
                && !player.isInsideVehicle()
                && player.hasPermission(PERM_USE)
                && isLookingUp(player)
                && isTouchingClimbable(player);
    }

    private static boolean isLookingUp(@NotNull Player player) {
        return player.getLocation().getPitch() <= MAX_LOOK_UP_PITCH;
    }

    private static boolean isTouchingClimbable(@NotNull Player player) {
        Block feet = player.getLocation().getBlock();
        Block eye = player.getEyeLocation().getBlock();

        if (isClimbable(feet.getType()) || isClimbable(eye.getType())
                || isClimbable(feet.getRelative(BlockFace.DOWN).getType())) {
            return true;
        }

        for (BlockFace face : CARDINAL_FACES) {
            if (isClimbable(feet.getRelative(face).getType()) || isClimbable(eye.getRelative(face).getType())) {
                return true;
            }
        }

        return false;
    }

    private static boolean isClimbable(@NotNull Material blockType) {
        return Tag.CLIMBABLE.isTagged(blockType);
    }
}
