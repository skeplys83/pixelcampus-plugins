package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pixelcampus.Pixelcampus;

import java.util.List;

public class HelloWorld implements CommandExecutor {

    private final Pixelcampus plugin;

    public HelloWorld(Pixelcampus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        commandSender.sendMessage("Hello Test Command :)");
        return false;
    }
}
