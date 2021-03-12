package it.overlands.albatros.events;


import it.overlands.albatros.Albatros;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.JSONObject;

import java.util.ArrayList;

public class BlockPlace implements Listener {
    @EventHandler
    // all'evento del blocco piazzato
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        System.out.println(e.getBlockPlaced().getLocation());

        //JSONObject json = new JSONObject();
        //json.put("",e.getBlockPlaced().getType())

        Block placed_block = e.getBlock();
        Player issuer = e.getPlayer();
        ArrayList<Player> ep = Albatros.getInstance().getExecutingPlayers();

        // se il player che ha piazzato la cassa non ha attivato il comando l'evento
        // è ignorato
        if(!ep.contains(issuer)){return;}

        if(placed_block.getType().equals(Material.CHEST)){
            //ha piazzato una chest
            int aux = Albatros.getInstance().addChest2Player(issuer,placed_block);
            int _MAXNUMCHEST = Albatros.getInstance().get_MAXNUMCHEST();
            if(aux>0 && aux< _MAXNUMCHEST){
                String message ="Chest confermata, te ne mancano" + (_MAXNUMCHEST -1);
                issuer.sendMessage(message);
            }
            if (aux == -1){
                issuer.sendMessage("Limite chest raggiunto operazione terminata");
            }
        }
    }

}
