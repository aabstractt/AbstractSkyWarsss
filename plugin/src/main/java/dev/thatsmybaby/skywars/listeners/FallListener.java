package dev.thatsmybaby.skywars.listeners;

import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallListener implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            GamePlayer gamePlayer = GamePlayer.of(p);

            if (gamePlayer == null) return;

            if (gamePlayer.getPlayer() == null) return;

            if (gamePlayer.getArena() == null) return;

            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(!gamePlayer.getArena().isFallDamage());
            }
        }
    }

}
