package dev.thatsmybaby.skywars.cmd;

import dev.thatsmybaby.Common;
import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.object.RedisGameArena;
import dev.thatsmybaby.redis.RedisProvider;
import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.factory.ArenaFactory;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayAgainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Run this command in-game");

            return false;
        }

        GamePlayer gamePlayer = GamePlayer.of((Player) commandSender);

        if (gamePlayer == null) {
            commandSender.sendMessage(ChatColor.RED + "Ha ocurrido un error al intentar ejecutar este comando.");

            return false;
        }

        if (!gamePlayer.getArena().isStarted() && !Common.isUnderDevelopment()) {
            commandSender.sendMessage(ChatColor.GOLD + "Debes esperar a que este juego inicie para poder ejecutar este comando.");

            return false;
        }

        GameArena betterArena = ArenaFactory.getInstance().getPlayableGame();

        if (betterArena == null || gamePlayer.getArena().getFolderName().equals(betterArena.getFolderName())) {
            TaskUtils.runAsync(() -> findGame((Player) commandSender));

            return false;
        }

        gamePlayer.setArena(betterArena);
        betterArena.joinPlayer(gamePlayer);

        return false;
    }

    /**
     * Search a game in redis but in other server because here not found an available game
     *
     * @param player    Player
     */
    private void findGame(Player player) {
        RedisGameArena betterArena = null;

        for (RedisGameArena arena : RedisProvider.getInstance().getArenasAvailable()) {
            if (arena.getServerName().equalsIgnoreCase(SkyWars.getServerName())) {
                continue;
            }

            if (arena.getPlayers() >= arena.getMaxSlots()) {
                continue;
            }

            if (betterArena == null) {
                betterArena = arena;

                continue;
            }

            if (arena.getPlayers() < betterArena.getPlayers()) {
                continue;
            }

            betterArena = arena;
        }

        if (betterArena == null) {
            // I use runSync because if execute kickPlayer in async it gives an exception caused by spigot
            TaskUtils.runSync(() -> player.kickPlayer(Common.translateString("GAME_NOT_FOUND")));

            return;
        }

        RedisGameArena finalBetterArena = betterArena;

        player.sendMessage(ChatColor.GREEN + "Game found! Sending you to " + ChatColor.GOLD + finalBetterArena.getServerName());

        TaskUtils.runAsync(() -> {
            RedisProvider.getInstance().setPlayerMap(player.getUniqueId(), finalBetterArena.getFolderName());

            RedisProvider.getInstance().connectTo(SkyWars.getInstance(), player, finalBetterArena.getServerName());
        });
    }
}