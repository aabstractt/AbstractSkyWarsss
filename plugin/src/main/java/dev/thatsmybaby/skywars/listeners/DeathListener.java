package dev.thatsmybaby.skywars.listeners;

import dev.thatsmybaby.skywars.listeners.event.GamePlayerDeathEvent;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import dev.thatsmybaby.skywars.utils.GameUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent ev){
        Player player = ev.getEntity();
        GamePlayer gamePlayer = GamePlayer.of(player);

        if (gamePlayer == null || gamePlayer.getArena() == null) {
            return;
        }

        ev.setDeathMessage(null);
        ev.setDroppedExp(0);

        Bukkit.getServer().getPluginManager().callEvent(new GamePlayerDeathEvent(gamePlayer));
    }

    @EventHandler
    public void onDeathGamePlayer(GamePlayerDeathEvent e){
        GamePlayer gamePlayer = e.getGamePlayer();

        GameUtil.handleDie(gamePlayer);
    }
}