package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ParkourWerte implements CommandExecutor{

    private static final String[] PARKOUR_PLACEHOLDERS = new String[]{
            "%parkour_global_version%",
            "%parkour_player_level%",
            "%parkour_player_rank%",
            "%parkour_player_personal_best_(course)_time%",
            "%parkour_course_record_(course)_player%",
            "%parkour_current_course_timer%",
            "%parkour_current_checkpoint_next%",
            "%parkour_leaderboard_(course)_(position)_time%",
            "%parkour_topten_(course)_(position)%",
            "%parkour_topfirstplaces_(position)%"
    };

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        commandSender.sendMessage("Parkour placeholders:");
        for (String placeholder : PARKOUR_PLACEHOLDERS) {
            commandSender.sendMessage(placeholder);
        }
        return true;
    }
}
