package org.mesmeralis.revelationPVP.Managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class KillManager {
    public HashMap<UUID, Integer> playerKills = new HashMap<>();
    public HashMap<UUID, Integer> playerDeaths = new HashMap<>();
    public HashMap<UUID, Integer> streak = new HashMap<>();

    public void givePerks() {

    }

    public int getKills(Player player){
        return this.playerKills.getOrDefault(player.getUniqueId(), 0);
    }

    public int getDeaths(Player player) {
        return  this.playerKills.getOrDefault(player.getUniqueId(), 0);
    }

    public int getStreak (Player player) {
        return this.streak.getOrDefault(player.getUniqueId(), 0);
    }

}
