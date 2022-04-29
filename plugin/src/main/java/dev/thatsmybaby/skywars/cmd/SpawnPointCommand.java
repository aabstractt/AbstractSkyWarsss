package dev.thatsmybaby.skywars.cmd;

import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.utils.GameUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SpawnPointCommand implements CommandExecutor {

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

        if (args.length != 3) {
            commandSender.sendMessage(ChatColor.GREEN + "/spawnpoint <gameId> <namePoint> <spawn;ballon> - Añadir un spawn");

            return true;
        }

        String gameId = args[0];
        String namePoint = args[1];
        String type = args[2];

        if (!SkyWars.getInstance().getConfig().contains("arenas." + gameId)) {
            commandSender.sendMessage(ChatColor.RED + "Está arena no existe.");

            return true;
        }

        ConfigurationSection section = SkyWars.getInstance().getConfig().getConfigurationSection("arenas." + gameId + ".islands");

        String location = GameUtil.setLocationToString(((Player) commandSender).getLocation());

        if (type.equalsIgnoreCase("ballon")) {
            section.set(namePoint + ".ballon", location);
        } else {
            section.set(namePoint + ".spawn", location);
        }

        SkyWars.getInstance().saveConfig();

        commandSender.sendMessage(ChatColor.GREEN + "¡Se ha modificado la arena con el nombre de " + ChatColor.GOLD + gameId);

        return true;
    }
}
