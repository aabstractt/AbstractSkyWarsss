package dev.thatsmybaby.skywars.utils.inventory.button;

import dev.thatsmybaby.skywars.utils.inventory.GUIButton;
import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import org.bukkit.inventory.ItemStack;

public class PlaceHolder implements GUIButton {

    private ItemStack item;

    public PlaceHolder(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void click(GUIPage page) {
    }

    public void destroy() {
        this.item = null;
    }

}
