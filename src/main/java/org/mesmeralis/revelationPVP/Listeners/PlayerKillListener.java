package org.mesmeralis.revelationPVP.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mesmeralis.revelationPVP.Managers.KillManager;
import org.mesmeralis.revelationPVP.RevelationPVP;
import org.mesmeralis.revelationPVP.Utils.ColourUtils;

public class PlayerKillListener implements Listener {

    RevelationPVP main;
    KillManager manager;

    public PlayerKillListener (RevelationPVP main, KillManager manager) {
        this.main = main;
        this.manager = manager;
    }

    @EventHandler
    public void onKill (PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player killed = event.getEntity();
        assert killer != null;
        manager.playerKills.put(killer.getUniqueId(), manager.getDeaths(killer) + 1);
        manager.playerDeaths.put(killed.getUniqueId(), manager.getDeaths(killed) + 1);
        manager.streak.remove(killed.getUniqueId());
        manager.streak.put(killer.getUniqueId(), manager.getStreak(killer) + 1);

        Bukkit.getServer().broadcastMessage(ColourUtils.colour("&c" + killed.getName() + "&7 was killed by &a" + killer.getName() + "&7."));
        killed.sendMessage(ColourUtils.colour("&cYou were killed by &a" + killer.getName() + "&7."));
        killer.sendMessage(ColourUtils.colour("&aYou killed by &c" + killer.getName() + "&7."));
        killer.sendMessage(ColourUtils.colour("&6Kill streak&7&: &e" + manager.getStreak(killer)));

        switch(manager.getStreak(killer)) {
            case 3:
                Bukkit.getServer().broadcastMessage("&6&lKILL STREAK! &a" + killer.getName() + "&7 is on a &e3 &7kill streak.");
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1, true));
                break;
            case 5:
                Bukkit.getServer().broadcastMessage("&6&lKILL STREAK! &a" + killer.getName() + "&7 is on a &e5 &7kill streak.");
                killer.getActivePotionEffects().clear();
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1, true));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 1, true));
                break;
            case 10:
                Bukkit.getServer().broadcastMessage("&6&lKILL STREAK! &a" + killer.getName() + "&7 is on a &e&l10 &7kill streak.");
                Bukkit.getServer().broadcastMessage("&a" + killer.getName() + " is &2&lUNSTOPPABLE!");
                killer.getActivePotionEffects().clear();
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1, true));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 1, true));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 1, true));
                break;
            case 15:
                Bukkit.getServer().broadcastMessage("&6&lKILL STREAK! &a" + killer.getName() + "&7 is on a &e&l15 &7kill streak.");
                Bukkit.getServer().broadcastMessage("&a" + killer.getName() + " is &4&lUNKILLABLE!");
                killer.getActivePotionEffects().clear();
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1, true));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 1, true));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 1, true));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 1, true));
                break;
            default:
                break;
        }


    }

}
