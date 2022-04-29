package dev.thatsmybaby;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TaskUtils {

    public static JavaPlugin plugin = null;

    public static void runAsync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        } else {
            runnable.run();
        }
    }

    public static void runSync(Runnable runnable) {
        if(Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void runLater(Runnable runnable, long later) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, later);
    }
}