package dev.thatsmybaby.skywars.utils.inventory.button;

import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import org.bukkit.entity.Player;

public interface ButtonAction {

    void onClick(Player player, GUIPage page);
}