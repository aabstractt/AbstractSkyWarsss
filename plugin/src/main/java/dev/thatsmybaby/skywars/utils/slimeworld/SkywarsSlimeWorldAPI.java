package dev.thatsmybaby.skywars.utils.slimeworld;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.skywars.SkyWars;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class SkywarsSlimeWorldAPI {

    private final SkyWars plugin;
    private final SlimePlugin slimeWorld;
    private final SlimeLoader slimeLoader;

    public SkywarsSlimeWorldAPI(SkyWars plugin) {
        this.plugin = plugin;
        slimeWorld = (SlimePlugin) plugin.getServer().getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = slimeWorld.getLoader("file");
    }

    public CompletableFuture<Boolean> importWorld(String world, String name) {
        CompletableFuture<Boolean> slimeWorldCompletableFuture = new CompletableFuture<>();

        TaskUtils.runAsync(() -> {
            try {
                // Note that this method should be called asynchronously
                plugin.getLogger().info("Importando mundo " + world + " a slime_worlds/" + name);
                slimeWorld.importWorld(new File(world), name, slimeLoader);
                slimeWorldCompletableFuture.complete(true);
            } catch (Exception ex) {
                /* Exception handling */
                ex.printStackTrace();
                slimeWorldCompletableFuture.complete(false);
            }
        });

        return slimeWorldCompletableFuture;
    }

    public CompletableFuture<SlimeWorld> loadWorld(String name) {
        CompletableFuture<SlimeWorld> slimeWorldCompletableFuture = new CompletableFuture<>();

        TaskUtils.runAsync(() -> {
            try {
                // Note that this method should be called asynchronously
                plugin.getLogger().info("Cargando mundo slime_worlds/" + name + ".slime");
                SlimePropertyMap props = new SlimePropertyMap();
                props.setString(SlimeProperties.DIFFICULTY, "NORMAL");
                props.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
                props.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
                slimeWorldCompletableFuture.complete(slimeWorld.loadWorld(slimeLoader, name, true, props));
            } catch (Exception ex) {
                /* Exception handling */
                ex.printStackTrace();

                slimeWorldCompletableFuture.complete(null);
            }
        });

        return slimeWorldCompletableFuture;
    }

    public void generateWorld(SlimeWorld world) {
        slimeWorld.generateWorld(world);
    }
}