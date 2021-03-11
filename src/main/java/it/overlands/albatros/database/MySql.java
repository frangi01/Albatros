package it.overlands.albatros.database;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySql {
    private static String URL = "jdbc:mysql://localhost:3306/";
    
    
    private final static String USER = "root";
    private final static String PASSWORD = "@6W9{Xb8";
    
    
    private final static String DBNAME = "albatros";
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver";

    private final static String CREATE_DATABASE = "CREATE DATABASE IF EXIST {DBNAME};";
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


    public static void loadDatabase() {
        //Statement stmt = c.createStatement();
        try {
            PreparedStatement pstmt = null;
            pstmt = c.prepareStatement(CREATE_DATABASE.replace("{DBNAME}",DBNAME));
            pstmt.execute();
            Logger.getLogger(MySql.class.getName()).log(Level.INFO, "Database creato o esistente");
            pstmt = c.prepareStatement(USE_DATABASE.replace("{DBNAME}",DBNAME));
            pstmt.execute();
            Logger.getLogger(MySql.class.getName()).log(Level.INFO, "Database selezionato");
        } catch (SQLException e) {
            Logger.getLogger(MySql.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    
    /*
    
    public static void main(String[] args) {
    //Qui il codice JDBC
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement stmt = conn.createStatement();
        PreparedStatement pstmt = conn.prepareStatement(INSERT);) {
                    
        } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(MySql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
