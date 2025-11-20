package me.pintoadmin.pintoRecipes;

import java.sql.*;
import java.util.*;

public class SQLiteManager {
    private final PintoRecipes plugin;
    
    public SQLiteManager(PintoRecipes plugin) {
        this.plugin = plugin;
    }
    Connection connection;

    public void init() {
        connection = null;
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Establish a connection to the database (creates a new file if it doesn't exist)
            String url = "jdbc:sqlite:%s/crafts.db".formatted(plugin.getDataFolder().getAbsolutePath());
            connection = DriverManager.getConnection(url);

            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS crafts (uuid TEXT PRIMARY KEY);");
            ps.execute();

            plugin.getLogger().info("Connection to SQLite established.");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            plugin.getLogger().severe("Error connecting to SQLite: " + e.getMessage());
        }
    }

    public void addColumns(){
        getConnection();
        for(String recipeName : plugin.getConfigLoader().recipes) {
            try {
                Statement statement = connection.createStatement();
                boolean columnExists = false;
                ResultSet rs = statement.executeQuery("PRAGMA table_info(crafts);");
                while (rs.next()) {
                    if (rs.getString("name").equalsIgnoreCase(recipeName)) {
                        columnExists = true;
                        break;
                    }
                }
                rs.close();

                if (!columnExists) {
                    PreparedStatement tablePS = connection.prepareStatement("ALTER TABLE crafts ADD COLUMN "+recipeName+" INTEGER NOT NULL DEFAULT 0;");
                    tablePS.execute();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error adding column "+recipeName+" to database: " + e.getMessage());
            }
        }
    }

    public int getServerCrafts(String recipeName){
        getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT "+recipeName+" FROM crafts");
            ResultSet rs = ps.executeQuery();

            int totalCrafts = 0;
            while (rs.next()){
                if(rs.getInt(recipeName) > 0) totalCrafts += rs.getInt(recipeName);
            }
            rs.close();
            return totalCrafts;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error querying column "+recipeName+" in database: " + e.getMessage());
            return 0;
        }
    }
    public int getPlayerCrafts(String recipeName, UUID uuid){
        getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT "+recipeName+" FROM crafts WHERE uuid = ?;");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getInt(recipeName);
            } else return 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error querying column "+recipeName+" in database: " + e.getMessage());
            return 0;
        }
    }
    public void incrementPlayerCrafts(String recipeName, UUID uuid){
        getConnection();
        try {
            if(!columnExists(recipeName)) return;
            PreparedStatement ps = connection.prepareStatement("INSERT INTO crafts (uuid, "+recipeName+") VALUES (?, 1) " +
                    "ON CONFLICT(uuid) DO UPDATE SET "+recipeName+" = "+recipeName+" + 1;");
            ps.setString(1, uuid.toString());

            ps.executeUpdate();
        } catch (SQLException e){
            plugin.getLogger().severe("Error updating column "+recipeName+" in database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean columnExists(String name){
        try {
            Statement statement = connection.createStatement();
            boolean columnExists = false;
            ResultSet rs = statement.executeQuery("PRAGMA table_info(crafts);");
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(name)) {
                    columnExists = true;
                    break;
                }
            }
            rs.close();
            return columnExists;
        } catch (SQLException e){
            plugin.getLogger().severe("Error checking if column exists: "+e.getMessage());
            return false;
        }
    }

    public void deinit(){
        if(connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing SQLite connection: " + e.getMessage());
        }
    }
    public Connection getConnection(){
        if (connection == null) {
            init();
        }
        return connection;
    }
}
