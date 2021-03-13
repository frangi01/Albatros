package it.overlands.albatros.listeners;

import it.overlands.albatros.Albatros;
import org.bukkit.Material;
import org.bukkit.Material.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;


public class ItemPlacedListener implements Listener {
    @EventHandler
    public void itemPlaced(InventoryMoveItemEvent e){
        System.out.println("DENTRO");
    //rileva lo spostamento di items tra inventari
        if(!(e.getInitiator().getHolder() instanceof Player)){return;}
        //se l'inventario di partenza non è di un Player ignora l'evento

        if(!(e.getDestination().getType()==InventoryType.CHEST)){return;}
        //se l'inventario di destinazione non è una chest ignora l'evento
        //questo if mi è costato 20 minuti mannaggia la cinciallegra

        Player player =(Player) e.getInitiator().getHolder();
        Block chest = (Block) e.getDestination().getHolder();
        HashMap<String, ArrayList<Block>> pcmap = Albatros.getInstance().getPlayerChestsMap();
        ItemStack item = e.getItem();

        if(pcmap.containsKey(player.getName())) {
            /** Il player è salvato nella lista, vuol dire che
             * ha selezionato delle casse migratorie.
             * Cerco se la cassa in cui è stata depositata roba
             * è una delle casse migratorie
             */
            ArrayList<Block> chestlist = pcmap.get(player.getName());


            for(Block b : chestlist){
                if(b.equals(chest)){
                    /** ho trovato la chest nell'arraylist del player in questione
                    *  posso quindi
                     */
                    //TODO recupero le coordinate del blocco CHEST in base a quelle recupero l'id della CHEST salvata sul db
                    chest.getLocation().getX();
                    chest.getLocation().getY();
                    chest.getLocation().getZ();
                    chest.getLocation().getPitch();
                    chest.getLocation().getYaw();
                    chest.getLocation().getWorld();



                    //TODO recupero l'inventario
                    //TODO per ogni ItemStack devo recuperare AMOUNT - DURABILITY - ENCHANTMENT booelan - TYPE

                    for(ItemStack i: e.getDestination()) {
                        System.out.println("Amount->"+i.getAmount()+"\nDurability->"+i.getDurability()+"\nType->"+i.getType()+"\nEnchant->"+i.getItemMeta().hasEnchants());
                        i.getAmount();
                        i.getDurability(); // ho perso 40min per cercare di capire come cazzo prendere questo valore in 1.16.5 ma non l'ho capito
                        i.getType();
                        // se ha un enchant
                        if(i.getItemMeta().hasEnchants()){
                            // recupero il map degli enchants
                            HashMap<Enchantment, Integer> enchantments = (HashMap<Enchantment, Integer>) i.getEnchantments();
                            // per ogni enchant recupero il nome e il livello
                            for(Enchantment en: enchantments.keySet()){
                                //TODO per ogni enchant aggiungi al db
                                en.getKey();
                                i.getEnchantmentLevel(Enchantment.getByKey(en.getKey()));
                            }
                        }

                        //TODO aggiungere gli elementi al db
                    }

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
