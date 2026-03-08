package Events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class Schild implements Listener {


    @EventHandler
    public void onSignPlace(BlockPlaceEvent event){

    if(event.getBlockPlaced().getType() == Material.OAK_SIGN ){
        event.getPlayer().sendMessage("Schild");
    }

    }


}
