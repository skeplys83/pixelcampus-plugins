package org.pixelcampus.smp;

import org.bukkit.plugin.java.JavaPlugin;
import org.pixelcampus.smp.features.speedladder.SpeedLadderListener;
import org.pixelcampus.smp.features.stats.commands.StatsAllCommand;
import org.pixelcampus.smp.features.stats.commands.StatsCommand;

public final class Smp extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        StatsAllCommand statsAllCommand = new StatsAllCommand();

        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("statsall").setExecutor(statsAllCommand);
        getCommand("statsall").setTabCompleter(statsAllCommand);

        getServer().getPluginManager().registerEvents(new SpeedLadderListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
