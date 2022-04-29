package dev.thatsmybaby.skywars.command;

import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.object.RedisGameArena;
import dev.thatsmybaby.object.hooks.VicnixPartyHook;
import dev.thatsmybaby.object.VicnixProxyParty;
import dev.thatsmybaby.redis.RedisProvider;
import dev.thatsmybaby.skywars.SkyWars;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SWCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Run this command in-game");

            return false;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("findgame")) {
            commandSender.sendMessage(ChatColor.RED + "Use: /" + label + " findgame <?map>");

            return false;
        }

        String mapName = null;

        if (args.length > 1) {
            mapName = args[1];
        }

        VicnixProxyParty party = VicnixPartyHook.getParty(((Player) commandSender).getUniqueId());

        RedisGameArena arena = SkyWars.getInstance().getRandomArena(mapName, party == null ? 1 : party.getMembers().size());

        if (arena == null) {
            commandSender.sendMessage(ChatColor.GOLD + "No se encontro ninguna arena disponible.");

            return false;
        }

        commandSender.sendMessage(ChatColor.GREEN + "Game found! Sending you to " + ChatColor.GOLD + arena.getServerName());

        TaskUtils.runAsync(() -> {
            if (party == null) {
                RedisProvider.getInstance().setPlayerMap(((Player) commandSender).getUniqueId(), arena.getFolderName());
            } else {
                for (String uniqueId : party.getMembers()) {
                    RedisProvider.getInstance().setPlayerMap(uniqueId, arena.getFolderName());
                }
            }

            RedisProvider.getInstance().connectTo(SkyWars.getInstance(), (Player) commandSender, arena.getServerName());
        });

        return false;
    }
}