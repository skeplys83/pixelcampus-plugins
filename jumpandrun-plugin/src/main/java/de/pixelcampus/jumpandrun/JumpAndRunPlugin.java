package de.pixelcampus.jumpandrun;

import org.bukkit.plugin.java.JavaPlugin;

public class JumpAndRunPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("PixelCampus Jump & Run Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PixelCampus Jump & Run Plugin disabled!");
    }
}
