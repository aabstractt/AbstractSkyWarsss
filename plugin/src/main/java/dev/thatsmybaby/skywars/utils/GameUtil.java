package dev.thatsmybaby.skywars.utils;

import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.object.hooks.LuckPermsHook;
import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import dev.thatsmybaby.skywars.object.spawn.IslandSpawn;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.Optional;

public class GameUtil {

    public static void updateGameTab() {
        for (GamePlayer gamePlayer : GamePlayer.values()) {
            for (GamePlayer others : GamePlayer.values()) {
                if (gamePlayer.equals(others)) continue;

                if (gamePlayer.getArena().equals(others.getArena())) continue;

                gamePlayer.getPlayer().hidePlayer(others.getPlayer());
            }
        }
    }

    public static void clearPlayer(Player player) {
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setExp(0);
        player.setLevel(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFireTicks(0);
        player.getInventory().clear();

        InventoryView inventoryView = player.getOpenInventory();

        if (inventoryView != null) {
            inventoryView.getTopInventory().clear();
        }

        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    public static String setLocationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getYaw() + "," + loc.getPitch();

    }

    public static Location getLocationFromConfig(String config) {
        if (config == null) {
            return null;
        }

        String[] split = config.split(",");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }


    public static Location getLocationFromConfig(String config, String world) {
        String[] split = config.split(",");
        return new Location(Bukkit.getWorld(world), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public static World createEmptyWorld(String worldName) {
        WorldCreator wc = new WorldCreator(worldName);
        wc.type(WorldType.FLAT);
        wc.generatorSettings("2;0;1;"); //This is what makes the world empty (void)
        return wc.createWorld();
    }

    public static void handleDie(GamePlayer gamePlayer) {
        GameArena arena = gamePlayer.getArena();

        Player instance = gamePlayer.getPlayer();

        instance.setHealth(20);
        instance.setGameMode(GameMode.SPECTATOR);

        Player killer = instance.getKiller();
        GamePlayer targetKiller = null;

        if (killer != null) {
            targetKiller = GamePlayer.of(killer);
        }

        if (targetKiller == null) {
            arena.sendArenaMessage(LuckPermsHook.getPlayerPrefix(gamePlayer.getPlayer()) + "&e murió.");
        } else {
            arena.sendArenaMessage(LuckPermsHook.getPlayerPrefix(gamePlayer.getPlayer()) + "&e murió a manos de " + LuckPermsHook.getPlayerPrefix(killer));

            targetKiller.setKills(targetKiller.getKills() + 1);
        }

        arena.sendArenaMessage("&c¡Quedan &6{arenaPlayers} &cjugadores con vida.");
        arena.onScoreboard();
        arena.onCheckArena();
        arena.onTabPrefix();

        TaskUtils.runLater(() -> {
            Optional<IslandSpawn> optional = arena.getPlayerIsland(gamePlayer.getPlayer());

            if (!optional.isPresent()) {
                return;
            }

            gamePlayer.getPlayer().teleport(optional.get().getLocation());
        }, 10L);
    }

    public static String formatTime(long seconds) {
        int minutes = 0;
        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }
        return "0" + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
}