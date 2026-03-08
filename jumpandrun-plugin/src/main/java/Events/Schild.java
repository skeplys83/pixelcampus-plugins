package Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class Schild implements Listener {


    private static final Logger log = LogManager.getLogger(Schild.class);

    //@EventHandler
    public void onSignPlace(BlockPlaceEvent event){

    if(event.getBlockPlaced().getType() == Material.OAK_SIGN ){
        event.getPlayer().sendMessage("Schild");
    }

    }

    @EventHandler
    public void onSignWrite(SignChangeEvent event){
        List<Component> lines = event.lines();
        lines.forEach(x -> log.atInfo().log(x));

        if(lines.size() != 2) {
            event.getPlayer().sendMessage("erste zeile muss [1. Platz] sein, zweite zeile die jump map. überprüfe das schild");
            return;
        }

        Player player = event.getPlayer();
        String firstLine = lines.getFirst().asComponent().toString();
        String secondLine = lines.get(1).asComponent().toString();

        if(firstLine.equals("[1. Platz]")){
            String score = player.getName() + PlaceholderAPI.setPlaceholders(player, " %parkour_course_record_" + secondLine + "_time%");
            event.line(2, Component.text(score));
        }
    }
}
