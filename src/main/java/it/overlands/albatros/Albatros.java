package it.overlands.albatros;

import it.overlands.albatros.commands.CommandsHandler;
import it.overlands.albatros.database.MySql;
import it.overlands.albatros.listeners.BlockPlaceListener;
import it.overlands.albatros.listeners.ItemPlacedListener;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class Albatros extends JavaPlugin {
    private static Albatros instance;
    private static final int _MAXNUMCHEST = 10;

    //playerCheck tiene il conto di quante casse hanno claimato
    private static HashMap<Player, ArrayList<Block>> playerChestsMap = new HashMap<Player, ArrayList<Block>>();
    // player che hanno lanciato il comando e stanno lavorando con le casse
    private static ArrayList<Player> executingPlayers = new ArrayList<>();





    public static int get_MAXNUMCHEST() {return _MAXNUMCHEST;}
    public static Albatros getInstance(){return instance;}

    /***************** playerChest functions ******************************/
    public static HashMap<Player, ArrayList<Block>> getPlayerChestsMap(){return playerChestsMap;}
    public static Set<Player> getPlayerList() {return playerChestsMap.keySet();}
    public  static int getSizeChestListofPlayer(Player p){
        if(playerChestsMap.containsKey(p)){
            return playerChestsMap.get(p).size();
        }
        return -1;
    }
    public static ArrayList<Block> getOnePlayerChestMap(Player p) {
        if (playerChestsMap.containsKey(p)) {
            return playerChestsMap.get(p);
        }
        return null;
    }

    public static int addChest2Player(Player p,Block chestID){
        //aggiunge la chest al player e ritorna il numero di chest attuali,
        // -1 se ha superato il limite
        if(playerChestsMap.containsKey(p)){
            //p è già dentro?
            if(playerChestsMap.get(p).size()<_MAXNUMCHEST){
                //p ha ancora chest da claimare?
                playerChestsMap.get(p).add(chestID);
                //TODO AGGIUNGI AL DB
                return playerChestsMap.get(p).size();
            }
            else return -1;
        }
        else{
            //crea un nuovo array di chests e mette il nuovo player nella mappa
            ArrayList<Block> aux = new ArrayList<>();
            aux.add(chestID);
            playerChestsMap.put(p,aux);
            return 1;
        }
    }

    public static void removeChests2Player(Player p){
        if(playerChestsMap.containsKey(p)){ playerChestsMap.put(p, new ArrayList<>());}
    }

    /************ executing player functions: ************/
    public static ArrayList<Player> getExecutingPlayers() {return executingPlayers;}

    public static void addExecutingPlayer(Player player){
        if(!executingPlayers.contains(player)){executingPlayers.add(player);}
    }
    public static  void removeExecutingPlayer(Player player){
        if(!executingPlayers.contains(player)){executingPlayers.remove(player);}
    }

    /*************************************/

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("albatros").setExecutor(new CommandsHandler());
        PluginManager pmg = getServer().getPluginManager();
        /* Sezione Handlers & GuiEvents */
        pmg.registerEvents(new BlockPlaceListener(), this);
        pmg.registerEvents(new ItemPlacedListener(), this);
        //TODO CONNESSIONE AL DB
        MySql.openConnection();
        MySql.loadDatabase();
            //TODO CREAZIONE DELLE TABELLE SE NON ESISTONO
            //TODO SE ESISTONO CARICA playerChests E executingPlayers CON I DATI SUL DB

        


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



}
