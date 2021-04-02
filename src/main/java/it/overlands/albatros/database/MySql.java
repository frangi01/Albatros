package it.overlands.albatros.database;

import it.overlands.albatros.Albatros;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySql {
    private static String URL = "jdbc:mysql://localhost:3306/";

    private final static String USER = "root";
    private final static String PASSWORD = "@6W9{Xb8";
    
    
    private final static String DBNAME = "Albatros";
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver";

    private final static String CREATE_DATABASE = "CREATE DATABASE {DBNAME};";
    private final static String USE_DATABASE = "USE {DBNAME};";

    private final static String CREATE_TABLE_CHEST = "CREATE TABLE IF NOT EXISTS `CHEST` (\n" +
            "  `id` int NOT NULL AUTO_INCREMENT,\n" +
            "  `player` varchar(100) NOT NULL,\n" +
            "  `world` varchar(100) NOT NULL,\n" +
            "  `x` double NOT NULL,\n" +
            "  `y` double NOT NULL,\n" +
            "  `z` double NOT NULL,\n" +
            "  `pitch` double NOT NULL,\n" +
            "  `yaw` double NOT NULL,\n" +
            "  `counter` int NOT NULL,\n" +
            "   PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

    private final static String CREATE_TABLE_ENCHANTMENTS = "CREATE TABLE IF NOT EXISTS `ENCHANTMENTS` (\n" +
            "  `id` int NOT NULL AUTO_INCREMENT,\n" +
            "  `name` varchar(100) NOT NULL,\n" +
            "  `level` int NOT NULL,\n" +
            "  `itemstack` int NOT NULL,\n" +
            "  `shulker` int NOT NULL," +
            "   PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

    private final static String CREATE_TABLE_ITEMSTACK = "CREATE TABLE IF NOT EXISTS `ITEMSTACK` (\n" +
            "  `id` int NOT NULL AUTO_INCREMENT,\n" +
            "  `amount` int NOT NULL,\n" +
            "  `durability` int NOT NULL,\n" +
            "  `enchantements` tinyint(1) NOT NULL DEFAULT '0',\n" +
            "  `type` varchar(100) NOT NULL,\n" +
            "  `chest` int NOT NULL," +
            "   PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

    private final static String CREATE_TABLE_SHULKER = "CREATE TABLE IF NOT EXISTS `SHULKER` (\n" +
            "  `id` int NOT NULL AUTO_INCREMENT,\n" +
            "  `amount` int NOT NULL,\n" +
            "  `durability` int NOT NULL,\n" +
            "  `enchantements` tinyint(1) NOT NULL DEFAULT '0',\n" +
            "  `type` varchar(100) NOT NULL,\n" +
            "  `item` int NOT NULL,\n" +
            "  `chest` int NOT NULL," +
            "   PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";


    public final static String ADD_CHEST = "INSERT INTO `CHEST`(`id`, `player`, `world`, `x`, `y`, `z`, `pitch`, `yaw`,`counter`) VALUES (NULL,?,?,?,?,?,?,?,?)";
    public final static String ADD_ITEM = "INSERT INTO `ITEMSTACK`(`id`, `amount`, `durability`, `enchantements`, `type`, `chest`) VALUES (NULL,?,?,?,?,?)";
    public final static String ADD_ENCHANTS = "INSERT INTO `ENCHANTMENTS`(`id`, `name`, `level`, `itemstack`, `shulker`) VALUES (NULL,?,?,?,?)";
    public final static String ADD_SHULKER_ITEM = "INSERT INTO `SHULKER`(`id`, `amount`, `durability`, `enchantements`, `type`, `item`, `chest`) VALUES (NULL,?,?,?,?,?,?)";
    public final static String RESET_ALL_CHESTS = "DELETE FROM `CHEST` WHERE `player` = ?";
    public final static String DEL_ALL_ITEMS = "DELETE FROM `ITEMSTACK` WHERE `chest` = ?";
    public final static String DEL_ALL_SHULKER = "DELETE FROM `SHULKER` WHERE `chest` = ?";
    public final static String DEL_ALL_ECHATMENTS = "SELECT * FROM `ENCHANTMENTS` WHERE `itemstack` = ? OR `shulker` = ?";

    private final static String GET_ALL_CHESTS = "SELECT * FROM `CHEST`";
    public final static String GET_COUNTER_CHEST = "SELECT `counter`,`player` FROM `CHEST` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?";

    public final static String GET_ID_CHEST = "SELECT `id` FROM `CHEST` WHERE `player` = ? AND `world` = ? AND `counter` = ?";

    public final static String GET_ALL_ITEMSTACK = "SELECT * FROM `ITEMSTACK` WHERE `chest`=?";
    public final static String GET_ALL_SHULKER_ITEMS = "SELECT * FROM `SHULKER` WHERE `item` = ? AND `chest` = ?";
    public final static String GET_ALL_ENCHANTMENTS_FROM_ITEMSTACK = "SELECT * FROM `ENCHANTMENTS` WHERE `itemstack` = ? AND `shulker` = -1";
    public final static String GET_ALL_ENCHANTMENTS_FROM_SHULKER = "SELECT * FROM `ENCHANTMENTS` WHERE `shulker` = ? AND `itemstack` = -1";

    
    public static Connection c = null;

    public static void openConnection(){
        try{
            Properties properties = new Properties();
            properties.setProperty("user", USER);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");

            c = DriverManager.getConnection(URL,properties);
        }catch(SQLException ex){
            Logger.getLogger(MySql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean checkDatabase(){
        /* recupera tutti database e torna vero se trova nella lista il DBNAME */
        ResultSet rs = null;
        try {
            rs = c.getMetaData().getCatalogs();
            while(rs.next())
                if(DBNAME.equals(rs.getString(1))) return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }


    public static void loadDatabase() {
        try {
            PreparedStatement pstmt = null;
            // se il database non esiste lo crea
            if(!checkDatabase()){
                pstmt = c.prepareStatement(CREATE_DATABASE.replace("{DBNAME}",DBNAME));
                pstmt.execute();
                Logger.getLogger(MySql.class.getName()).log(Level.INFO, "Database creato");
            }
            // seleziona il database da usare
            pstmt = c.prepareStatement(USE_DATABASE.replace("{DBNAME}",DBNAME));
            pstmt.execute();
            Logger.getLogger(MySql.class.getName()).log(Level.INFO, "Database selezionato");
            // cotrolla se ci sono le tabelle e se non ci sono creale

            pstmt = c.prepareStatement(CREATE_TABLE_CHEST);
            pstmt.execute();
            Logger.getLogger(MySql.class.getName()).log(Level.INFO, "Tabella CHEST caricata");


            pstmt = c.prepareStatement(CREATE_TABLE_ITEMSTACK);
            pstmt.execute();
            Logger.getLogger(MySql.class.getName()).log(Level.INFO, "Tabella ITEMSTACK caricata");

            pstmt = c.prepareStatement(CREATE_TABLE_SHULKER);
            pstmt.execute();
            Logger.getLogger(MySql.class.getName()).log(Level.INFO, "Tabella SHULKER caricata");


            pstmt = c.prepareStatement(CREATE_TABLE_ENCHANTMENTS);
            pstmt.execute();
            Logger.getLogger(MySql.class.getName()).log(Level.INFO, "Tabella ENCHANTMENTS caricata");

        } catch (SQLException e) {
            Logger.getLogger(Albatros.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
