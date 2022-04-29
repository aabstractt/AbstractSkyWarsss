package dev.thatsmybaby.skywars.cmd;

import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceStartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Run this command in-game");

            return true;
        }

        if (!commandSender.hasPermission("vicnix.skywars.admin")) {
            commandSender.sendMessage(ChatColor.RED + "No tienes permisos.");

            return true;
        }

        GamePlayer gamePlayer = GamePlayer.of((Player) commandSender);

        if (gamePlayer == null) {
            commandSender.sendMessage(ChatColor.RED + "¡Asegurate de estar dentro de un juego!");

            return true;
        }

        gamePlayer.getArena().onStartCountdown();

        commandSender.sendMessage(ChatColor.GREEN + "¡Haz comenzando la cuenta atrás forzadamente!");

        return true;
    }
}
