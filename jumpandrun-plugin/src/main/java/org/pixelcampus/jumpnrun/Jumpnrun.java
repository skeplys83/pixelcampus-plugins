package org.pixelcampus.jumpnrun;

import commands.HelloWorld;
import org.bukkit.plugin.java.JavaPlugin;

public final class Jumpnrun extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("testCommand").setExecutor(new HelloWorld());
        getServer().getPluginManager().registerEvents(new Events.HelloWorld(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
