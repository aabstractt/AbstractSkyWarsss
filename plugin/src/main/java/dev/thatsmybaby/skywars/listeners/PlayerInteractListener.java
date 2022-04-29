package dev.thatsmybaby.skywars.listeners;

import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.menu.MainVoteMenu;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEvent(PlayerInteractEvent ev) {
        Player player = ev.getPlayer();

        if (ev.getAction() != Action.RIGHT_CLICK_AIR && ev.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack itemStack = ev.getItem();

        if (itemStack == null) {
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return;
        }

        if (!itemMeta.hasDisplayName() || !itemMeta.getDisplayName().equals(SkyWars.VOTE_ITEM_NAME)) {
            return;
        }

        GamePlayer gamePlayer = GamePlayer.of(player);

        if (gamePlayer == null) {
            return;
        }

        gamePlayer.setOpenInventory(new MainVoteMenu(player));
    }
}