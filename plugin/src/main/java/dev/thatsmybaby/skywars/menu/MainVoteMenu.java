package dev.thatsmybaby.skywars.menu;

import dev.thatsmybaby.Common;
import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import dev.thatsmybaby.skywars.utils.ItemBuilder;
import dev.thatsmybaby.skywars.utils.inventory.button.SimpleButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainVoteMenu extends GUIPage {

    public MainVoteMenu(Player player) {
        super(player, Common.translateString("MAIN_VOTE_MENU_TITLE"), 27);

        build();
    }

    @Override
    public void buildPage() {
        GamePlayer gamePlayer = GamePlayer.of(getPlayer());

        if (gamePlayer == null) {
            return;
        }

        this.addButton(new SimpleButton(new ItemBuilder(Material.CHEST, 1).setTitle("&dCofres").addLore("&aVota por el tipo de items").build()).setAction((player, page) -> gamePlayer.setOpenInventory(new ChestVoteMenu(player))), 10);

        this.addButton(new SimpleButton(new ItemBuilder(Material.EXP_BOTTLE, 1).setTitle("&dVelocidad").addLore("&aVota por el tipo de proyectil").build()).setAction((player, page) -> {

        }), 12);

        this.addButton(new SimpleButton(new ItemBuilder(Material.WATCH, 1).setTitle("&dTiempo").addLore("&aVota por el tiempo del juego").build()).setAction((player, page) -> gamePlayer.setOpenInventory(new TimeVoteMenu(player))), 14);

        this.addButton(new SimpleButton(new ItemBuilder(Material.APPLE, 1).setTitle("&dVida").addLore("&aVota por el tiempo del juego").build()).setAction((player, page) -> {

        }), 16);
    }

    public static void updateMenu(GameArena arena, Class<? extends GUIPage> clazz) {
        for (GamePlayer target : arena.getPlayers()) {
            GUIPage openInventory = target.getOpenInventory();

            if (openInventory == null || !openInventory.getClass().equals(clazz)) {
                continue;
            }

            openInventory.refresh();
        }
    }

    @Override
    public void destroy() {

    }
}