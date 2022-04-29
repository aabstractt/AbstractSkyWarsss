package dev.thatsmybaby.skywars.utils.inventory.button;

import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import dev.thatsmybaby.skywars.utils.inventory.GUIButton;
import org.bukkit.inventory.ItemStack;

public class SimpleButton implements GUIButton {

    protected ItemStack item;
    protected ButtonAction action;

    public SimpleButton(ItemStack item) {
        this.item = item;
    }

    public SimpleButton(ItemStack item, ButtonAction buttonAction) {
        this(item);
        this.action = buttonAction;
    }

    public SimpleButton setAction(ButtonAction action) {
        this.action = action;
        return this;
    }


    public ItemStack getItem() {
        return item;
    }

    public void click(GUIPage page) {
        if (action == null) {
            return;
        }
        action.onClick(page.getPlayer(), page);
    }

    public void destroy() {
        this.action = null;
        this.item = null;
    }
}
