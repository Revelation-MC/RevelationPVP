package org.mesmeralis.revelationPVP.Listeners;

import org.bukkit.Bukkit;
import org.mesmeralis.revelationPVP.Utils.ColourUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.mesmeralis.revelationPVP.Gui.RolesGui;
import org.mesmeralis.revelationPVP.RevelationPVP;
import org.mesmeralis.revelationPVP.Roles;

public class InventoryListener implements Listener {
    private final RevelationPVP main;

    public InventoryListener(RevelationPVP main) {
        this.main = main;
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (!e.getView().getTitle().equals(RolesGui.TITLE)) return;

        e.setCancelled(true);

        // Fix #1 — role must reset per click
        Roles role = null;

        switch (e.getSlot()) {
            case 10 -> role = Roles.ASSASSIN;
            case 12 -> role = Roles.HYPNOTIST;
            case 14 -> role = Roles.VIKING;
            case 16 -> role = Roles.WIZARD;
            case 31 -> {
                this.main.getRoleManager().setRole(player.getUniqueId(), Roles.CITIZEN, ignored -> {
                    player.sendMessage(ColourUtils.colour(RevelationPVP.PREFIX + "&cYou have left your team."));
                    player.closeInventory();
                });
                return;
            }
        }
        Bukkit.getServer().broadcastMessage("DEBUG: Slot = " + e.getSlot());

        if (role == null) return;

      //  final double roleChangeFee = this.main.getConfig().getDouble("role-change-fee");
        final Roles currentRole = this.main.getRoleManager().getRole(player.getUniqueId());

        // Fix #2 — correct method name
      //  if (!this.main.getRoleManager().isFirstRoleChange(player.getUniqueId())) {

            Roles finalRole = role;
            //this.main.getEconomy().ifPresent(economy -> {
              //  if (economy.getBalance(player) >= roleChangeFee) {

                    if (currentRole != finalRole) {

                    //    economy.withdrawPlayer(player, roleChangeFee);
                       // player.sendMessage(RevelationPVP.PREFIX + ChatColor.RED + "-" + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + roleChangeFee);

                        // Fix #3 — close GUI *inside* callback
                        this.main.getRoleManager().setRole(player.getUniqueId(), finalRole, newRole -> {
                            player.sendMessage(RevelationPVP.PREFIX + ChatColor.AQUA + "Set your team to " + ChatColor.GREEN + newRole.getPrefix());
                            player.closeInventory();
                        });

                    } else {
                        player.sendMessage(RevelationPVP.PREFIX + ColourUtils.colour("&cYou are already on this team."));
                    }

               // }
          //  });


      /**  } else {
            // First change is FREE
            this.main.getRoleManager().setRole(player.getUniqueId(), role, newRole -> {
                player.sendMessage(ColourUtils.colour(RevelationPVP.PREFIX + "&aYour team is now " + newRole.getPrefix()));
                player.closeInventory();
            });
        }**/
    }

}
