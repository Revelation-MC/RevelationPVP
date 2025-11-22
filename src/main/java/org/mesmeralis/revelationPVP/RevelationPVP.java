package org.mesmeralis.revelationPVP;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mesmeralis.revelationPVP.Listeners.PlayerDamageListener;
import org.mesmeralis.revelationPVP.Listeners.PlayerKillListener;
import org.mesmeralis.revelationPVP.Managers.KillManager;
import org.mesmeralis.revelationPVP.Managers.RankManager;
import org.mesmeralis.revelationPVP.Papi.PapiExpansion;
import org.mesmeralis.revelationPVP.Storage.SQLGetter;
import org.mesmeralis.revelationPVP.Storage.Storage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public final class RevelationPVP extends JavaPlugin {

    public Storage storage;
    public SQLGetter data;
    public KillManager manager = new KillManager();
    public RankManager rankManager;
    public HashMap<UUID, PapiExpansion.Record> map = new HashMap<>();
    public UUID topWins;
    public UUID topPoints;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.storage = new Storage(this);
        try {
            storage.connectSQL();
            Bukkit.getLogger().info("Database connected successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            Bukkit.getLogger().info("Database was unable to connect.");
        }
        this.data = new SQLGetter(this);
        this.rankManager = new RankManager(this);
        rankManager.loadRanks();
        this.initListeners();
        new PapiExpansion(this).register();
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            topWins = data.getTopWins().join();
            topPoints = data.getTopPoints().join();
        }, 0L,600L);
        for (Player player : Bukkit.getOnlinePlayers()) {
            final int points = this.data.getPoints(player.getUniqueId()).join();
            final int kills = this.data.getKills(player.getUniqueId()).join();
            final int wins = this.data.getWins(player.getUniqueId()).join();
            final int deaths = this.data.getDeaths(player.getUniqueId()).join();
            PapiExpansion.Record record = new PapiExpansion.Record(points, kills, deaths, wins);
            this.map.put(player.getUniqueId(), record);
        }
    }

    @Override
    public void onDisable() {

    }

    private void initListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerKillListener(this, manager), this);
    }

    private void registerCommands() {

    }
}
