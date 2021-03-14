package it.overlands.albatros;

import it.overlands.albatros.commands.CommandsHandler;
import it.overlands.albatros.database.MySql;
import it.overlands.albatros.listeners.BlockPlaceListener;
import it.overlands.albatros.listeners.ItemPlacedListener;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class Albatros extends JavaPlugin {
    private static Albatros instance;
    private static final int _MAXNUMCHEST = 5;

    //playerCheck tiene il conto di quante casse hanno claimato contiene
    private static HashMap<String, ArrayList<Chest>> playerChestsMap = new HashMap<>();
    // player che hanno lanciato il comando e stanno lavorando con le casse
    private static ArrayList<Player> executingPlayers = new ArrayList<>();

    /**
     * PROBLEMI DA CORREGERE
     * 1) nel map se io piazzo 5 chest me ne inserisce 4
     *
     *
     * ATTENZIONE
     * playerChestsMap da ora contiene il nome<String> e non il <Player> perchè al riavvio del plugin non si può recuperare il <Player> dal nome se questo è offline
     *
     * **/





    public static int get_MAXNUMCHEST() {return _MAXNUMCHEST;}
    public static Albatros getInstance(){return instance;}

    /***************** playerChest functions ******************************/
    public static HashMap<String, ArrayList<Chest>> getPlayerChestsMap(){return playerChestsMap;}
    public static void resetPlayerChestsMap(){playerChestsMap=new HashMap<>(); return;}

    public static Set<String> getPlayerList() {return playerChestsMap.keySet();}
    public  static int getSizeChestListofPlayer(Player p){
        if(playerChestsMap.containsKey(p.getName())){
            return playerChestsMap.get(p.getName()).size();
        }
        return -1;
    }
    public static ArrayList<Chest> getOnePlayerChestMap(Player p) {
        if (playerChestsMap.containsKey(p.getDisplayName())) {
            return playerChestsMap.get(p.getDisplayName());
        }
        return null;
    }

    public static int addChest2Player(String p,Chest chestID){
        //aggiunge la chest al player e ritorna il numero di chest attuali,
        // -1 se ha superato il limite
        if(playerChestsMap.containsKey(p)){
            //p è già dentro?
            if(playerChestsMap.get(p).size()<_MAXNUMCHEST){
                //p ha ancora chest da claimare?
                playerChestsMap.get(p).add(chestID);
                //caricamento dati chest sul db
                try {
                    PreparedStatement pstmt = MySql.c.prepareStatement(MySql.ADD_CHEST);
                    pstmt.setString (1, p);
                    pstmt.setString (2, chestID.getLocation().getWorld().getName());
                    pstmt.setDouble (3, chestID.getLocation().getX());
                    pstmt.setDouble (4, chestID.getLocation().getY());
                    pstmt.setDouble (5, chestID.getLocation().getZ());
                    pstmt.setFloat (6, chestID.getLocation().getPitch());
                    pstmt.setFloat (7, chestID.getLocation().getYaw());
                    pstmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return playerChestsMap.get(p).size();
            }
            else return -1;
        }
        else{
            //crea un nuovo array di chests e mette il nuovo player nella mappa
            ArrayList<Chest> aux = new ArrayList<>();
            aux.add(chestID);
            playerChestsMap.put(p,aux);
            return 1;
        }
    }

    public static void removeChests2Player(Player p){
        if(playerChestsMap.containsKey(p.getName())){
            playerChestsMap.put(p.getName(), new ArrayList<>());
            // cancella tutte le righe della tabella con il nome del player
            PreparedStatement pstmt = null;
            try {
                pstmt = MySql.c.prepareStatement(MySql.RESET_ALL_CHESTS);
                pstmt.setString (1, p.getName());
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
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
        instance = this;
        // Plugin startup logic
        this.getCommand("albatros").setExecutor(new CommandsHandler());
        PluginManager pmg = getServer().getPluginManager();
        /* Sezione Handlers & GuiEvents */
        pmg.registerEvents(new BlockPlaceListener(), this);
        pmg.registerEvents(new ItemPlacedListener(), this);
        //pmg.registerEvents(new ItemPlacedListener(), this);
        //CONNESSIONE AL DB
        MySql.openConnection();
        //CREAZIONE DELLE TABELLE SE NON ESISTONO
        MySql.loadDatabase();
        //carica playerChests con i dati sul db
        MySql.loadFields();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



}
