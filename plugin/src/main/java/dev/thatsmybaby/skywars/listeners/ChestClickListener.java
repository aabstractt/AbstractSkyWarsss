package dev.thatsmybaby.skywars.listeners;

import dev.thatsmybaby.skywars.object.GameArena;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestClickListener implements Listener {

    @EventHandler
    public void onClickChestEvent(PlayerInteractEvent ev) {
        Block block = ev.getClickedBlock();

        if (block == null) {
            return;
        }

        Player player = ev.getPlayer();

        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            GamePlayer gamePlayer = GamePlayer.of(player);

            if (gamePlayer == null) {
                return;
            }

            GameArena arena = gamePlayer.getArena();

            if (arena == null) {
                return;
            }

            if (arena.getOpenChest().contains(block.getLocation()) || arena.getPlacedChest().contains(block.getLocation())) {
                return;
            }

            arena.getOpenChest().add(block.getLocation());

            arena.getChestType().fillChest(((Chest) block.getState()).getInventory(), block.getLocation());
        }
    }


    @EventHandler
    public void onPlaceChest(BlockPlaceEvent ev) {
        Player player = ev.getPlayer();

        GamePlayer gamePlayer = GamePlayer.of(player);

        if (gamePlayer == null || gamePlayer.getPlayer() == null) {
            return;
        }

        GameArena arena = gamePlayer.getArena();

        if (arena == null) {
            return;
        }

        if (ev.getBlock().getType() == Material.CHEST || ev.getBlock().getType() == Material.TRAPPED_CHEST) {
            arena.getPlacedChest().add(ev.getBlock().getLocation());
        }
    }
}
