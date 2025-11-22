package org.mesmeralis.revelationPVP.Gui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mesmeralis.revelationPVP.RevelationPVP;
import org.mesmeralis.revelationPVP.Roles;
import org.mesmeralis.revelationPVP.Utils.ColourUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RolesGui {
    // Don't ever do this, it's horrible. I'm just being lazy. I'll fix it eventually... Yeah...
    private static final RevelationPVP plugin = RevelationPVP.getPlugin(RevelationPVP.class);

    public static final String TITLE = "Teams";

    private static final ItemStack LEAVE_ROLE_ICON = new ItemStack(Material.BARRIER);

    static {
        final ItemMeta leaveTeamIconMeta = LEAVE_ROLE_ICON.getItemMeta();
        leaveTeamIconMeta.setDisplayName(ColourUtils.colour("&cLeave Role"));
        leaveTeamIconMeta.setLore(Collections.singletonList(ColourUtils.colour("&fClick here to leave your current role.")));
        LEAVE_ROLE_ICON.setItemMeta(leaveTeamIconMeta);
    }

    public static Inventory forPlayer(Player player) {
        final Roles currentRole = plugin.getRoleManager().getRole(player.getUniqueId());

        int rows = 3;
        if (currentRole != Roles.CITIZEN) {
            rows += 1;
        }

        final Inventory inventory = Bukkit.createInventory(null, rows * 9, TITLE);

        int slot = 10;
        for (Roles role : Roles.values()) {
            if (role == Roles.CITIZEN) {
                continue;
            }

            final ItemStack icon = new ItemStack(role.getIcon(), 1);
            final ItemMeta meta = icon.getItemMeta();

            if (meta == null) {
                continue;
            }

            final List<String> description = Arrays.stream(role.getDescription()).map(ColourUtils::colour).collect(Collectors.toList());

            description.add(0, ""); // Blank line.
            description.add(""); // Yet another blank line.

            if (role == currentRole) {
                meta.setDisplayName(role.getPrefix());
                description.add(ChatColor.RED + "You already have this role.");
            } else {
                meta.setDisplayName(role.getPrefix());
                description.add(ChatColor.RED + "Fee: " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + plugin.getConfig().getDouble("role-change-fee"));
            }

            meta.setLore(description);
            icon.setItemMeta(meta);

            inventory.setItem(slot, icon);
            slot += 2;
        }

        if (currentRole != Roles.CITIZEN) {
            inventory.setItem(31, LEAVE_ROLE_ICON);
        }

        return inventory;
    }
}
