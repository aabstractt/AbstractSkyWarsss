package dev.thatsmybaby.skywars.object.player;

import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import lombok.Getter;
import lombok.Setter;
import dev.thatsmybaby.skywars.object.cage.SkyWarsCage;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class GamePlayer {
    private static final Map<Player, GamePlayer> gamePlayersList = new HashMap<>();

    private GameArena arena;

    private String networkPlayerName;

    private SkyWarsCage skyWarsCage;

    private UUID uniqueId;
    private Player player;

    private int kills;

    private String chestVote = null;
    private String timeVote = null;

    @Getter @Setter
    private GUIPage openInventory;

    private float lastKillerTime = -1;
    private UUID lastKiller = null;
    private float lastAssistanceTime = -1;
    private UUID lastAssistance = null;

    public GamePlayer(Player player, GameArena arena){
        this.player = player;

        this.uniqueId = player.getUniqueId();

        this.arena = arena;

        this.networkPlayerName = player.getName();

        gamePlayersList.put(player, this);
    }

    public void attack(Player killer) {
        if (this.lastKiller == null) {
            this.lastKiller = killer.getUniqueId();

            return;
        }

        float now = System.currentTimeMillis();

        if (!this.lastKiller.equals(killer.getUniqueId())) {
            this.lastAssistance = this.lastKiller;
            this.lastAssistanceTime = now;

            this.lastKiller = killer.getUniqueId();
        }

        this.lastKillerTime = now;
    }

    public GamePlayer getLastKiller() {
        if (this.lastKiller == null || this.lastKillerTime == -1 || this.arena == null) {
            return null;
        }

        if (System.currentTimeMillis() - this.lastKillerTime > 10) {
            return null;
        }

        GamePlayer killer = of(this.lastKiller);

        if (killer == null || killer.getPlayer() == null) {
            return null;
        }

        return killer;
    }

    public boolean votedChest(String type) {
        return this.chestVote != null && this.chestVote.equalsIgnoreCase(type);
    }

    public boolean votedTime(String type) {
        return this.timeVote != null && this.timeVote.equals(type);
    }

    public static GamePlayer of(Player player){
        return gamePlayersList.getOrDefault(player, null);
    }

    public static GamePlayer of(UUID uuid) {
        return gamePlayersList.values().stream().filter(gamePlayer -> gamePlayer.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public static Collection<GamePlayer> values(){
        return gamePlayersList.values();
    }

}
