package dev.thatsmybaby.skywars.menu;

import dev.thatsmybaby.Common;
import dev.thatsmybaby.skywars.factory.ArenaFactory;
import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import org.bukkit.entity.Player;

public class SpeedVoteMenu extends GUIPage {

    public SpeedVoteMenu(Player player) {
        super(player, Common.translateString("SPEED_VOTE_MENU_TITLE"), 27);
    }

    @Override
    public void buildPage() {
        GameArena gameArena = ArenaFactory.getInstance().getArena(getPlayer());

        if (gameArena == null) {
            return;
        }

        int slot = 10;


    }

    @Override
    public void destroy() {

    }
}
