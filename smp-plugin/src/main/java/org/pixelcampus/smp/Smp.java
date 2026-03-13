package org.pixelcampus.smp;

import org.bukkit.plugin.java.JavaPlugin;
import org.pixelcampus.smp.features.speedladder.SpeedLadderListener;
import org.pixelcampus.smp.features.stats.StatsCommand;

public final class Smp extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        StatsCommand statsCommand = new StatsCommand(this);

        getCommand("stats").setExecutor(statsCommand);
        getCommand("stats").setTabCompleter(statsCommand);

        getServer().getPluginManager().registerEvents(new SpeedLadderListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
