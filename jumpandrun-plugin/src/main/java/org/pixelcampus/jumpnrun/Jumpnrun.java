package org.pixelcampus.jumpnrun;

import Events.Schild;
import commands.ParkourWerte;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Jumpnrun extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("parkourwerte").setExecutor(new ParkourWerte());
        getServer().getPluginManager().registerEvents(new Schild(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
