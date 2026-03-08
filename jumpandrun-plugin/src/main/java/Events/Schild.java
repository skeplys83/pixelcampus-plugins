package Events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class Schild implements Listener {


    @EventHandler
    public void onSignPlace(BlockPlaceEvent event){

    if(event.getBlockPlaced().getType() == Material.OAK_SIGN ){
        event.getPlayer().sendMessage("Schild");
    }

    }

    @EventHandler
    public void onSignWrite(SignChangeEvent event){
        TextComponent line = (TextComponent)event.line(0);
        String firstLine = line.content().lines().findFirst().get();

        event.getPlayer().sendMessage(firstLine + " first line");

        if(line.content().equals("[1. Platz]")){
            event.getPlayer().sendMessage(line.toString());
        }
        else{
            event.getPlayer().sendMessage("falsch");
        }



    }


}
