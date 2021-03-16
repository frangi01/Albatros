package it.overlands.albatros.listeners;

import it.overlands.albatros.Albatros;
import it.overlands.albatros.database.MySql;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


import static it.overlands.albatros.database.MySql.GET_CHEST;
import static org.bukkit.Material.*;


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
        Chest chest = (Chest) inv.getHolder();

        if(Albatros.getPlayerList().contains(player.getDisplayName())){
            ArrayList<Chest> listachests= Albatros.getOnePlayerChestMap(player.getDisplayName());

            if(listachests.contains(chest)){
                player.sendMessage("hai chiuso una cassa registrata!");
                //SALVA CIò CHE C'è DENTRO SUL DB
                PreparedStatement pstmt = null;
                try {
                    pstmt = MySql.c.prepareStatement(GET_CHEST);
                    pstmt.setDouble(1,chest.getLocation().getBlockX());
                    pstmt.setDouble(2,chest.getLocation().getBlockY());
                    pstmt.setDouble(3,chest.getLocation().getBlockZ());
                    ResultSet rs = pstmt.executeQuery();
                    int chest_id = 0;
                    while (rs.next()) {
                        chest_id = rs.getInt("id");
                    }
                    System.out.println("sto per entrare nel loop");
                    //int aux = 0;
                    //recupero l'inventario
                    //per ogni ItemStack devo recuperare AMOUNT - DURABILITY - ENCHANTMENT booelan - TYPE

                    pstmt = MySql.c.prepareStatement(MySql.DEL_ALL_ITEMS);
                    pstmt.setInt (1, chest_id);
                    pstmt.execute();

                    for (ItemStack i : chest.getInventory()) {
                        if (i == null) { continue;                        }
                        pstmt = MySql.c.prepareStatement(MySql.ADD_ITEM, Statement.RETURN_GENERATED_KEYS);
                        pstmt.setInt(1, i.getAmount());
                        pstmt.setShort(2, i.getDurability()); // ho perso 40min per cercare di capire come cazzo prendere questo valore in 1.16.5 ma non l'ho capito
                        pstmt.setBoolean(3, i.getItemMeta().hasEnchants());
                        pstmt.setString(4, i.getType().toString());
                        pstmt.setInt(5, chest_id);
                        pstmt.execute();

                        rs = pstmt.getGeneratedKeys();
                        int inventory_id = 0;
                        if (rs.next()) {
                            inventory_id = rs.getInt(1);
                        }
                        // se ha un enchant
                        if (i.getItemMeta().hasEnchants()) {
                            // recupero il map degli enchants
                            Map<Enchantment, Integer> enchantments = i.getEnchantments();
                            // per ogni enchant recupero il nome e il livello e lo carico sul db
                            for (Enchantment en : enchantments.keySet()) {
                                pstmt = MySql.c.prepareStatement(MySql.ADD_ENCHANTS);
                                pstmt.setString(1, en.getKey().toString());
                                pstmt.setInt(2, i.getEnchantmentLevel(Enchantment.getByKey(en.getKey())));
                                pstmt.setInt(3, inventory_id);
                                pstmt.execute();
                            }
                        }
                        // se è una shulker
                        if (i.getItemMeta() instanceof BlockStateMeta) {
                            BlockStateMeta im = (BlockStateMeta) i.getItemMeta();
                            if (im.getBlockState() instanceof ShulkerBox) {
                                player.sendMessage("è una shulker!");
                                ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                                for(ItemStack is : shulker.getInventory()){
                                    if(is==null){continue;}
                                    player.sendMessage("la shulker ha qualcosa dentro!");
                                }


                            }
                            //aux++;
                            //continue;
                        }
                        //System.out.println("Amount->" + i.getAmount() + "\nDurability->" + i.getDurability() + "\nType->" + i.getType() + "\nEnchant->" + i.getItemMeta().hasEnchants());
                        /*
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
                        }*/
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            else{
                player.sendMessage("non hai chiuso una cassa registrata...");
            }
        }
        else{
            player.sendMessage("non sei nella lista player!");
        }
    }


    @EventHandler
    public void inv(InventoryOpenEvent e) {
        if(!(e.getPlayer() instanceof  Player)){return;}
        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();

        if(!inv.getType().equals(InventoryType.CHEST)){
            player.sendMessage("non hai aperto una chest...");
            return;
        }
        Chest chest = (Chest) inv.getHolder();

        if(!Albatros.getPlayerList().contains(player.getDisplayName())){
            player.sendMessage("player non presente nella lista dei registrati");
        }
        if(!Albatros.getOnePlayerChestMap(player.getDisplayName()).contains(chest)){
            player.sendMessage("chest non registrata!");
        }
        //l'utente c'è, la chest pure, scarichiamo dal DB!



    }
}