package de.pixelcampus.smp;

import org.bukkit.plugin.java.JavaPlugin;

public class SMPPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("PixelCampus SMP Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PixelCampus SMP Plugin disabled!");
    }
}
