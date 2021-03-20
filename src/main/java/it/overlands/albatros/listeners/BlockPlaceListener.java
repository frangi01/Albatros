package it.overlands.albatros.listeners;


import it.overlands.albatros.Albatros;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.json.JSONObject;
import javax.swing.*;
import java.util.ArrayList;

import static java.lang.Math.abs;

public class BlockPlaceListener implements Listener {

    /**
    * Un listener per confermare la posa delle chest
    **/

    // all'evento del blocco piazzato
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        //System.out.println(e.getBlockPlaced().getLocation());
        //JSONObject json = new JSONObject();
        //json.put("",e.getBlockPlaced().getType())

        Block pb = e.getBlock();
        Player issuer = e.getPlayer();
        ArrayList<String> ep = Albatros.getExecutingPlayers();



        /*se il player che ha piazzato la cassa non ha attivato il comando l'evento è ignorato */
        if(!ep.contains(issuer.getDisplayName())){return;}

        if(pb.getType().equals(Material.CHEST)){
            //se è nel mondo giusto
            Chest placed_block = (Chest) pb.getState();
            //TODO mettere il nome del mondo giusto
            //if(e.getPlayer().getWorld().getName().equals("world")){
                //ha piazzato una chest
                if(!distanceCheck(issuer,placed_block)){
                    issuer.sendMessage("Solo casse singole! piazzale ad un blocco di distanza"
                    +"l'una dall'altra!!");
                    e.setCancelled(true);
                    return;
                }

                System.out.println("Player: " +issuer.getDisplayName()+ " ha tentato di piazzare una chest");
                int aux = Albatros.addChest2Player(issuer.getDisplayName(),placed_block);
                /** piazzo la chest nell'arraylist del player in questione
                 * aux mi ritorna -1 se ho superato il limite, altrimenti il numero di chest
                 * attualmente piazzate.
                 */

                int _MAXNUMCHEST = Albatros.get_MAXNUMCHEST();

                if(aux>0 && aux< _MAXNUMCHEST){
                    String message = "Chest confermata, te ne mancano: " + (_MAXNUMCHEST - aux);
                    issuer.sendMessage(message);
                }
                else if(aux == _MAXNUMCHEST){
                    String message = "Chest confermata, hai finito!";
                    issuer.sendMessage(message);
                    Albatros.removeExecutingPlayer(issuer.getDisplayName());
                }
                if (aux == -1){
                    e.setCancelled(true);
                    issuer.sendMessage("Limite chest raggiunto operazione terminata");
                    Albatros.removeExecutingPlayer(issuer.getDisplayName());
                }
            //}
        }
    }

    private boolean distanceCheck(Player player, Chest chest) {
        Location loc = chest.getLocation();
        BlockFace b;
        double nx = abs(loc.getX());
        double nz = abs(loc.getZ());

        ArrayList<Chest> listachests = Albatros.getOnePlayerChestMap(player.getDisplayName());
        if(listachests == null) { return true;}

        double ox;
        double oz;
        double xdist;
        double zdist;
      /*
        if(listachests!=null) {
            if (listachests.size() >= 1) {
                Chest r = listachests.get(0);
                b = chest.getBlock().getFace(r.getBlock());
                player.sendMessage("face: " + b.toString());
            }
        }*/

        for(Chest r : listachests){
            b  = chest.getBlock().getFace(r.getBlock());
            ox = r.getX();
            oz = r.getZ();
            xdist = abs(abs(ox)-abs(nx));
            zdist = abs(abs(oz)-abs(nz));
            if(b==null){
                player.sendMessage("abbastanza lontano");
                return true;
            }
            player.sendMessage(" ox: "+ox + "; nx: " + nx);
            player.sendMessage(" oz: "+oz + "; nz: " + nz);

            player.sendMessage(" xdist: " + xdist + "; zdist: " + zdist);
            player.sendMessage("facing: " + b.toString());
            if(b.toString().equals(BlockFace.SOUTH.toString()) || b.toString().equals(BlockFace.NORTH.toString())){
                player.sendMessage("NOrd o SUD if");
                //check on x axis
                if(xdist<2){return  false;}
            }
            else if(b== BlockFace.EAST || b == BlockFace.WEST ){
                //check on z axis
                player.sendMessage("Est o West if");
                if(zdist <2 ){return  false;}
            }
        }
        return true;

    }
}
