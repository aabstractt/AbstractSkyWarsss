package dev.thatsmybaby.skywars.utils.inventory.button;

import dev.thatsmybaby.skywars.utils.inventory.GUIButton;
import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import org.bukkit.inventory.ItemStack;

public class CloseButton implements GUIButton {

    private ItemStack item;

    public CloseButton(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void click(GUIPage page) {
        page.getPlayer().closeInventory();
    }

    public void destroy() {
        this.item = null;
    }

}
