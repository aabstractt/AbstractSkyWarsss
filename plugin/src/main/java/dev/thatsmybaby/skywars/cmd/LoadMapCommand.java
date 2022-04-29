package dev.thatsmybaby.skywars.cmd;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.world.World;
import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.utils.GameUtil;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;

public class LoadMapCommand implements CommandExecutor {

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Run this command in-game");

            return true;
        }

        if (!command.getName().equalsIgnoreCase("createmap")) {
            return true;
        }

        if (!commandSender.hasPermission("vicnix.skywars.admin")) {
            commandSender.sendMessage(ChatColor.RED + "No tienes permisos.");

            return true;
        }

        if (strings.length != 2) {
            commandSender.sendMessage(ChatColor.GREEN + "/createmap <schematicName> <gameId> - Crear un juego apartir de una schematic");

            return true;
        }

        String schem = strings[0];
        String gameId = strings[1];

        if ((new File(gameId)).exists()) {
            commandSender.sendMessage(ChatColor.GREEN + "¡El mundo ya existe, espera mientras se carga!");

            org.bukkit.World w = GameUtil.createEmptyWorld(gameId);

            if (!Bukkit.getWorlds().contains(w)) {
                Bukkit.getWorlds().add(w);
            }

            return true;
        }

        if (SkyWars.getInstance().getConfig().contains("arenas." + gameId)) {
            commandSender.sendMessage(ChatColor.RED + "Está arena ya existe.");

            return true;
        }

        org.bukkit.World w = GameUtil.createEmptyWorld(gameId);

        w.setSpawnLocation(0, 75, 0);

        if (!Bukkit.getWorlds().contains(w)) {
            Bukkit.getWorlds().add(w);
        }

        Schematic schematic = ClipboardFormat.SCHEMATIC.load(new File(SkyWars.getInstance().getDataFolder(), "schematics/" + schem + ".schematic"));

        World world = FaweAPI.getWorld(w.getName());
        EditSession editSession = schematic.paste(world, new Vector(0, 75, 0), true, true, null);
        editSession.flushQueue();

        w.save();

        FileUtils.copyDirectory(new File(gameId), new File(SkyWars.getInstance().getDataFolder(), "rollbacks/" + gameId));

        ConfigurationSection section = SkyWars.getInstance().getConfig().createSection("arenas." + gameId);

        section.set("map-rollback", schem);
        section.set("name", schem);
        section.set("mode", "NORMAL");
        section.createSection("islands");
        section.createSection("ballons");

        SkyWars.getInstance().saveConfig();

        commandSender.sendMessage(ChatColor.GREEN + "¡Se ha creado la arena con el nombre de " + ChatColor.GOLD + gameId);

        return true;
    }
}