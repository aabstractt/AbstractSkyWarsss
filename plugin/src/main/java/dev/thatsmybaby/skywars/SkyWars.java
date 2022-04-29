package dev.thatsmybaby.skywars;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.world.World;
import dev.thatsmybaby.Common;
import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.object.hooks.LuckPermsHook;
import dev.thatsmybaby.redis.RedisProvider;
import dev.thatsmybaby.skywars.chests.ChestTypeManager;
import dev.thatsmybaby.skywars.cmd.*;
import dev.thatsmybaby.skywars.enums.GameState;
import dev.thatsmybaby.skywars.factory.ArenaFactory;
import dev.thatsmybaby.skywars.listeners.*;
import dev.thatsmybaby.skywars.object.cage.SkyWarsCage;
import dev.thatsmybaby.skywars.utils.GameUtil;
import dev.thatsmybaby.skywars.utils.slimeworld.SkywarsSlimeWorldAPI;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkyWars extends JavaPlugin {

    public final SkyWarsCage DEFAULT_CAGE = new SkyWarsCage(this, "default");

    @Getter
    private static SkyWars instance;
    @Getter
    private SkywarsSlimeWorldAPI slimeWorldAPI;

    public static String VOTE_ITEM_NAME = ChatColor.translateAlternateColorCodes('&', "&r&aVotar");

    @Override
    public void onEnable() {
        instance = this;

        TaskUtils.plugin = this;

        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.saveResource("messages.yml", false);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getCommand("spawnpoint").setExecutor(new SpawnPointCommand());
        getCommand("createmap").setExecutor(new LoadMapCommand());
        getCommand("forcestart").setExecutor(new ForceStartCommand());
        getCommand("teleportmap").setExecutor(new TeleportMapCommand());
        getCommand("playagain").setExecutor(new PlayAgainCommand());

        this.slimeWorldAPI = new SkywarsSlimeWorldAPI(this);

        if (this.checkSlimeWorldImport()) {
            return;
        }

        File dir = new File(getDataFolder(), "schematics");
        if (!dir.exists()) dir.mkdir();

        dir = new File(getDataFolder(), "rollbacks");
        if (!dir.exists()) dir.mkdir();

        dir = new File(getDataFolder(), "cages");
        if (!dir.exists()) dir.mkdir();

        if (this.getConfig().getBoolean("load")) {
            try {
                Common.loadMessages(this.getDataFolder());

                ConfigurationSection section = this.getConfig().getConfigurationSection("redis");

                RedisProvider.getInstance().init(section.getString("address", ""), section.getString("password", ""));

                ArenaFactory.getInstance().init();

                if (LuckPermsHook.canHook()) {
                    LuckPermsHook.init();

                    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Using LuckPerms as PlayerHook!");
                }

                ChestTypeManager.loadChests(this);

                registerListeners(
                        new PlayerJoinQuitListener(),
                        new PlayerChatListener(),
                        new PlayerInteractListener(),
                        new GeneralListener(),
                        new ChestClickListener(),
                        new FallListener(),
                        new PvPListener(),
                        new BlockListener(),
                        new DamageListener(),
                        new DeathListener()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getLogger().info("El juego no cargo, debes configurar los mapas primero.");
        }
    }

    @Override
    public void onDisable() {
        ArenaFactory.getInstance().close();
    }

    public static String getServerName() {
        return instance.getConfig().getString("server-name");
    }

    @SneakyThrows
    public boolean checkSlimeWorldImport() {
        boolean importSlime = this.getConfig().getBoolean("slimeworld-manager.import-world");

        Bukkit.getLogger().info("¡Verificando importación de SlimeWorld!");

        if (importSlime) {
            String name = this.getConfig().getString("slimeworld-manager.world-name");
            String schem = this.getConfig().getString("slimeworld-manager.world-schem");

            FileUtils.copyDirectory(new File(this.getDataFolder(), "rollbacks/" + name), new File(name));

            org.bukkit.World w = GameUtil.createEmptyWorld(name);
            if (!Bukkit.getWorlds().contains(w)) {
                Bukkit.getWorlds().add(w);
            }

            Schematic schematic = ClipboardFormat.SCHEMATIC.load(new File(this.getDataFolder(), "schematics/" + schem + ".schematic"));

            World world = FaweAPI.getWorld(w.getName());

            EditSession editSession = schematic.paste(world, new Vector(0, 75, 0), true, true, null);
            editSession.flushQueue();

            w.save();
            this.getServer().unloadWorld(w.getName(), true);

            slimeWorldAPI.importWorld(name, name).thenAccept(completed -> {
                if (completed) {
                    this.getLogger().info("¡Se ha completado la importación del mundo!");
                } else {
                    this.getLogger().info("Error al importar el mundo.");
                }
            });
        }

        return importSlime;
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    public void updateMap(String folderName, String mapName, GameState status, int playersCount, int maxSlots) {
        this.updateMap(folderName, mapName, status, playersCount, maxSlots, false);
    }

    public void updateMap(String folderName, String mapName, GameState status, int playersCount, int maxSlots, boolean started) {
        TaskUtils.runAsync(() -> RedisProvider.getInstance().updateMap(getServerName(), folderName, mapName, status.ordinal(), playersCount, maxSlots, started));
    }
}