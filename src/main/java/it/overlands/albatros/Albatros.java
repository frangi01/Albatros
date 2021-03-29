package it.overlands.albatros;

import it.overlands.albatros.commands.CommandsHandler;
import it.overlands.albatros.database.MySql;
import it.overlands.albatros.listeners.BlockPlaceListener;
import it.overlands.albatros.listeners.ItemPlacedListener;
import org.bukkit.block.Chest;
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
    private static ArrayList<String> executingPlayers = new ArrayList<>();

    public static int get_MAXNUMCHEST() {return _MAXNUMCHEST;}
    public static Albatros getInstance(){return instance;}

    public static final String oldWorld = "magazzino";
    public static final String newWorld = "world";

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


    public static ArrayList<Chest> getOnePlayerChestMap(String player) {
        if (playerChestsMap.containsKey(player)) {
            return playerChestsMap.get(player);
        }
        return null;
    }

    public static int addChest2Player(String p,Chest chest){
        //aggiunge la chest al player e ritorna il numero di chest attuali,
        // -1 se ha superato il limite
        if(playerChestsMap.containsKey(p)){
            //p è già dentro?
            if(playerChestsMap.get(p).size()<_MAXNUMCHEST){
                //p ha ancora chest da claimare?
                playerChestsMap.get(p).add(chest);
                //caricamento dati chest sul db
                try {
                    PreparedStatement pstmt = MySql.c.prepareStatement(MySql.ADD_CHEST);
                    pstmt.setString (1, p);
                    pstmt.setString (2, chest.getLocation().getWorld().getName());
                    pstmt.setDouble (3, chest.getLocation().getX());
                    pstmt.setDouble (4, chest.getLocation().getY());
                    pstmt.setDouble (5, chest.getLocation().getZ());
                    pstmt.setFloat (6, chest.getLocation().getPitch());
                    pstmt.setFloat (7, chest.getLocation().getYaw());
                    pstmt.setInt(8,playerChestsMap.get(p).size());
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
            aux.add(chest);
            playerChestsMap.put(p,aux);
            try {
                PreparedStatement pstmt = MySql.c.prepareStatement(MySql.ADD_CHEST);
                pstmt.setString (1, p);
                pstmt.setString (2, chest.getLocation().getWorld().getName());
                pstmt.setDouble (3, chest.getLocation().getX());
                pstmt.setDouble (4, chest.getLocation().getY());
                pstmt.setDouble (5, chest.getLocation().getZ());
                pstmt.setFloat (6, chest.getLocation().getPitch());
                pstmt.setFloat (7, chest.getLocation().getYaw());
                pstmt.setInt (8, 1);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
    public static ArrayList<String> getExecutingPlayers() {return executingPlayers;}

    public static void addExecutingPlayer(String player){
        if(!executingPlayers.contains(player)){executingPlayers.add(player);}
    }
    public static  void removeExecutingPlayer(String player){
        if(executingPlayers.contains(player)){executingPlayers.remove(player);}
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
        //MySql.loadFields();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



}
