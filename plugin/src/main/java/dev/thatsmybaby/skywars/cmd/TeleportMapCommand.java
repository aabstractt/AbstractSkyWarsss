package dev.thatsmybaby.skywars.cmd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportMapCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Run this command in-game");

            return true;
        }

        if (!commandSender.hasPermission("vicnix.skywars.admin")) {
            commandSender.sendMessage(ChatColor.RED + "No tienes permisos.");

            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage(ChatColor.GREEN + "/teleportmap <world> - Ir a un mundo");

            return true;
        }

        World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            commandSender.sendMessage(ChatColor.RED + "¡Esté mundo no existe!");

            return true;
        }

        ((Player) commandSender).teleport(world.getSpawnLocation());

        commandSender.sendMessage(ChatColor.GREEN + "¡Has sido enviado al mundo " + ChatColor.GOLD + world);

        return true;
    }
}