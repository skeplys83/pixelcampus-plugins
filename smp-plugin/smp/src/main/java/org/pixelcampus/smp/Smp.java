package org.pixelcampus.smp;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Smp extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getServer().getWorlds().forEach(world -> world.sendMessage(Component.text("Hello World from smp Plugin!")));
        Bukkit.getScheduler().runTaskLater(this, () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say Hello World from smp Plugin!"), 200L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
