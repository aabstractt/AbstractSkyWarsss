package dev.thatsmybaby.skywars.chests;

import dev.thatsmybaby.skywars.SkyWars;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChestType
{
    private final String name;
    private String title;
    private int slot;
    private Material item;
    private short item_data;
    private File config_file;
    private FileConfiguration config;
    private List<String> description;
    private List<RandomItem> items;
    private SkyWars plugin;
    
    public ChestType(String name) {
        this.config = null;

        this.description = new ArrayList<>();
        this.items = new ArrayList<>();
        this.name = name;
        this.config = YamlConfiguration.loadConfiguration(new File(SkyWars.getInstance().getDataFolder(), "chests/" + File.separator + name + ".yml"));

        ChestTypeManager.chestTypeHashMap.put(this.getName(), this);
    }
    
    public void setTitle(final String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
    }
    
    public void setSlot(final int slot) {
        this.slot = slot;
    }
    
    public void setDescription(final List<String> description) {
        this.description = description;
    }
    
    public void setItem(final String item, final short data) {
        if (ChestTypeManager.isNumeric(item)) {
            this.item = Material.getMaterial(Integer.parseInt(item));
        }
        else {
            this.item = Material.getMaterial(item.toUpperCase());
        }
        this.item_data = data;
    }
    
    public void setItems(final List<RandomItem> items) {
        this.items = items;
    }
    
    public void addDescription(final String description) {
        this.description.add(description);
    }
    
    public void addItem(final RandomItem item) {
        this.items.add(item);
    }
    
    public FileConfiguration getConfig() {
        return this.config;
    }
    
    public Material getItem() {
        return this.item;
    }
    
    public short getItemData() {
        return this.item_data;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getShortName() {
        return (this.config.getString("name") == null) ? this.name : this.config.getString("name");
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public List<String> getDescription() {
        return this.description;
    }
    
    public List<RandomItem> getItems() {
        return this.items;
    }
    
    private int countItems(final Inventory inv) {
        int i = 0;
        for (final ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                ++i;
            }
        }
        return i;
    }

    private boolean containsItem(ItemStack itemStack, Inventory inv){
        for(int i = 0; i < inv.getSize(); i++){
            if(inv.getItem(i) != null){
                if(inv.getItem(i).getType() == itemStack.getType())return true;
                if(inv.getItem(i).getType().toString().contains("LEGGINGS") &&
                        itemStack.getType().toString().contains("LEGGINGS"))return true;
                if(inv.getItem(i).getType().toString().contains("BOOTS") &&
                        itemStack.getType().toString().contains("BOOTS"))return true;
                if(inv.getItem(i).getType().toString().contains("CHESTPLATE") &&
                        itemStack.getType().toString().contains("CHESTPLATE"))return true;
                if(inv.getItem(i).getType().toString().contains("HELMET") &&
                        itemStack.getType().toString().contains("HELMET"))return true;
            }
        }
        return false;
    }


    public void fillChest(Inventory inv, Location loc) {
        this.fillChest(inv, inv.getHolder() instanceof DoubleChest);
    }
    
    private void fillChest(final Inventory inv, final boolean doubleChest) {
        inv.clear();
        if (this.getItems().size() > 0) {
            final int max_items = 15;
            while (this.countItems(inv) < max_items) {
                Collections.shuffle(this.getItems(), new Random());
                for (final RandomItem item : this.getItems()) {
                    if (this.countItems(inv) < max_items && item.hasChance()) {
                        if(!containsItem(item.getItem().build(), inv)) {

                            inv.setItem(new Random().nextInt(inv.getSize()), item.getItem().build());
                        }

                    }
                }
            }
        }
    }
    private void fillChest(final Inventory inv, final boolean doubleChest, List<RandomItem> randomList) {
    inv.clear();
    if (randomList.size() > 0) {
        final int max_items = 15;
        while (this.countItems(inv) < max_items) {
            Collections.shuffle(randomList, new Random());
            for (final RandomItem item : randomList) {
                if (this.countItems(inv) < max_items && item.hasChance()) {
                    if(!containsItem(item.getItem().build(), inv)) {

                        inv.setItem(new Random().nextInt(inv.getSize()), item.getItem().build());
                    }

                }
            }
        }
    }
}
}
