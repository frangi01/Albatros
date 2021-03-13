package it.overlands.albatros.listeners;

import it.overlands.albatros.Albatros;

import it.overlands.albatros.database.MySql;
import org.bukkit.block.Block;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


import static it.overlands.albatros.database.MySql.GET_CHEST;


public class ItemPlacedListener implements Listener {

    @EventHandler
    public void inv(InventoryClickEvent e){
        //se l'inventario non è una chest
        if(e.getClickedInventory().getType() != InventoryType.CHEST){return;}

        Player player =(Player) e.getWhoClicked();
        Block chest = (Block) e.getView().getTopInventory().getHolder();
        HashMap<String, ArrayList<Block>> pcmap = Albatros.getInstance().getPlayerChestsMap();
        ItemStack item = e.getCurrentItem();


        if(pcmap.containsKey(player.getName())) {
            /* Il player è salvato nella lista, vuol dire che
             * ha selezionato delle casse migratorie.
             * Cerco se la cassa in cui è stata depositata roba
             * è una delle casse migratorie
             */
            ArrayList<Block> chestlist = pcmap.get(player.getName());
            for (Block b : chestlist) {
                if (b.equals(chest)) {
                    // ho trovato la chest nell'arraylist del player in questione posso quindi
                    //recupero le coordinate del blocco CHEST in base a quelle recupero l'id della CHEST salvata sul db

                    /*chest.getLocation().getY();
                    chest.getLocation().getZ();
                    chest.getLocation().getPitch();
                    chest.getLocation().getYaw();
                    chest.getLocation().getWorld();*/

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
        }
    }
}
