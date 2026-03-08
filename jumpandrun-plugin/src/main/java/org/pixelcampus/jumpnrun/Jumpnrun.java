package org.pixelcampus.jumpnrun;

import commands.ParkourWerte;
import org.bukkit.plugin.java.JavaPlugin;

public final class Jumpnrun extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("MeinName").setExecutor(new ParkourWerte());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
