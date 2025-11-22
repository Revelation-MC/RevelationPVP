package org.mesmeralis.revelationPVP.Storage;

import org.bukkit.entity.Player;
import org.mesmeralis.revelationPVP.RevelationPVP;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLGetter {

    private RevelationPVP main;

    public SQLGetter(RevelationPVP main) {
        this.main = main;
        this.createTable();
    }

    public void createTable() {
        PreparedStatement ps;
        try {
            ps = main.storage.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS pvp "
                    + "(NAME VARCHAR(100), UUID VARCHAR(100), KILLS INT(100), DEATHS INT(100), WINS INT(100), POINTS INT(100), PRIMARY KEY(NAME))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Void> createPlayer(Player player) {
        return CompletableFuture.runAsync(() -> {
            try {
                UUID uuid = player.getUniqueId();
                if (!exists(uuid).join()) {
                    PreparedStatement ps2 = main.storage.getConnection().prepareStatement("INSERT IGNORE INTO pvp (NAME,UUID) VALUES (?,?)");
                    ps2.setString(1, player.getName());
                    ps2.setString(2, uuid.toString());
                    ps2.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Boolean> exists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("SELECT * FROM pvp WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet results = ps.executeQuery();
                return results.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public CompletableFuture<Void> addPoints(UUID uuid, int points) {
        this.main.map.compute(uuid, (uid, pointss) -> {
            pointss.addPoints(points);
            return pointss;
        });
        return CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("UPDATE pvp SET POINTS=? WHERE UUID =?");
                ps.setInt(1, getPoints(uuid).join() + points);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> addWins(UUID uuid, int wins) {
        this.main.map.compute(uuid, (uid, winss) -> {
            winss.addWins(wins);
            return winss;
        });
        return CompletableFuture.runAsync(() -> {
                    try {
                        PreparedStatement ps = main.storage.getConnection().prepareStatement("UPDATE pvp SET WINS=? WHERE UUID =?");
                        ps.setInt(1, getWins(uuid).join() + wins);
                        ps.setString(2, uuid.toString());
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public CompletableFuture<Void> addDeaths(UUID uuid, int deaths) {
        this.main.map.compute(uuid, (uid, deathss) -> {
            deathss.addDeaths(deaths);
            return deathss;
        });
        return CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("UPDATE pvp SET DEATHS=? WHERE UUID =?");
                ps.setInt(1, getDeaths(uuid).join() + deaths);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> addKills(UUID uuid, int kills) {
        this.main.map.compute(uuid, (uid, killss) -> {
            killss.addKills(kills);
            return killss;
        });
        return CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("UPDATE pvp SET KILLS=? WHERE UUID =?");
                ps.setInt(1, getKills(uuid).join() + kills);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Integer> getPoints(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("SELECT POINTS FROM pvp WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("POINTS");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    public CompletableFuture<Integer> getKills(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("SELECT KILLS FROM pvp WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("KILLS");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    public CompletableFuture<Integer> getDeaths(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("SELECT DEATHS FROM pvp WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("DEATHS");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    public CompletableFuture<Integer> getWins(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("SELECT WINS FROM pvp WHERE UUID=?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("WINS");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    public CompletableFuture<UUID> getTopWins() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("SELECT UUID FROM pvp ORDER BY WINS DESC LIMIT 1");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return UUID.fromString(rs.getString("UUID"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<UUID> getTopPoints() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement ps = main.storage.getConnection().prepareStatement("SELECT UUID FROM pvp ORDER BY POINTS DESC LIMIT 1");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return UUID.fromString(rs.getString("UUID"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
