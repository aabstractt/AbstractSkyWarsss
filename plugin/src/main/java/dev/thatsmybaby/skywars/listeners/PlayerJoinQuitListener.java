package dev.thatsmybaby.skywars.listeners;

import dev.thatsmybaby.Common;
import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.redis.RedisProvider;
import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import dev.thatsmybaby.skywars.factory.ArenaFactory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        ev.setJoinMessage(null);

        player.teleport(Bukkit.getWorld(SkyWars.getInstance().getConfig().getString("default-world")).getSpawnLocation());
        player.setGameMode(GameMode.SPECTATOR);

        TaskUtils.runAsync(() -> {
            String folderName = RedisProvider.getInstance().getPlayerMap(player.getUniqueId());

            if (folderName == null && !player.hasPermission("vicnix.staff")) {
                TaskUtils.runLater(() -> player.kickPlayer("¡No hay juegos disponibles en esté servidor! Prueba otra vez."), 20);

                return;
            }

            if (folderName != null) {
                RedisProvider.getInstance().setPlayerMap(player.getUniqueId(), null);

                GameArena arena = ArenaFactory.getInstance().getArena(folderName);

                if (arena == null || !arena.canJoin()) {
                    TaskUtils.runLater(() -> player.kickPlayer("¡No hay juegos disponibles en esté servidor! Prueba otra vez."), 20);

                    return;
                }

                GamePlayer gamePlayer = new GamePlayer(player, arena);

                TaskUtils.runLater(() -> arena.joinPlayer(gamePlayer), 20);

                Bukkit.getLogger().info(gamePlayer.getNetworkPlayerName() + " enviado hacia " + arena.getMapName() + ".");
            }
        });
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent ev) {
        ev.setQuitMessage(null);

        GamePlayer gamePlayer = GamePlayer.of(ev.getPlayer());

        if (gamePlayer == null) {
            return;
        }

        GameArena arena = gamePlayer.getArena();

        if (arena == null) {
            return;
        }

        arena.quitPlayer(gamePlayer);
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent ev) {
        Player player = ev.getPlayer();

        if (ev.getReason().equalsIgnoreCase(Common.translateString("GAME_NOT_FOUND")) && Common.isUnderDevelopment()) {
            RedisProvider.getInstance().connectTo(SkyWars.getInstance(), player, "lobby");
        }
    }
}