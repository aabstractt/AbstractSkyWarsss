package dev.thatsmybaby.skywars.task;

import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.redis.RedisProvider;
import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import dev.thatsmybaby.skywars.chests.ChestType;
import dev.thatsmybaby.skywars.chests.ChestTypeManager;
import dev.thatsmybaby.skywars.enums.GameState;
import dev.thatsmybaby.skywars.enums.SkyWarsMode;
import dev.thatsmybaby.skywars.menu.TimeVoteMenu;
import dev.thatsmybaby.skywars.object.cage.SkyWarsCage;
import dev.thatsmybaby.skywars.utils.GameUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStartCountdownTask extends BukkitRunnable {

    private final GameArena arena;
    private int seconds;

    public GameStartCountdownTask(GameArena arena) {
        this.arena = arena;

        this.seconds = GameArena.START_COUNTDOWN;
    }

    @Override
    public void run() {
        if (--seconds <= 0) {
            this.arena.setStatus(GameState.INGAME);

            this.arena.sendArenaMessage(ChatColor.YELLOW + "¡La partida ha comenzado!");

            checkChests();
            checkTime();

            for (GamePlayer gamePlayer : this.arena.getPlayers()) {
                SkyWarsCage.undoLastSession(gamePlayer.getPlayer());

                GameUtil.clearPlayer(gamePlayer.getPlayer());

                gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
                // Agregar poción de efecto en el modo SPEED SKYWARS
                if (this.arena.getSkyWarsMode().equals(SkyWarsMode.SPEED)) {
                    gamePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false));
                }
            }

            RedisProvider.getInstance().removeMap(SkyWars.getServerName(), this.arena.getMapName());

            this.arena.setBuild(true);
            this.arena.setDestroy(true);
            this.arena.setPvp(true);
            this.arena.onScoreboard();
            this.arena.onTabPrefix();
            this.arena.chestRefillEventTask(false);

            TaskUtils.runLater(() -> this.arena.setFallDamage(true), 100L);

            this.cancel();
        } else {
            if (seconds % 5 == 0 || seconds < 5) {
                this.arena.getPlayers().forEach(soundPlayer -> soundPlayer.getPlayer().playSound(soundPlayer.getPlayer().getLocation(), Sound.CLICK, 1f, 1f));

                this.arena.sendArenaMessage("GAME_START_IN", ((seconds >= 4 ? ChatColor.GOLD : ChatColor.RED)).toString() + this.seconds);

                //this.arena.sendArenaMessage(ChatColor.YELLOW + "La partida comenzará en " + (seconds >= 4 ? "" + ChatColor.GOLD + seconds : "" + ChatColor.RED + seconds) + " " + ChatColor.YELLOW + "segundos.");
            }
        }

        for (GamePlayer gamePlayer : this.arena.getPlayers()) {
            gamePlayer.getPlayer().setLevel(seconds);
            gamePlayer.getPlayer().setExp(((float) seconds / (float) GameArena.START_COUNTDOWN));
        }
    }

    private void checkChests() {
        Map<String, Integer> map = new HashMap<>();

        for (ChestType chestType : ChestTypeManager.chestTypeHashMap.values()) {
            int count = (int) this.arena.getPlayers().stream().filter(gamePlayer -> gamePlayer.votedChest(chestType.getName())).count();

            if (count > 0) {
                map.put(chestType.getName(), count);
            }
        }

        ChestType chestType = ChestTypeManager.getChestType("Normal");
        if (!map.isEmpty()) {
            List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
            list.sort(Map.Entry.comparingByValue());

            chestType = ChestTypeManager.getChestType(list.get(0).getKey());
        }

        this.arena.setChestType(chestType);

        this.arena.sendArenaMessage("Se ha seleccionado el tipo de Cofres " + this.arena.getChestType().getName());
    }

    private void checkTime() {
        Map<String, Integer> map = new HashMap<>();

        for (TimeVoteMenu.List list : TimeVoteMenu.List.values()) {
            int count = (int) this.arena.getPlayers().stream().filter(gamePlayer -> gamePlayer.votedTime(list.name())).count();

            if (count > 0) {
                map.put(list.name(), count);
            }
        }

        TimeVoteMenu.List list = selectBetter(map, TimeVoteMenu.List.DAY, TimeVoteMenu.List.class);

        this.arena.sendArenaMessage("Se ha seleccionado el tiempo de " + list.getTitle());

        this.arena.getWorld().setTime(list.getTime());
    }

    @SuppressWarnings("unchecked")
    private <T> T selectBetter(Map<String, Integer> map, T defaultType, Class<T> enumType) {
        if (map.isEmpty()) {
            return defaultType;
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        return (T) Enum.valueOf((Class<? extends Enum>) enumType, list.get(0).getKey());
    }
}