package org.mesmeralis.revelationPVP.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mesmeralis.revelationPVP.RevelationPVP;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    public RevelationPVP main;

    public JoinQuitListener(RevelationPVP main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            main.getRoleManager().loadUserAsync(uuid);
        });
    }
}
