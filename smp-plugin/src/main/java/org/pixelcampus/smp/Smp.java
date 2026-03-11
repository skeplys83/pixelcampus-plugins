package org.pixelcampus.smp;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

public final class Smp extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getCommand("test")
                .setExecutor((sender, command, label, args) -> {
                    sender.sendMessage(Component.text("Hello, world!"));
                    return true;
                });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
