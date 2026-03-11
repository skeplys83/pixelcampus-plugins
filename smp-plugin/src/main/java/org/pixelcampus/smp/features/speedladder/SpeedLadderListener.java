package org.pixelcampus.smp.features.speedladder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

public class SpeedLadderListener implements Listener {

    @EventHandler
    public void onPlayerToggleSneak(@NotNull PlayerToggleSneakEvent event) {
        // TODO: speedLadder implementation
        // Intended behavior: when sneaking on climbable blocks (ladder, vines, etc.)
        // and looking up, boost player upward.
    }
}