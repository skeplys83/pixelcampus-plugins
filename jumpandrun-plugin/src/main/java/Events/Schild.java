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
        TextComponent line = (TextComponent)event.line(0);
        List<String> lines = line.content().lines().toList();
        Player player = event.getPlayer();

        if(lines.get(0).equals("[1. Platz]") && lines.size() != 2){
            String score = player.getName() + PlaceholderAPI.setPlaceholders(player, "%parkour_course_record_" + lines.get(1) + "_time%");

            try {
                event.line(2, Component.text(score));
            }catch (Exception e) {
                log.atInfo().log("couldnt write 3rd line");
            }

        } else{
            event.getPlayer().sendMessage("erste zeile muss 1. Platz] sein, zweite zeile die jump map. überprüfe das schild");
        }

        log.atInfo().log("test log :)");


    }


}
