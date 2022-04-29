package dev.thatsmybaby.skywars.utils.inventory;

import dev.thatsmybaby.Utils;
import lombok.Getter;
import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.object.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import dev.thatsmybaby.skywars.utils.inventory.button.NullButton;
import dev.thatsmybaby.skywars.utils.inventory.page.FailedPage;

import java.util.HashMap;

public abstract class GUIPage implements Listener {

    protected final HashMap<Integer, GUIButton> buttons;
    protected boolean overrideClose = false;
    protected final boolean blockInventoryMovement = true;
    protected final int size;
    protected ClickType type;

    protected final Inventory menu;

    @Getter
    private final Player player;
    private final String name;

    public GUIPage(Player player, String rawName, int size) {
        this.player = player;

        Utils.invokeStaticMethod(Utils.getCBSClass("event.CraftEventFactory"), "handleInventoryCloseEvent", new Class[]{Utils.getNMSClass("EntityHuman")}, Utils.invokeMethod(player, "getHandle", new Class[]{}, null));

        this.size = size;
        this.name = (rawName.length() > 32 ? rawName.substring(0, 32) : rawName);
        this.buttons = new HashMap<>();

        Bukkit.getServer().getPluginManager().registerEvents(this, SkyWars.getInstance());
        this.menu = Bukkit.getServer().createInventory(null, size, name);

        Utils.setFieldValue(Utils.getNMSClass("EntityHuman"), "activeContainer", Utils.invokeMethod(player, "getHandle", new Class[]{}), Utils.getFieldValue(Utils.getNMSClass("EntityHuman"), "defaultContainer", Utils.invokeMethod(player, "getHandle", new Class[]{}, null)));

        player.openInventory(menu);
    }

    public void build() {
        if (!this.player.isOnline()) {
            destroy();

            return;
        }

        try {
            this.buildPage();

            this.player.updateInventory();
        } catch (Exception e) {
            e.printStackTrace();

            new FailedPage(this.getPlayer(), "§eError xd");
        }
    }

    public abstract void buildPage();

    public void addButton(GUIButton button, int slot) {
        if (slot >= size) {
            return;
        }

        if (!(button instanceof NullButton) && button.getItem() != null) {
            this.menu.setItem(slot, button.getItem());
        }

        this.buttons.put(slot, button);
    }

    public void removeButton(int slot) {
        this.menu.setItem(slot, null);

        if (this.buttons.get(slot) != null) {
            this.buttons.get(slot).destroy();
        }

        this.buttons.remove(slot);
    }

    public void removeAll() {
        for (int i = 0; i <= size - 1; i++) {
            this.removeButton(i);
        }

        this.buttons.clear();
    }

    public void refresh() {
        this.removeAll();

        this.build();
    }

    public boolean isFree(int slot) {
        return !this.buttons.containsKey(slot);
    }

    public void onInventoryCloseOverride() {

    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event) {
        if (this.overrideClose) {
            this.onInventoryCloseOverride();

            return;
        }

        Player player = (Player) event.getPlayer();

        if (this.player.getName().equalsIgnoreCase(player.getName())) {
            destroy();

            destroyInternal();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        this.type = event.getClick();

        if (!this.player.getName().equalsIgnoreCase(player.getName())) {
            return;
        }

        if (!this.player.getOpenInventory().getTitle().equalsIgnoreCase(name)) {
            return;
        }

        event.setCancelled(blockInventoryMovement);

        if (!buttons.containsKey(event.getRawSlot())) {
            return;
        }

        event.setCancelled(true);

        buttons.get(event.getRawSlot()).click(this);
    }

    public ClickType getType() {
        return type;
    }

    public abstract void destroy();

    public void destroyInternal() {
        HandlerList.unregisterAll(this);

        this.buttons.values().forEach(GUIButton::destroy);

        this.buttons.clear();

        GamePlayer gamePlayer = GamePlayer.of(getPlayer());

        if (gamePlayer != null) {
            gamePlayer.setOpenInventory(null);
        }
    }
}