package it.overlands.albatros.listeners;

import it.overlands.albatros.Albatros;
import org.bukkit.Material;
import org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemPlacedListener implements Listener {

    public void itemPlaced(InventoryMoveItemEvent e){
    //rileva lo spostamento di items tra inventari

        if(!(e.getInitiator().getHolder() instanceof Player)){return;}
            /*se l'inventario di partenza non è di un Player ignora l'evento */

        if(!(e.getDestination().getType()==InventoryType.CHEST)){return;}
        //se l'inventario di destinazione non è una cassa ignora l'evento
        //questo if mi è costato 20 minuti mannaggia la cinciallegra

        Player player =(Player) e.getInitiator().getHolder();
        Block chest = (Block) e.getDestination().getHolder();
        HashMap<Player, ArrayList<Block>> pcmap = Albatros.getInstance().getPlayerChests();
        ItemStack item = e.getItem();

        if(pcmap.containsKey(player)) {
            /** Il player è salvato nella lista, vuol dire che
             * ha selezionato delle casse migratorie.
             * Cerco se la cassa in cui è stata depositata roba
             * è una delle casse migratorie
             */
            ArrayList<Block> chestlist = pcmap.get(player);


            for(Block b : chestlist){
                if(b.equals(chest)){
                    /** ho trovato la chest nell'arraylist del player in questione
                    *  posso quindi completare la transazione mettendo i nuovi materiali
                    *  nel database
                    */


                    //TODO aggiungere gli elementi al database
                    //TODO rimuovere l'itemstack dalla cassa

                    //l'itemstack deve essere salvato nel database e rimosso dalla chest!!


                }
                else{
                    /* è una cassa random, il player sta facendo altro
                     * non mi interessa e non faccio nulla */
                    return;
                }

            }

        }
    }

}
