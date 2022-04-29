package dev.thatsmybaby.skywars.listeners;

import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onDamageable(EntityDamageEvent ev){
        if (!(ev.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) ev.getEntity();
        GamePlayer gamePlayer = GamePlayer.of(player);

        if (gamePlayer == null || gamePlayer.getArena() == null) {
            return;
        }

        if (!gamePlayer.getArena().isDamageable()) {
            ev.setCancelled(true);

            return;
        }

        if (ev.getCause() == EntityDamageEvent.DamageCause.VOID) {
            ev.setDamage(ev.getDamage() * 20);
        }

        if (ev instanceof EntityDamageByEntityEvent) {
            if (!(((EntityDamageByEntityEvent) ev).getDamager() instanceof Player)) {
                return;
            }

            gamePlayer.attack((Player) ((EntityDamageByEntityEvent) ev).getDamager());
        }
    }
}