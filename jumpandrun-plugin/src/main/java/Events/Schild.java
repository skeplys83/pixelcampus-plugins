package Events;

import net.kyori.adventure.text.Component;
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
        List<Component> lines = event.lines();

        event.getPlayer().sendMessage(lines.get(0).toString() + "test");

        if(lines.get(0).toString() == "[1. Platz]"){
            event.getPlayer().sendMessage(lines.get(0));
        }
        else{
            event.getPlayer().sendMessage("falsch");
        }



    }


}
