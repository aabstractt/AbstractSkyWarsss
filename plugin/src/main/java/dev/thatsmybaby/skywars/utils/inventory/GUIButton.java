package dev.thatsmybaby.skywars.utils.inventory;

import org.bukkit.inventory.ItemStack;

public interface GUIButton {

    void click(GUIPage page);

    void destroy();

    ItemStack getItem();
}