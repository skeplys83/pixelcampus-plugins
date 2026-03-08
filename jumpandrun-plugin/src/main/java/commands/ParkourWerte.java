package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import me.clip.placeholderapi.PlaceholderAPI;

public class ParkourWerte implements CommandExecutor{



    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

        if(!(commandSender instanceof Player))return false;
        Player player = (Player)commandSender;

        if(strings.length == 1){
            player.sendMessage("%parkour_course_record_" + strings[0] + "_time%");
        }





        return true;
    }
}
