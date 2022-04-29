package dev.thatsmybaby.skywars.listeners;

import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerChatEvent(AsyncPlayerChatEvent ev) {
        Player player = ev.getPlayer();

        GamePlayer gamePlayer = GamePlayer.of(player);

        if (gamePlayer == null || gamePlayer.getPlayer() == null) {
            return;
        }

        GameArena arena = gamePlayer.getArena();

        if (arena == null) {
            return;
        }

        if (!arena.isStarted() && !player.hasPermission("vicnix.skywars.chat")) {
            player.sendMessage(ChatColor.RED+"¡No puedes hablar antes que comience la partida!");

            ev.setCancelled(true);

            return;
        }

        for(Player recipients : ev.getRecipients()){
            GamePlayer gp = GamePlayer.of(recipients);

            if (gp == null || arena.getPlayers().contains(gp)) {
                continue;
            }

            ev.getRecipients().remove(recipients);
        }
    }
}
