package org.mesmeralis.revelationPVP.Listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mesmeralis.revelationPVP.RevelationPVP;
import org.mesmeralis.revelationPVP.Roles;

public class PlayerDamageListener implements Listener {
    private final RevelationPVP main;
    
    public PlayerDamageListener(RevelationPVP main) { this.main = main; }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        final Entity attacker = e.getDamager();
        final Entity victim = e.getEntity();

        if (!(attacker instanceof Player)) {
            if (victim instanceof Player) {
                final Roles victimRole = this.main.getRoleManager().getRole(victim.getUniqueId());
                final double damage = victimRole.applyCombatProtections(attacker, (Player) victim, e.getDamage());
                if (damage == -1) {
                    e.setCancelled(true);
                }
                e.setDamage(damage);
            }
            return;
        }

        final Roles attackerRole = this.main.getRoleManager().getRole(attacker.getUniqueId());

        if (attackerRole == Roles.CITIZEN) {
            return;
        }

        double finalDamage;
        if (victim instanceof Player) {
            final Roles victimRole = this.main.getRoleManager().getRole(victim.getUniqueId());
            finalDamage = attackerRole.applyCombatPerks((Player) attacker, (Player) victim, e.getDamage());
        } else {
            finalDamage = attackerRole.applyCombatPerks((Player) attacker, victim, e.getDamage());
        }

        if (finalDamage == -1) {
            e.setCancelled(true);
        } else {
            e.setDamage(finalDamage);
        }
    }
}
