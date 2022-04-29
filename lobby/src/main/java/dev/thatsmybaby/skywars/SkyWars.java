package dev.thatsmybaby.skywars;

import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.object.RedisGameArena;
import dev.thatsmybaby.object.hooks.VicnixPartyHook;
import dev.thatsmybaby.redis.RedisProvider;
import dev.thatsmybaby.skywars.command.SWCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkyWars extends JavaPlugin {

    @Getter
    private static SkyWars instance;

    @Getter
    private final Map<String, RedisGameArena> arenas = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        TaskUtils.plugin = this;

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        ConfigurationSection section = this.getConfig().getConfigurationSection("redis");

        RedisProvider.getInstance().init(section.getString("address"), section.getString("password"));

        if (VicnixPartyHook.canHook()) {
            getLogger().info("VicnixProxyParty hook enabled!");
        }

        getCommand("sw").setExecutor(new SWCommand());

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            List<RedisGameArena> arenas = RedisProvider.getInstance().getArenasAvailable();

            for (String hash : this.arenas.keySet()) {
                if (arenas.stream().anyMatch(gameArena -> gameArena.getHash().equals(hash))) {
                    continue;
                }

                this.arenas.remove(hash);
            }

            for (RedisGameArena arena : arenas) {
                this.arenas.put(arena.getHash(), arena);
            }
        }, 20, 20);
    }

    public RedisGameArena getRandomArena(String mapName, int playersCount) {
        RedisGameArena betterArena = null;

        for (RedisGameArena arena : this.arenas.values()) {
            if (mapName != null && !mapName.equalsIgnoreCase(arena.getMapName())) {
                continue;
            }

            if (arena.getPlayers() >= arena.getMaxSlots()) {
                continue;
            }

            if (arena.getPlayers() + playersCount > arena.getMaxSlots()) {
                continue;
            }

            if (betterArena == null) {
                betterArena = arena;

                continue;
            }

            if (arena.getPlayers() > betterArena.getPlayers()) {
                continue;
            }

            betterArena = arena;
        }

        if (betterArena != null && betterArena.getPlayers() >= betterArena.getMaxSlots()) {
            betterArena = null;
        }

        return betterArena;
    }
}