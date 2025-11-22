package org.mesmeralis.revelationPVP;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mesmeralis.revelationPVP.Commands.RolesCommand;
import org.mesmeralis.revelationPVP.Listeners.InventoryListener;
import org.mesmeralis.revelationPVP.Listeners.JoinQuitListener;
import org.mesmeralis.revelationPVP.Listeners.PlayerDamageListener;
import org.mesmeralis.revelationPVP.Listeners.PlayerKillListener;
import org.mesmeralis.revelationPVP.Managers.KillManager;
import org.mesmeralis.revelationPVP.Managers.RankManager;
import org.mesmeralis.revelationPVP.Managers.RoleManager;
import org.mesmeralis.revelationPVP.Papi.PapiExpansion;
import org.mesmeralis.revelationPVP.Storage.SQLGetter;
import org.mesmeralis.revelationPVP.Storage.Storage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public final class RevelationPVP extends JavaPlugin {

    public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "PVP" + ChatColor.DARK_GRAY + "] ";
    public Storage storage;
    public SQLGetter data;
    public KillManager manager = new KillManager();
    public RankManager rankManager;
    public RoleManager roleManager;
    public HashMap<UUID, PapiExpansion.Record> map = new HashMap<>();
    public UUID topWins;
    public UUID topPoints;
    public Economy economy;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.initStorage();
        this.data = new SQLGetter(this);
        this.rankManager = new RankManager(this);
        this.roleManager = new RoleManager(this.data);
        this.initListeners();
        this.registerCommands();
        new PapiExpansion(this).register();
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            topWins = data.getTopWins().join();
            topPoints = data.getTopPoints().join();
        }, 0L,600L);
       Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
           roleManager.loadAll();
           for (Player player : Bukkit.getOnlinePlayers()) {
               final int points = this.data.getPoints(player.getUniqueId()).join();
               final int kills = this.data.getKills(player.getUniqueId()).join();
               final int wins = this.data.getWins(player.getUniqueId()).join();
               final int deaths = this.data.getDeaths(player.getUniqueId()).join();
               final Roles role = this.data.getRole(player.getUniqueId()).join();
               PapiExpansion.Record record = new PapiExpansion.Record(points, kills, deaths, wins, role);
               this.map.put(player.getUniqueId(), record);
           }
       });
    }

    @Override
    public void onDisable() {
        if (this.storage != null) {
            this.storage.disconnectSQL();
        }
    }


    public void initStorage() {

        if (this.storage != null) {
            this.storage.disconnectSQL();
        }
        this.storage = new Storage(this);

        try {
            this.storage.connectSQL();
            getLogger().info("Successfully connected to MySQL.");
        } catch (Exception e) {
            getLogger().severe("Failed to connect to MySQL:");
            e.printStackTrace();
        }
        // 4. Recreate your SQLGetter instance
        this.data = new SQLGetter(this);

        // 5. Optional: log table creation
        getLogger().info("SQL table checked/initialized.");
    }


    private void initListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerKillListener(this, manager), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
    }

    private void registerCommands() {
        this.getCommand("roles").setExecutor(new RolesCommand(this));
    }

    public Optional<Economy> getEconomy() {
        if (this.economy == null) {
            final RegisteredServiceProvider<Economy> provider = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null) {
                this.economy = provider.getProvider();
            }
        }
        return Optional.ofNullable(this.economy);
    }

    public RoleManager getRoleManager() { return this.roleManager; }

    public long reload(String... flags) {
        final long start = System.currentTimeMillis();

        for (String flag : flags) {

            if (flag.equalsIgnoreCase("-full")) {

                this.roleManager.shutdown();

                this.storage.disconnectSQL();

                this.initStorage();

                Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                    this.roleManager.loadAll();
                });

                this.reloadConfig();
                break;
            }

            if (flag.equalsIgnoreCase("-config")) {
                this.reloadConfig();
            }
        }

        return System.currentTimeMillis() - start;
    }

}
