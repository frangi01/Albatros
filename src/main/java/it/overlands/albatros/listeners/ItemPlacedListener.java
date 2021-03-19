package it.overlands.albatros.listeners;

import it.overlands.albatros.Albatros;
import it.overlands.albatros.database.MySql;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


import static it.overlands.albatros.database.MySql.*;
import static org.bukkit.Material.*;
import static org.bukkit.Registry.MATERIAL;


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

        if(!inv.getType().equals(InventoryType.CHEST)){
            return;
        }
        Chest chest = (Chest) inv.getHolder();

        if(Albatros.getPlayerList().contains(player.getDisplayName())){
            ArrayList<Chest> listachests= Albatros.getOnePlayerChestMap(player.getDisplayName());
    try{
        listachests.contains(chest);
    }catch (NullPointerException ex){
        return;
    }

    if(listachests.contains(chest)){
        System.out.println(player.getDisplayName() + " ha chiuso una cassa recistrata");
        //SALVA CIò CHE C'è DENTRO SUL DB
        PreparedStatement pstmt = null;
        try {
            pstmt = MySql.c.prepareStatement(GET_COUNTER_CHEST);//SELECT `counter` FROM `CHEST` WHERE `player` = ? AND `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?
            pstmt.setString(1,player.getName());
            pstmt.setString(2,e.getPlayer().getWorld().getName());
            pstmt.setDouble(3,chest.getX());
            pstmt.setDouble(4,chest.getY());
            pstmt.setDouble(5,chest.getZ());
            ResultSet rs = pstmt.executeQuery();
            int counter_chest = -1;
            while (rs.next()) { counter_chest = rs.getInt("counter"); }
                // recupero l'id della chest
                pstmt = MySql.c.prepareStatement(GET_ID_CHEST);//SELECT `id` FROM `CHEST` WHERE `player` = ? AND `world` = ? AND `counter` = ?
                pstmt.setString(1,player.getName());
                pstmt.setString(2,Albatros.oldWorld);
                pstmt.setInt(3,counter_chest);
                rs = pstmt.executeQuery();
                int id_chest = -1;
                while (rs.next()) { id_chest = rs.getInt("id"); }

                    //recupero l'inventario
                    //per ogni ItemStack devo recuperare AMOUNT - DURABILITY - ENCHANTMENT booelan - TYPE

                    pstmt = MySql.c.prepareStatement(MySql.DEL_ALL_ITEMS);
                    pstmt.setInt (1, id_chest);
                    pstmt.execute();

                    for (ItemStack i : chest.getInventory()) {
                        if (i == null) { continue;                        }
                        pstmt = MySql.c.prepareStatement(MySql.ADD_ITEM, Statement.RETURN_GENERATED_KEYS);
                        pstmt.setInt(1, i.getAmount());
                        if(i.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
                            org.bukkit.inventory.meta.Damageable d = (org.bukkit.inventory.meta.Damageable) i.getItemMeta();
                            pstmt.setInt(2, i.getType().getMaxDurability() - d.getDamage()); // ho perso 40min per cercare di capire come cazzo prendere questo valore in 1.16.5 ma non l'ho capito
                        }else{
                            pstmt.setInt(2, -1); // ho perso 40min per cercare di capire come cazzo prendere questo valore in 1.16.5 ma non l'ho capito
                            System.out.println("Oggetto non danneggiabile");
                        }
                        //pstmt.setShort(2, i.getDurability()); // ho perso 40min per cercare di capire come cazzo prendere questo valore in 1.16.5 ma non l'ho capito
                        pstmt.setBoolean(3, i.getItemMeta().hasEnchants());
                        pstmt.setString(4, i.getType().toString());
                        pstmt.setInt(5, id_chest);
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
                                pstmt.setString(1, en.getKey().getKey().replace("minecraft:",""));
                                pstmt.setInt(2, i.getEnchantmentLevel(Enchantment.getByKey(en.getKey())));
                                pstmt.setInt(3, inventory_id);
                                pstmt.setInt(4,-1);
                                pstmt.execute();
                            }
                        }
                        // se è una shulker
                        if (i.getItemMeta() instanceof BlockStateMeta) {
                            BlockStateMeta im = (BlockStateMeta) i.getItemMeta();
                            if (im.getBlockState() instanceof ShulkerBox) {
                                System.out.println(player.getDisplayName() +" ha chiuso una chest con dentro una shulker!");
                                ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                                System.out.println(player.getDisplayName()+" ha chiuso una chest con una shulker che contiene: ");
                                for(ItemStack is : shulker.getInventory()){
                                    if(is==null){continue;}
                                    pstmt = MySql.c.prepareStatement(MySql.ADD_SHULKER_ITEM,Statement.RETURN_GENERATED_KEYS);
                                    pstmt.setInt (1, is.getAmount());
                                    if(is.getItemMeta() instanceof  org.bukkit.inventory.meta.Damageable) {
                                        org.bukkit.inventory.meta.Damageable d = (org.bukkit.inventory.meta.Damageable) is.getItemMeta();
                                        pstmt.setInt(2, is.getType().getMaxDurability()-d.getDamage()); // ho perso 40min per cercare di capire come cazzo prendere questo valore in 1.16.5 ma non l'ho capito
                                    } else{
                                        pstmt.setInt(2, -1); // ho perso 40min per cercare di capire come cazzo prendere questo valore in 1.16.5 ma non l'ho capito
                                    }
                                    pstmt.setBoolean (3, is.getItemMeta().hasEnchants());
                                    pstmt.setString (4, is.getType().toString());
                                    pstmt.setInt (5, inventory_id);
                                    pstmt.setInt (6, id_chest);
                                    pstmt.execute();

                                    rs = pstmt.getGeneratedKeys();
                                    int shulker_id = 0;
                                    if (rs.next()) {
                                        shulker_id = rs.getInt(1);
                                    }
                                    System.out.println(is.getType());
                                    if (is.getItemMeta().hasEnchants()) {
                                        // recupero il map degli enchants
                                        Map<Enchantment, Integer> enchantments = is.getEnchantments();
                                        // per ogni enchant recupero il nome e il livello e lo carico sul db
                                        for (Enchantment en : enchantments.keySet()) {
                                            pstmt = MySql.c.prepareStatement(MySql.ADD_ENCHANTS);
                                            pstmt.setString(1, en.getKey().getKey().replace("minecraft:",""));
                                            pstmt.setInt(2, is.getEnchantmentLevel(Enchantment.getByKey(en.getKey())));
                                            pstmt.setInt(3, -1);
                                            pstmt.setInt(4,shulker_id);
                                            pstmt.execute();
                                            System.out.println("con "+en.getKey().toString() + is.getEnchantmentLevel(Enchantment.getByKey(en.getKey())));
                                        }
                                    }
                                }
                            }
                        }
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
        if(!(e.getPlayer() instanceof  Player)){ return; }
        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if(!inv.getType().equals(InventoryType.CHEST)){
            player.sendMessage("non hai aperto una chest...");
            return;
        }

        if(!Albatros.getPlayerList().contains(player.getDisplayName())){
            System.out.println(player.getDisplayName()+" non registrato nella listaplayer!");
            return;
        }


        Chest chest = (Chest) inv.getHolder();

        if(Albatros.getOnePlayerChestMap(player.getDisplayName())==null){return;}
        if(!Albatros.getOnePlayerChestMap(player.getDisplayName()).contains(chest)){
            System.out.println(player.getDisplayName()+", registrato ha cliccato su una cassa non registrata");
            return;
        }
        System.out.println(player.getDisplayName()+", registrato ha cliccato su una cassa");


        //da id_chest dalla posizione del blocco
        PreparedStatement pstmt = null;


        try {
            pstmt = c.prepareStatement(GET_COUNTER_CHEST);//SELECT `counter` FROM `CHEST` WHERE `player` = ? AND `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?
            pstmt.setString(1,player.getName());
            pstmt.setString(2,e.getPlayer().getWorld().getName());
            pstmt.setDouble(3,chest.getX());
            pstmt.setDouble(4,chest.getY());
            pstmt.setDouble(5,chest.getZ());
            ResultSet rs = pstmt.executeQuery();
            int counter_chest = -1;
            while (rs.next()) { counter_chest = rs.getInt("counter"); }

            //reset chest inventory
            chest.getInventory().clear();

            // recupero l'id della chest
            pstmt = c.prepareStatement(GET_ID_CHEST);//SELECT `id` FROM `CHEST` WHERE `player` = ? AND `world` = ? AND `counter` = ?
            pstmt.setString(1,player.getName());
            pstmt.setString(2,Albatros.oldWorld);
            pstmt.setInt(3,counter_chest);
            rs = pstmt.executeQuery();
            int id_chest = -1;
            while (rs.next()) { id_chest = rs.getInt("id"); }

            //recupero tutti i dati e li inserisco nella chest
            pstmt = c.prepareStatement(GET_ALL_ITEMSTACK);//SELECT `id` FROM `CHEST` WHERE `x` = ? AND `y` = ? AND `z` = ?
            pstmt.setInt(1,id_chest);
            rs = pstmt.executeQuery();

            /** per ogni itemstack****/
            while (rs.next()) {
                int id_item = rs.getInt("id");
                int amount = rs.getInt("amount");
                int durability = rs.getInt("durability");
                boolean enchantements = rs.getBoolean("enchantements");
                String type = rs.getString("type");
                int damage = getMaterial(type).getMaxDurability() - durability;

                if(Material.getMaterial(type) == null){
                    System.out.println("ITEMSTACK NELLA CHEST NON RICONOSCIUTO!");
                    return;
                }

                /************** se è una shulker **********************/
                if (type.contains("SHULKER")) {
                    System.out.println(" la chest aperta da "+ player.getDisplayName()+" contiene una shulker");

                    //creo un itemstack di shulker
                    ItemStack shulker_is = new ItemStack(Material.getMaterial(type));
                    BlockStateMeta bsm = (BlockStateMeta) shulker_is.getItemMeta();
                    ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();

                    pstmt = c.prepareStatement(GET_ALL_SHULKER_ITEMS);//SELECT * FROM `SHULKER` WHERE `item` = ? AND `chest` = ?
                    pstmt.setInt(1, id_item);
                    pstmt.setInt(2, id_chest);
                    ResultSet rs2 = pstmt.executeQuery();
                    System.out.println("OPEN: La shulker nella chest contiene: ");
                    // per ogni itemstack salvato nella shulker
                    while (rs2.next()) {
                        int sid_item = rs2.getInt("id");
                        int samount = rs2.getInt("amount");
                        int sdurability = rs2.getInt("durability");
                        boolean senchantements = rs2.getBoolean("enchantements");
                        String stype = rs2.getString("type");
                        //int sdamage = sdurability - Objects.requireNonNull(getMaterial(type)).getMaxDurability() ;

                        System.out.println(stype);
                        if(Material.getMaterial(type) ==null){
                            System.out.println("ITEMSTACK NELLA SHULKER NON RICONOSCIUTO!");
                            continue;
                        }

                        ItemStack is = new ItemStack(Material.getMaterial(stype),samount);
                        if(is.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
                            org.bukkit.inventory.meta.Damageable d = (org.bukkit.inventory.meta.Damageable) is.getItemMeta();
                            d.setDamage(sdurability);
                            is.setItemMeta((ItemMeta) d);
                        }

                        //.out.println("AAAAAAAAAAAAAAAAAAAAAAA "+sdamage);
                        //itemstack dell'oggetto nella shulker.

                        if (senchantements) {
                            player.sendMessage(stype +" ha i seguenti incantamenti: ");
                            pstmt = c.prepareStatement(GET_ALL_ENCHANTMENTS_FROM_SHULKER);//SELECT `id` FROM `CHEST` WHERE `x` = ? AND `y` = ? AND `z` = ?
                            pstmt.setInt(1, sid_item);
                            ResultSet rs3 = pstmt.executeQuery();
                            // per ogni enchantments
                            while (rs3.next()) {
                                String name_enchant = rs3.getString("name");
                                int level = rs3.getInt("level");

                                System.out.println(name_enchant);

                                //inserisci l'enchant all'itemstack
                                ItemMeta im = is.getItemMeta();
                                NamespacedKey key = NamespacedKey.minecraft(name_enchant);
                                if(Enchantment.getByKey(key)==null){
                                    System.out.println("ENCHANT"+ name_enchant +" NON TROVATO!");continue;
                                }
                                im.addEnchant(Enchantment.getByKey(key),level,false);
                                is.setItemMeta(im);
                                System.out.println(name_enchant);
                            }
                        }
                        shulker.getInventory().addItem(is);
                    }
                    bsm.setBlockState(shulker);
                    shulker.update();
                    shulker_is.setItemMeta(bsm);
                    chest.getInventory().addItem(shulker_is);
                } else {
                    /** NON é UNA SHULKER *****/
                   System.out.println("La chest contiene: "+Material.getMaterial(type));
                    // crea il nuovo Itemstack
                    ItemStack itemStack = new ItemStack(Material.getMaterial(type), amount, (short) damage);

                    //se ha enchantments allora recupera l'enchantmet dell'item in quella chest
                    if (enchantements) {
                        pstmt = MySql.c.prepareStatement(GET_ALL_ENCHANTMENTS_FROM_ITEMSTACK);//SELECT `id` FROM `CHEST` WHERE `x` = ? AND `y` = ? AND `z` = ?
                        pstmt.setInt(1, id_item);
                        ResultSet rs2 = pstmt.executeQuery();
                        System.out.println(type +" ha i seguenti incantamenti: ");

                        // per ogni enchantments
                        while (rs2.next()) {
                            String name_enchant = rs2.getString("name");
                            player.sendMessage(name_enchant);
                            int level = rs2.getInt("level");

                            System.out.println(name_enchant);

                            //inserisci l'enchant all'itemstack
                            ItemMeta im = itemStack.getItemMeta();
                            NamespacedKey key = NamespacedKey.minecraft(name_enchant.toLowerCase(Locale.ROOT));
                            if(Enchantment.getByKey(key)==null){
                               System.out.println("ENCHANT"+ name_enchant +" NON TROVATO!");continue;
                            }
                            im.addEnchant(Enchantment.getByKey(key),level,true);
                            itemStack.setItemMeta(im);
                        }
                    }
                    chest.getInventory().addItem(itemStack);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}