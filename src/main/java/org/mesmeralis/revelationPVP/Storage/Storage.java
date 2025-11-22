package org.mesmeralis.revelationPVP.Storage;

import org.bukkit.Bukkit;
import org.mesmeralis.revelationPVP.RevelationPVP;
import org.mesmeralis.revelationPVP.Roles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class Storage {

    RevelationPVP main;
    public Storage(RevelationPVP main) {
        this.main = main;
    }

    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connectSQL() throws ClassNotFoundException, SQLException {
        String host = main.getConfig().getString("host");
        int port = main.getConfig().getInt("port");
        String database = main.getConfig().getString("database");
        String user = main.getConfig().getString("username");
        String pass = main.getConfig().getString("password");
        if(!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", user, pass);
        }
    }

    public void disconnectSQL() {
        if(isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                Bukkit.getLogger().info("Could not disconnect from the database.");
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }


}
