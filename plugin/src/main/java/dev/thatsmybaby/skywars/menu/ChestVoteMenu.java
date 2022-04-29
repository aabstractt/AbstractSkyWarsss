package dev.thatsmybaby.skywars.menu;

import dev.thatsmybaby.Common;
import dev.thatsmybaby.object.hooks.LuckPermsHook;
import dev.thatsmybaby.skywars.chests.ChestType;
import dev.thatsmybaby.skywars.chests.ChestTypeManager;
import dev.thatsmybaby.skywars.factory.ArenaFactory;
import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import dev.thatsmybaby.skywars.utils.ItemBuilder;
import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import dev.thatsmybaby.skywars.utils.inventory.button.SimpleButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ChestVoteMenu extends GUIPage {

    @AllArgsConstructor
    @Getter
    public enum List {
        BASIC("&a&lBasicos", "BASICOS"),
        NORMAL("&a&lNormal", "NORMAL"),
        OVERPOWERED("&a&lPoderosos", "PODEROSOS");

        private final String title;
        private final String type;

        public static List byTitle(String title) {
            for (List value : values()) {
                if (ChatColor.translateAlternateColorCodes('&', value.title).equals(title)) {
                    return value;
                }
            }

            return null;
        }
    }

    public ChestVoteMenu(Player player) {
        super(player, Common.translateString("CHEST_VOTE_MENU_TITLE"), 27);

        this.build();
    }

    @Override
    public void buildPage() {
        GameArena gameArena = ArenaFactory.getInstance().getArena(getPlayer());

        if (gameArena == null) {
            return;
        }

        for (ChestType chestType : ChestTypeManager.chestTypeHashMap.values()) {
            int count = (int) gameArena.getPlayers().stream().filter(player -> player.votedChest(chestType.getName())).count();

            this.addButton(new SimpleButton(new ItemBuilder(chestType.getItem(), count).setTitle(chestType.getTitle()).addLore(chestType.getDescription()).build()).setAction((player, page) -> {
                GamePlayer gamePlayer = GamePlayer.of(player);

                if (gamePlayer == null) {
                    return;
                }

                if (gamePlayer.getChestVote() != null && gamePlayer.getChestVote().equalsIgnoreCase(chestType.getName())) {
                    return;
                }

                gamePlayer.setChestVote(chestType.getName());

                MainVoteMenu.updateMenu(gameArena, this.getClass());

                gamePlayer.getArena().sendArenaMessage(LuckPermsHook.getPlayerPrefix(gamePlayer.getPlayer()) + "&e ha votado por cofres " + chestType.getName() + "&r&e.");
            }), chestType.getSlot());
        }

        this.addButton(new SimpleButton(new ItemBuilder(Material.BOOK, 1).setTitle("&9Regresar").build()).setAction((player, page) -> new MainVoteMenu(player)), 22);
    }

    @Override
    public void destroy() {

    }
}