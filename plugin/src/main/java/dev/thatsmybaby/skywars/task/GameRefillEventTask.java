package dev.thatsmybaby.skywars.task;

import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRefillEventTask extends BukkitRunnable {

    private final GameArena arena;
    private int seconds;

    public GameRefillEventTask(GameArena arena) {
        this.arena = arena;

        this.seconds = GameArena.CHEST_REFILL;
    }

    @Override
    public void run() {

        if (--seconds <= 0) {
            for (GamePlayer player : this.arena.getPlayers()) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CHEST_OPEN, 1f, 1f);
            }

            this.arena.sendArenaMessage("CHESTS_REFILLED");

            //this.arena.sendArenaMessage(ChatColor.YELLOW + "¡Todos los " + ChatColor.RED + "Cofres " + ChatColor.YELLOW + "fueron rellenados.");

            this.arena.getOpenChest().clear();
            this.arena.setChestRefilled(true);
            this.arena.onScoreboard();

            this.cancel();
        } else {
            this.arena.setChestCountdown(seconds);
            this.arena.onScoreboard();

            if (seconds % 60 == 0 || seconds < 5) {
                this.arena.getPlayers().forEach(soundPlayer -> soundPlayer.getPlayer().playSound(soundPlayer.getPlayer().getLocation(), Sound.CLICK, 1f, 1f));

                this.arena.sendArenaMessage("CHESTS_REFILL_IN", (this.seconds >= 4 ? ChatColor.GOLD : ChatColor.RED).toString() + this.seconds);

                //this.arena.sendArenaMessage(ChatColor.YELLOW + "Los cofres se rellenaran en " + (seconds >= 4 ? "" + ChatColor.GOLD + seconds : "" + ChatColor.RED + seconds) + " " + ChatColor.YELLOW + "segundos.");
            }
        }
    }
}
