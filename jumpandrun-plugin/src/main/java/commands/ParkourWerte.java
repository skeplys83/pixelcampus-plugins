package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import me.clip.placeholderapi.PlaceholderAPI;

public class ParkourWerte implements CommandExecutor{

    private static final String[] PARKOUR_PLACEHOLDERS = new String[]{
            "%parkour_course_record_(course)_time%"
    };

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(!(commandSender instanceof Player))return false;
        Player player = (Player)commandSender;

        player.sendMessage("Parkour placeholders:");

        for (String placeholder : PARKOUR_PLACEHOLDERS) {
            String parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
            player.sendMessage(parsed);
        }

        return true;
    }
}
