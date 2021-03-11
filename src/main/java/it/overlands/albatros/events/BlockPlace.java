package it.overlands.albatros.events;


import it.overlands.albatros.Albatros;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.JSONObject;

public class BlockPlace implements Listener {
    @EventHandler
    // all'evento del blocco piazzato
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        System.out.println(e.getBlockPlaced());
        // se il blocco piazzato è una chest e se il player è in executingPlayers
        if(e.getBlockPlaced().getType().toString()=="CHEST" && Albatros.getExecutingPlayers().contains(e.getPlayer())){

            e.getPlayer().sendMessage("okay");
        }

        //JSONObject json = new JSONObject();
        //json.put("",e.getBlockPlaced().getType())


    }

}
