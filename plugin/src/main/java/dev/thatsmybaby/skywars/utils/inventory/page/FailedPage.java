package dev.thatsmybaby.skywars.utils.inventory.page;

import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import dev.thatsmybaby.skywars.utils.inventory.button.PlaceHolder;
import dev.thatsmybaby.skywars.utils.inventory.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FailedPage extends GUIPage {

    private String[] reason;

    public FailedPage(Player player, String... reason) {
        super(player, "Error!", 54);
        this.reason = reason;
        build();
    }

    public void buildPage() {
        ItemStack confirm = new ItemStackBuilder(Material.REDSTONE_BLOCK).setName(ChatColor.RED + "ERROR:").setLore(reason);

        for (int i = 0; i < 54; i++) {
            addButton(new PlaceHolder(confirm), i);
        }
    }

    public void destroy() {
        this.reason = null;
    }

}
