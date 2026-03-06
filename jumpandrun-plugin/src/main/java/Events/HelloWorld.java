package Events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HelloWorld implements Listener {

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event){
        Player player = event.getPlayer();
        event.getBlock().getWorld().sendMessage(Component.text(player.getName() + " destroyed a Block :("));
    }
}
