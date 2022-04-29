package dev.thatsmybaby.skywars.factory;

import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.redis.RedisProvider;
import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.object.GameArena;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArenaFactory {

    @Getter
    private final static ArenaFactory instance = new ArenaFactory();

    private final Map<String, GameArena> arenas = new HashMap<>();

    private GameArena playing = null;

    public void init() throws IOException {
        SkyWars instance = SkyWars.getInstance();

        ConfigurationSection arenaSection = instance.getConfig().getConfigurationSection("arenas");

        for (String folderName : arenaSection.getKeys(false)) {
            if (!(new File("slime_worlds/" + folderName + ".slime")).exists()) {
                FileUtils.copyFile(new File("slime_worlds/" + arenaSection.getString(folderName + ".map-rollback") + ".slime"), new File("slime_worlds/" + folderName + ".slime"));
            }

            instance.getSlimeWorldAPI().loadWorld(folderName).thenAccept(slimeWorld -> {
                if (slimeWorld == null) {
                    return;
                }

                instance.getSlimeWorldAPI().generateWorld(slimeWorld);

                TaskUtils.runLater(() -> {
                    GameArena arena = new GameArena(folderName, arenaSection.getConfigurationSection(folderName));

                    arena.loadMap();

                    this.arenas.put(folderName, arena);

                    instance.getLogger().info(folderName + " cargado correctamente.");

                    instance.updateMap(folderName, arena.getMapName(), arena.getStatus(), arena.getPlayers().size(), arena.getIslandSpawns().size());
                }, 40L);
            });
        }
    }

    public GameArena getArena(String folderName) {
        return this.arenas.get(folderName);
    }

    public GameArena getArena(Player player) {
        return this.arenas.values().stream().filter(arena -> arena.getPlayerIsland(player).isPresent()).findFirst().orElse(null);
    }

    public GameArena getPlayableGame() {
        if (this.playing == null || !this.playing.canJoin()) {
            this.arenas.values().stream().filter(GameArena::canJoin).findFirst().ifPresent(game -> this.playing = game);
        }

        return this.playing;
    }

    public void close() {
        for (GameArena arena : this.arenas.values()) {
            RedisProvider.getInstance().removeMap(SkyWars.getServerName(), arena.getMapName());
        }
    }
}