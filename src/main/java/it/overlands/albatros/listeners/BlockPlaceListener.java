package it.overlands.albatros.listeners;


import it.overlands.albatros.Albatros;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
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
import static org.bukkit.block.data.type.Chest.Type.SINGLE;

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
        if(!ep.contains(issuer.getName())){return;}

        if(pb.getType().equals(Material.CHEST)){
            //se è nel mondo giusto
            Chest placed_block = (Chest) pb.getState();
            //if(e.getPlayer().getWorld().getName().equals("world")){
                //ha piazzato una chest

            if(!distanceCheck(issuer,placed_block)){
                    placed_block.setType(Material.CHEST);
                    issuer.sendMessage("Solo casse singole! piazzale ad un blocco di distanza"
                    +"l'una dall'altra!!");
                    e.setCancelled(true);
                    return;
                }
                // togli il  nick
                Albatros.getInstance().getServer().dispatchCommand(Albatros.getInstance().getServer().getConsoleSender(),"nick off "+issuer.getName());
                // ottieni la porta
                int server_name = issuer.getServer().getPort();

                int aux = Albatros.addChest2Player(issuer.getName(),placed_block, server_name);
                issuer.sendMessage("Server: "+ server_name);
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
                    Albatros.removeExecutingPlayer(issuer.getName());
                }
                if (aux == -1){
                    e.setCancelled(true);
                    issuer.sendMessage("Limite chest raggiunto operazione terminata");
                    Albatros.removeExecutingPlayer(issuer.getName());
                }
            //}
        }
    }

    private boolean distanceCheck(Player player, Chest chest) {
        System.out.println("a"+player.getWorld().getName());
        Location loc = chest.getLocation();
        double nx = loc.getX();
        double nz = loc.getZ();
        double ny = loc.getY();

        //check dei dintorni
        /**
         * blockxp: blocco x+1
         * blockxm: blocco x-1
         * blockzp: blocco x+1
         * blockzm: blocco x-1
         */

        Block blockxp = player.getWorld().getBlockAt((int)nx+1,(int)ny,(int) nz);
        Block blockxm = player.getWorld().getBlockAt((int)nx-1,(int)ny,(int) nz);
        Block blockzp = player.getWorld().getBlockAt((int)nx,(int)ny,(int) nz+1);
        Block blockzm = player.getWorld().getBlockAt((int)nx,(int)ny,(int) nz-1);


        if(blockxp!=null){
            System.out.println("non è un blocco null");
            if(blockxp.getType().equals(Material.CHEST)){
                //vicino ad una chest
                return false;
            }
        }
        if(blockxm!=null){
            if(blockxm.getType().equals(Material.CHEST)){
                //vicino ad una chest
                return false;
            }
        }
        if(blockzp!=null){
            if(blockzp.getType().equals(Material.CHEST)){
                //vicino ad una chest
                return false;
            }
        }
        if(blockzm!=null){
            if(blockzm.getType().equals(Material.CHEST)){
                //vicino ad una chest
                return false;
            }
        }
        return true;
    }
}
