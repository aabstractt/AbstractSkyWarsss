package dev.thatsmybaby.skywars.utils.inventory.page;

import dev.thatsmybaby.skywars.utils.inventory.GUIPage;
import dev.thatsmybaby.skywars.utils.inventory.button.SimpleButton;
import dev.thatsmybaby.skywars.utils.inventory.button.PlaceHolder;
import dev.thatsmybaby.skywars.utils.inventory.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GUIMultiPage extends GUIPage {

    protected int currentPage;
    protected int pageSize = 44;

    public GUIMultiPage(Player player, String rawName) {
        this(player, rawName, 54);
    }

    public GUIMultiPage(Player player, String rawName, int size) {
        super(player, rawName, size);
    }

    public void buildPage() {
        ItemStack nextPage = new ItemStackBuilder().setMaterial(Material.PAPER).setStackAmount(currentPage + 2).setName(ChatColor.YELLOW + "Click para ir a la siguiente pagina (page " + (currentPage + 2) + ")");
        ItemStack previousPage = new ItemStackBuilder().setMaterial(Material.PAPER).setStackAmount(currentPage).setName(ChatColor.YELLOW + "Click para ir a la pagina anterior (page " + (currentPage) + ")");
        ItemStack currentPageItem = new ItemStackBuilder().setMaterial(Material.PAPER).setStackAmount(currentPage + 1).setName(ChatColor.YELLOW + "Estas en la pagina " + (currentPage + 1) + "");

        if ((currentPage + 1) * pageSize < getListCount()) {
            addButton(new SimpleButton(nextPage).setAction((player, guiPage) -> {
                currentPage++;
                refresh();
            }), 53);
        }

        if (currentPage != 0) {
            addButton(new SimpleButton(previousPage).setAction((player, guiPage) -> {
                currentPage--;

                refresh();
            }), 45);
        }

        if (getListCount() != -1) {
            addButton(new PlaceHolder(currentPageItem), 49);
        }

        buildContent();
    }

    protected abstract void buildContent();

    protected abstract int getListCount();

}
