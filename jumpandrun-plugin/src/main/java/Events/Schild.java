package Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

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
        String firstLine = PlainTextComponentSerializer.plainText().serialize(lines.get(0));
        String secondLine = PlainTextComponentSerializer.plainText().serialize(lines.get(1));
        Player player = event.getPlayer();

        if(firstLine.isBlank() || secondLine.isBlank()) {
            event.getPlayer().sendMessage("erste zeile muss [1. Platz] sein, zweite zeile die jump map. überprüfe das schild");
            return;
        }

        if(firstLine.equals("[1. Platz]")){
            String score = PlaceholderAPI.setPlaceholders(player, "%parkour_course_record_" + secondLine + "_time%");
            event.line(2, Component.text(player.getName()));
            event.line(3, Component.text(score));
        }
    }
}
