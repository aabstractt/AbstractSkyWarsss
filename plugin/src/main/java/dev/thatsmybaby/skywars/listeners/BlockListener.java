package dev.thatsmybaby.skywars.listeners;

import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent e) {
        Player p = (Player) e.getPlayer();
        GamePlayer gamePlayer = GamePlayer.of(p);
        if (gamePlayer == null) return;
        if (gamePlayer.getPlayer() == null) return;
        if (gamePlayer.getArena() == null) return;
        e.setCancelled(!gamePlayer.getArena().isDestroy());
    }

    @EventHandler
    public void onBlockPlace(BlockBreakEvent e) {
        Player p = (Player) e.getPlayer();
        GamePlayer gamePlayer = GamePlayer.of(p);
        if (gamePlayer == null) return;

        if (gamePlayer.getPlayer() == null) return;

        if (gamePlayer.getArena() == null) return;

        e.setCancelled(!gamePlayer.getArena().isBuild());
    }

}
