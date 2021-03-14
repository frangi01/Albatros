package it.overlands.albatros.listeners;

import it.overlands.albatros.Albatros;
import it.overlands.albatros.database.MySql;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.block.Block;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;


import static it.overlands.albatros.database.MySql.GET_CHEST;


public class ItemPlacedListener implements Listener {

    /**
     * un listener per vedere se è stato spostato qualcosa
     * da un inventario ad un altro
     */
    @EventHandler
    public void inv(InventoryCloseEvent e){
        if(!(e.getPlayer() instanceof  Player)){return;}
        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();

        //e.getPlayer().sendMessage("evento triggerato, hai chiuso un inventario");
        if(!inv.getType().equals(InventoryType.CHEST)){
            player.sendMessage("non hai chiuso una chest...");
            return;
        }


        if(Albatros.getPlayerList().contains(player.getDisplayName())){
            ArrayList<Chest> listachests= Albatros.getOnePlayerChestMap(player);

            if(listachests.contains((Chest) inv.getHolder())){
                player.sendMessage("hai chiuso una cassa registrata!");
            }
            else{
                player.sendMessage("non hai chiuso una cassa registrata...");
            }


        }
        else{
            player.sendMessage("non sei nella lista player!");

        }



    }









    public void inv(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        try{
            Objects.requireNonNull(e.getClickedInventory());
        }
        catch(NullPointerException exception){
            e.getWhoClicked().sendMessage("hai buttato la roba in mano per terra o fuori dalla gui");
            return;
        }

        if (!(e.getClickedInventory().getHolder() instanceof Player)) {
            return;
        }
        //un Player ha cliccato sul suo inventario

        Player player = (Player) e.getWhoClicked();

        switch (e.getAction().toString()) {
            case "PICKUP_ALL":
                /*il player ha cliccato tutto lo stack e adesso è sul cursore*/
                //TODO implementare
                player.sendMessage("hai raccolto tutto l'ItemStack sul cursore");
                break;//funge
            case "PICKUP_HALF":
                /*il player ha cliccato metà stack (tasto destro) e adesso è sul cursore*/
                //TODO implementare
                if (e.isRightClick()) {
                    player.sendMessage("hai raccolto sul cursore metà ItemStack");
                }
                break;//funge
            case "COLLECT_TO_CURSOR":
                /* il player ha cliccato due volte su un item, ha raccolto il materiale
                sparso e completato uno stack in mano (raccoglie anche dalla destinazione!!)
                 */
                //TODO implementare
                player.sendMessage("hai cliccato due volte e raccolto uno stack sul cursore");
                break;//funge
            /*
                    i place funzionano solo nella gui del player

*/
            case "PLACE_ALL":
                //TODO fare il check se ha buttato per terra o nel suo inv e implementare
                player.sendMessage("hai droppato dal cursore tutto quello che avevi raccolto sul cursore");
                break;
            case "PLACE_ONE":
                //TODO fare il check se ha buttato per terra o nel suo inv e implementare
                player.sendMessage("hai droppato dal cursore un item di quello che avevi raccolto");
                break;
            case "PLACE_SOME":
                //TODO fare il check se ha buttato per terra o nel suo inv e implementare
                player.sendMessage("hai droppato dal cursore un pò di items");

            default:

                break;
        }

        if(e.getClickedInventory().getType() == InventoryType.CHEST){
            System.out.println("chesta");
        }

        if(e.isShiftClick()){
            player.sendMessage("hai trasferito tutto lo stack premendo shift");
        }




            HashMap<String, ArrayList<Chest>> pcmap = Albatros.getPlayerChestsMap();
            ItemStack item = e.getCurrentItem();

/*
            if(pcmap.containsKey(player.getName())) {
                /* Il player è salvato nella lista, vuol dire che
                 * ha selezionato delle casse migratorie.
                 * Cerco se la cassa in cui è stata depositata roba
                 * è una delle casse migratorie

                ArrayList<Block> chestlist = pcmap.get(player.getName());
                for (Block b : chestlist) {
                    if (b.equals(chest)) {
                        // ho trovato la chest nell'arraylist del player in questione posso quindi
                        //recupero le coordinate del blocco CHEST in base a quelle recupero l'id della CHEST salvata sul db

                    /*chest.getLocation().getY();
                    chest.getLocation().getZ();
                    chest.getLocation().getPitch();
                    chest.getLocation().getYaw();
                    chest.getLocation().getWorld();

                        PreparedStatement pstmt = null;
                        try {
                            pstmt = MySql.c.prepareStatement(GET_CHEST);//SELECT `id` FROM `CHEST` WHERE `x` = ? AND `y` = ? AND `z` = ?
                            pstmt.setDouble(1,chest.getLocation().getBlockX());
                            pstmt.setDouble(2,chest.getLocation().getBlockY());
                            pstmt.setDouble(3,chest.getLocation().getBlockZ());
                            ResultSet rs = pstmt.executeQuery();
                            int chest_id = 0;
                            while (rs.next()) {
                                chest_id = rs.getInt("id");
                            }
                            //recupero l'inventario
                            //per ogni ItemStack devo recuperare AMOUNT - DURABILITY - ENCHANTMENT booelan - TYPE
                            for (ItemStack i : e.getClickedInventory()) {
                                //System.out.println("Amount->" + i.getAmount() + "\nDurability->" + i.getDurability() + "\nType->" + i.getType() + "\nEnchant->" + i.getItemMeta().hasEnchants());
                                pstmt = MySql.c.prepareStatement(MySql.RELOAD_ITEMS);
                                pstmt.setInt (1, i.getAmount());
                                pstmt.setShort (2, i.getDurability()); //// ho perso 40min per cercare di capire come cazzo prendere questo valore in 1.16.5 ma non l'ho capito
                                pstmt.setBoolean (3, i.getItemMeta().hasEnchants());
                                pstmt.setString (4, i.getType().toString());
                                pstmt.setInt (5, chest_id);
                                pstmt.execute();

                                rs = pstmt.getGeneratedKeys();
                                int inventory_id = 0;
                                if(rs.next()){
                                    inventory_id = rs.getInt(1);
                                }
                                // se ha un enchant
                                if (i.getItemMeta().hasEnchants()) {
                                    // recupero il map degli enchants
                                    HashMap<Enchantment, Integer> enchantments = (HashMap<Enchantment, Integer>) i.getEnchantments();
                                    // per ogni enchant recupero il nome e il livello e lo carico sul db
                                    for (Enchantment en : enchantments.keySet()) {
                                        pstmt = MySql.c.prepareStatement(MySql.ADD_ENCHANTS);
                                        pstmt.setString (1, en.getKey().toString());
                                        pstmt.setInt (1, i.getEnchantmentLevel(Enchantment.getByKey(en.getKey())));
                                        pstmt.setInt (2, inventory_id);
                                        pstmt.execute();
                                    }
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }



                    } else {
                        // è una cassa random, il player sta facendo altro non mi interessa e non faccio nulla
                        return;
                    }
                }
            }*/
    }
}