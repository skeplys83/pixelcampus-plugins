package org.pixelcampus;

import commands.HelloWorld;
import org.bukkit.plugin.java.JavaPlugin;

public final class Pixelcampus extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("testCommand").setExecutor(new HelloWorld(this));
        getServer().getPluginManager().registerEvents(new Events.HelloWorld(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
