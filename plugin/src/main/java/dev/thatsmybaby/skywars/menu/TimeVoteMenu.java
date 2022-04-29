package dev.thatsmybaby.skywars.menu;

import dev.thatsmybaby.Common;
import dev.thatsmybaby.object.hooks.LuckPermsHook;
import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import dev.thatsmybaby.skywars.utils.inventory.button.SimpleButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.thatsmybaby.skywars.factory.ArenaFactory;
import dev.thatsmybaby.skywars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TimeVoteMenu extends GUIPage {

    @AllArgsConstructor
    @Getter
    public enum List {

        DAY("&a&lDay", 1000),
        SUNSET("&a&lSunset", 12000),
        NIGHT("&a&lNight", 13000);

        private final String title;
        private final long time;
    }

    public TimeVoteMenu(Player player) {
        super(player, Common.translateString("TIME_VOTE_MENU_TITLE"), 27);
    }

    @Override
    public void buildPage() {
        GameArena gameArena = ArenaFactory.getInstance().getArena(getPlayer());

        if (gameArena == null) {
            return;
        }

        int slot = 10;
        for (List list : List.values()) {
            int count = (int) gameArena.getPlayers().stream().filter(gamePlayer -> gamePlayer.votedTime(list.name())).count();

            this.addButton(new SimpleButton(new ItemBuilder(Material.IRON_INGOT, count).setTitle(list.title).build()).setAction(((player, page) -> {
                GamePlayer gamePlayer = GamePlayer.of(player);

                if (gamePlayer == null) {
                    return;
                }

                if (gamePlayer.getTimeVote() != null || gamePlayer.getTimeVote().equalsIgnoreCase(list.name())) {
                    return;
                }

                gamePlayer.setTimeVote(list.name());

                MainVoteMenu.updateMenu(gameArena, this.getClass());

                gamePlayer.getArena().sendArenaMessage(LuckPermsHook.getPlayerPrefix(gamePlayer.getPlayer()) + "&e ha votado por tiempo " + list.getTitle() + "&r&e.");
            })), slot);

            slot += 3;
        }

        this.addButton(new SimpleButton(new ItemBuilder(Material.BOOK, 1).setTitle("&9Regresar").build()).setAction((player, page) -> new MainVoteMenu(player)), 22);
    }

    @Override
    public void destroy() {

    }
}