package org.mesmeralis.revelationPVP.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mesmeralis.revelationPVP.Gui.RolesGui;
import org.mesmeralis.revelationPVP.RevelationPVP;
import org.mesmeralis.revelationPVP.Roles;
import org.mesmeralis.revelationPVP.Utils.ColourUtils;

import java.util.Locale;


public class RolesCommand implements CommandExecutor {
    private final RevelationPVP main;

    public RolesCommand(RevelationPVP main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.openInventory(RolesGui.forPlayer(player));
            return true;
        }

        final String argument = args[0].toLowerCase(Locale.ROOT);

        if (argument.equalsIgnoreCase("leave")) {
            final Roles currentRole = this.main.getRoleManager().getRole(player.getUniqueId());

            if (currentRole == Roles.CITIZEN) {
                player.sendMessage(ColourUtils.colour(main.PREFIX + "&cYou are not currently on a team."));
                return true;
            }

            this.main.getRoleManager().setRole(player.getUniqueId(), Roles.CITIZEN, ignored ->
                    player.sendMessage(ColourUtils.colour(main.PREFIX + "&cYou have left your team."))
            );
            return true;
        }

        if (argument.equalsIgnoreCase("reload") && sender.hasPermission("roles.admin")) {
            long elapsed;
            if (args.length > 1) {
                final String[] flags = new String[args.length - 1];
                System.arraycopy(args, 1, flags, 0, args.length - 1);
                elapsed = this.main.reload(flags);
            } else {
                elapsed = this.main.reload("-config");
            }
            sender.sendMessage(ColourUtils.colour(main.PREFIX + "&aPlugin reloaded in " + elapsed + "ms."));
        }

        if(argument.equalsIgnoreCase("info")) {
            if(main.getRoleManager().getRole(player.getUniqueId()) == null) {
                sender.sendMessage(ColourUtils.colour(main.PREFIX + "&cYou are not on a team."));
            } else {
                sender.sendMessage(ColourUtils.colour(main.PREFIX + "&aYour team is: " + main.getRoleManager().getRole(player.getUniqueId()).getPrefix()));
            }
        }

        return true;
    }
}
