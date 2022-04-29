package dev.thatsmybaby.skywars.utils.inventory.button;

import dev.thatsmybaby.skywars.utils.inventory.GUIButton;
import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import org.bukkit.inventory.ItemStack;

public class NullButton implements GUIButton {

    @Override
    public void click(GUIPage page) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public ItemStack getItem() {
        return null;
    }
}
