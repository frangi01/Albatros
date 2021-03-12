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



    private final static String DROP_TABLE = "DROP TABLE IF NOT EXISTS animals";


    private final static String CREATE_TABLE = "CREATE TABLE animals (\n" +
	"     id MEDIUMINT NOT NULL AUTO_INCREMENT,\n" +
	"     name CHAR(30) NOT NULL,\n" +
	"     PRIMARY KEY (id)\n" +
	");";
    private final static String INSERT = "INSERT INTO animals " + "(name) values (?)";
    private final static String SELECT = "SELECT * from animals";
    
    public static Connection c = null;
    private static Statement stmt;

    private static void selectDatabase(){
        //URL += DBNAME+"?";
    }

    public static void openConnection(){
        try{
            Properties properties = new Properties();
            properties.setProperty("user", USER);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("useSSL", "false");

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
        //Statement stmt = c.createStatement();
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

        } catch (SQLException e) {
            Logger.getLogger(Albatros.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
