package dev.thatsmybaby.skywars.object;

import dev.thatsmybaby.Common;
import dev.thatsmybaby.TaskUtils;
import dev.thatsmybaby.nametag.NameTag;
import dev.thatsmybaby.object.hooks.LuckPermsHook;
import lombok.Getter;
import lombok.Setter;
import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.chests.ChestType;
import dev.thatsmybaby.skywars.chests.ChestTypeManager;
import dev.thatsmybaby.skywars.enums.GameState;
import dev.thatsmybaby.skywars.enums.SkyWarsMode;
import dev.thatsmybaby.skywars.object.cage.SkyWarsCage;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import dev.thatsmybaby.skywars.object.spawn.IslandSpawn;
import dev.thatsmybaby.skywars.task.GameRefillEventTask;
import dev.thatsmybaby.skywars.task.GameStartCountdownTask;
import dev.thatsmybaby.skywars.utils.BoardAPI;
import dev.thatsmybaby.skywars.utils.GameUtil;
import dev.thatsmybaby.skywars.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class GameArena {

    public static final int START_COUNTDOWN = 30, CHEST_REFILL = 180;

    @Getter
    private final String folderName;
    @Getter
    private String mapName;
    @Getter @Setter
    private GameState status = GameState.IDLE;

    private boolean fallDamage;
    private boolean pvp;
    private boolean build;
    private boolean destroy;
    private boolean damageable;
    private boolean chestRefilled;

    private int chestCountdown;

    private ChestType chestType;

    private SkyWarsMode skyWarsMode;

    private Set<GamePlayer> players;
    private Set<IslandSpawn> islandSpawns;
    private Set<Location> openChest, placedChest;

    private BukkitTask startCountdownTask, chestRefillTask;

    private final ConfigurationSection section;

    public GameArena(String folderName, ConfigurationSection section) {
        this.folderName = folderName;

        this.section = section;
    }

    public void loadMap() {
        this.mapName = this.section.getString("mapName");

        this.status = GameState.IDLE;
        this.skyWarsMode = SkyWarsMode.NORMAL;

        this.fallDamage = false;
        this.pvp = false;
        this.build = false;
        this.destroy = false;
        this.damageable = true;
        this.chestRefilled = false;

        this.chestCountdown = 0;

        this.startCountdownTask = null;

        this.players = new HashSet<>();
        this.islandSpawns = new HashSet<>();
        this.openChest = new HashSet<>();
        this.placedChest = new HashSet<>();

        this.chestType = ChestTypeManager.getChestType("NORMAL");

        for (String s : this.section.getConfigurationSection("islands").getKeys(false)) {
            islandSpawns.add(new IslandSpawn(
                    null,
                    GameUtil.getLocationFromConfig(this.section.getString("islands." + s + ".ballon")),
                    GameUtil.getLocationFromConfig(this.section.getString("islands." + s + ".spawn"), this.folderName)
            ));
        }

        this.islandSpawns.forEach(islandSpawn -> islandSpawn.getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.AIR));

        Bukkit.getLogger().info(this.folderName + " juego cargado en el mapa " + this.mapName);
    }

    public World getWorld() {
        return Bukkit.getWorld(this.folderName);
    }

    protected void clear() {
        this.players.clear();
        this.islandSpawns.clear();
        this.openChest.clear();
        this.placedChest.clear();
    }

    public void joinPlayer(GamePlayer gamePlayer) {
        Player instance = gamePlayer.getPlayer();

        if (!canJoin()) {
            instance.kickPlayer("Está arena comenzo antes de tiempo, vuelve a intentarlo nuevamente.");

            return;
        }

        Optional<IslandSpawn> optional = this.getOpenIsland();

        if (!optional.isPresent()) {
            instance.kickPlayer("No hay más espacio disponible para está partida.");

            return;
        }

        this.players.add(gamePlayer);

        IslandSpawn islandSpawn = optional.get();
        islandSpawn.setByPlayer(instance);

        // Cage paste
        SkyWars.getInstance().DEFAULT_CAGE.paste(instance, islandSpawn.getLocation());

        // Teleport to Island Spawn
        instance.setFallDistance(0.0F);
        instance.teleport(islandSpawn.getLocation().clone().add(0.5, 0, 0.5));

        GameUtil.clearPlayer(instance);

        instance.getInventory().setItem(1, new ItemBuilder(Material.MAP).setTitle(SkyWars.VOTE_ITEM_NAME).build());

        this.sendArenaMessage("PLAYER_JOINED", LuckPermsHook.getPlayerPrefix(instance), String.valueOf(this.players.size()), String.valueOf(this.islandSpawns.size()));

        for (GamePlayer targetGamePlayer : this.players) {
            NameTag.setNametag(LuckPermsHook.getPlayerPrefix(instance, false), "", NameTag.NameTags.valueOf(LuckPermsHook.getPlayerGroup(instance).toUpperCase()).ordinal(), instance, Collections.singletonList(targetGamePlayer.getPlayer()));

            NameTag.setNametag(LuckPermsHook.getPlayerPrefix(targetGamePlayer.getPlayer(), false), "", NameTag.NameTags.valueOf(LuckPermsHook.getPlayerGroup(targetGamePlayer.getPlayer()).toUpperCase()).ordinal(), targetGamePlayer.getPlayer(), Collections.singletonList(instance));
        }

        if (this.players.size() >= (islandSpawns.size() / 2)) {
            this.onStartCountdown();
        }

        SkyWars.getInstance().updateMap(this.folderName, this.mapName, this.status, this.players.size(), this.islandSpawns.size());

        this.onScoreboard();
    }

    public void quitPlayer(GamePlayer player) {
        if (this.status == GameState.IDLE || this.status == GameState.STARTING) {
            Optional<IslandSpawn> optional = this.getPlayerIsland(player.getPlayer());

            if (optional.isPresent()) {
                IslandSpawn islandSpawn = optional.get();

                islandSpawn.setByPlayer(null);
            }

            this.sendArenaMessage("PLAYER_LEFT", LuckPermsHook.getPlayerPrefix(player.getPlayer()), String.valueOf(this.players.size()), String.valueOf(this.islandSpawns.size()));

            // UNDO Schematic Cage
            SkyWarsCage.undoLastSession(player.getPlayer());

            if (this.players.size() < (this.islandSpawns.size() / 2)) {
                this.stopCountdown();
            }

            this.onScoreboard();
        }

        this.players.remove(player);

        SkyWars.getInstance().updateMap(this.folderName, this.mapName, this.status, this.players.size(), this.islandSpawns.size(), this.isStarted());
    }

    public void rollback() {
        this.status = GameState.ENDING;

        Iterator<GamePlayer> iterator = this.players.iterator();

        while (iterator.hasNext()) {
            GamePlayer gamePlayer = iterator.next();

            // I use remove before because if I kick the player after the PlayerQuitEvent is handle and remove the player first
            iterator.remove();

            Bukkit.dispatchCommand(gamePlayer.getPlayer(), "playagain now");

            //gamePlayer.getPlayer().kickPlayer("La arena se está reiniciando");
        }

        Bukkit.unloadWorld(this.folderName, false);

        TaskUtils.runLater(() -> {
            clear();

            SkyWars.getInstance().getSlimeWorldAPI().loadWorld(this.folderName).thenAccept(slimeWorld -> {
                if (slimeWorld == null) {
                    return;
                }

                TaskUtils.runLater(() -> {
                    this.loadMap();

                    SkyWars.getInstance().updateMap(this.folderName, this.mapName, this.status, this.players.size(), this.islandSpawns.size());
                }, 30L);

                SkyWars.getInstance().getSlimeWorldAPI().generateWorld(slimeWorld);
            });
        }, 40L);
    }

    public void onScoreboard() {
        HashMap<String, Integer> values = new HashMap<>();

        if (!chestRefilled && this.status == GameState.INGAME || this.status == GameState.ENDING) {
            values.put(ChatColor.translateAlternateColorCodes('&', "   "), 9);
            values.put(ChatColor.translateAlternateColorCodes('&', "&fEvento &d(Rellenado)&f:"), 8);
            values.put(ChatColor.translateAlternateColorCodes('&', "&d" + GameUtil.formatTime(this.chestCountdown)), 7);
        }

        values.put(ChatColor.translateAlternateColorCodes('&', "   "), 6);
        values.put(ChatColor.translateAlternateColorCodes('&', "&fMapa: &d" + this.mapName), 5);
        values.put(ChatColor.translateAlternateColorCodes('&', "  "), 4);
        values.put(ChatColor.translateAlternateColorCodes('&', "&fJugadores: &d" + this.players.size() + "/" + this.islandSpawns.size()), 3);
        values.put(ChatColor.translateAlternateColorCodes('&', " "), 2);
        values.put(ChatColor.translateAlternateColorCodes('&', "&amc.vicnix.net"), 1);

        String title;
        switch (this.skyWarsMode) {
            case LUCKYBLOCKS:
                title = ChatColor.translateAlternateColorCodes('&', "&6&lLUCKY BLOCKS");
                break;
            case SPEED:
                title = ChatColor.translateAlternateColorCodes('&', "&b&lSPEED SKYWARS");
                break;
            case TEAM:
                title = ChatColor.translateAlternateColorCodes('&', "&a&lSKYWARS TEAM");
                break;
            default:
                title = ChatColor.translateAlternateColorCodes('&', "&a&lSKYWARS");
                break;
        }

        this.players.forEach(all -> BoardAPI.ScoreboardUtil.rankedSidebarDisplay(all.getPlayer(), title, values));
    }

    public void setChestType(ChestType chestType) {
        this.chestType = chestType;
    }

    public void onTabPrefix() {
        for (GamePlayer gamePlayer : this.players) {
            String killsString = ChatColor.YELLOW.toString() + gamePlayer.getKills();
            StringBuilder newNameSpaces = new StringBuilder(ChatColor.AQUA + gamePlayer.getPlayer().getName());

            for (int i = 0; i < 15 - (gamePlayer.getNetworkPlayerName().length() - killsString.length()); i++) {
                newNameSpaces.append(' ');
            }

            gamePlayer.getPlayer().setPlayerListName(newNameSpaces.append(killsString).toString());
        }
    }

    public void onCheckArena() {
        if (this.status == GameState.INGAME) {
            if (this.alivePlayers().size() > 1) {
                return;
            }

            this.chestRefillEventTask(true);

            this.damageable = false;

            GamePlayer gamePlayer = this.alivePlayers().stream().findAny().orElse(null);

            this.sendArenaMessage("PLAYER_WIN", gamePlayer == null ? "NADIE" : gamePlayer.getNetworkPlayerName());
            //this.sendArenaMessage("&aEl ganador de la partida fue &6" + (gamePlayer == null ? "NADIE" : gamePlayer.getNetworkPlayerName()));

            TaskUtils.runLater(this::rollback, 100L);
        }
    }

    public void onStartCountdown() {
        if (this.startCountdownTask != null) {
            return;
        }

        this.status = GameState.STARTING;
        this.startCountdownTask = new GameStartCountdownTask(this).runTaskTimer(SkyWars.getInstance(), 0L, 20L);
    }

    public void stopCountdown() {
        if (this.startCountdownTask != null) {
            this.startCountdownTask.cancel();
        }

        this.startCountdownTask = null;

        this.status = GameState.IDLE;

        this.players.forEach(p -> {
            p.getPlayer().setLevel(0);

            p.getPlayer().setExp(0);
        });
    }

    public void chestRefillEventTask(boolean stop) {
        if (!stop) {
            this.chestRefillTask = new GameRefillEventTask(this).runTaskTimer(SkyWars.getInstance(), 20L, 20L);
        } else if (this.chestRefillTask != null) {
            this.chestRefillTask.cancel();

            this.chestCountdown = CHEST_REFILL;
        }
    }

    public boolean canJoin() {
        return !isStarted() && this.players.size() < this.islandSpawns.size();
    }

    public boolean isStarted() {
        return this.status.ordinal() > GameState.STARTING.ordinal();
    }

    public Set<GamePlayer> alivePlayers() {
        return this.players.stream().filter(players -> players.getPlayer().getGameMode() != GameMode.SPECTATOR).collect(Collectors.toSet());
    }

    public Optional<IslandSpawn> getOpenIsland() {
        return this.islandSpawns.stream().filter(island -> island.getByPlayer() == null).findAny();
    }

    public Optional<IslandSpawn> getPlayerIsland(Player player) {
        return this.islandSpawns.stream().filter(islandSpawn -> islandSpawn.getByPlayer() != null).filter(islandSpawn -> islandSpawn.getByPlayer().equals(player)).findAny();
    }

    public void sendArenaMessage(String message, String... args) {
        for (Player player : getWorld().getPlayers()) {
            player.sendMessage(Common.translateString(message, args));
        }
    }
}