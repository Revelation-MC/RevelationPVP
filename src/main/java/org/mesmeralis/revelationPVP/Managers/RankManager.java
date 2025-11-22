package org.mesmeralis.revelationPVP.Managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.mesmeralis.revelationPVP.RevelationPVP;
import org.mesmeralis.revelationPVP.Storage.SQLGetter;
import org.mesmeralis.revelationPVP.Utils.ColourUtils;

import java.util.HashMap;
import java.util.Map;

public class RankManager {
    private final RevelationPVP main;
    private final SQLGetter data;
    public RankManager(RevelationPVP main) {
        this.main = main;
        this.data = main.data;
    }

    private Map<Integer, String> rankThresholds = new HashMap<>();
    private Map<String, String> rankPrefixes = new HashMap<>();

    public void loadRanks() {
        rankThresholds.clear(); // Clears old data
        FileConfiguration config = main.getConfig();
        if (!main.getConfig().contains("ranks")) {
            Bukkit.getLogger().info("WARNING: Ranks have not been set in the config!");
        } else {
            Bukkit.getLogger().info("Ranks have been set in the config.");
        }
        for (String rank : config.getConfigurationSection("ranks").getKeys(false)) {
            int points = config.getInt("ranks." + rank + ".points");
            String prefix = config.getString("ranks." + rank + ".prefix");
            rankThresholds.put(points, rank);
            rankPrefixes.put(rank, prefix);
        }
    }

    public String getRankFromPoints(int points) {
        String bestRank = "Rookie"; // Default rank
        for (Map.Entry<Integer, String> entry : rankThresholds.entrySet()) {
            if (points >= entry.getKey()) {
                bestRank = entry.getValue(); // Update to the highest qualifying rank
            }
        }
        return bestRank;
    }

    public String getRankPrefix(String rankName) {
        if(!rankPrefixes.containsKey(rankName)) {
            Bukkit.getLogger().info("Rank " + rankName + " not recognized. Assigning a default rank.");
            return ColourUtils.colour("&8BrokenPrefix");
        }
        return rankPrefixes.get(rankName);
    }
}
