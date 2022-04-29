package dev.thatsmybaby.skywars.listeners.event;

import dev.thatsmybaby.skywars.object.player.GamePlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class GamePlayerDeathEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final GamePlayer gamePlayer;

    public GamePlayerDeathEvent(GamePlayer gamePlayer){
        this.gamePlayer = gamePlayer;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }


    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
