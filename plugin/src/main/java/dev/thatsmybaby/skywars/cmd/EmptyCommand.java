package dev.thatsmybaby.skywars.cmd;

import dev.thatsmybaby.skywars.SkyWars;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmptyCommand implements CommandExecutor {

    private SkyWars plugin;
    public EmptyCommand(SkyWars plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender instanceof Player){

            Player p = (Player)commandSender;
            if(p.hasPermission("vicnix.skywars.admin")){
                p.sendMessage(ChatColor.RED+"No tienes permisos.");
                return true;
            }

        }

        return true;
    }
}
