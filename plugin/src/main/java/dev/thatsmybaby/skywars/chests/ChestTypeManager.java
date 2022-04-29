package dev.thatsmybaby.skywars.chests;

import dev.thatsmybaby.skywars.SkyWars;
import dev.thatsmybaby.skywars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.util.HashMap;

public class ChestTypeManager {

    public static HashMap<String, ChestType> chestTypeHashMap = new HashMap<>();

    public static void loadChests(SkyWars plugin) {
        ChestTypeManager.chestTypeHashMap.clear();

        final File dir = new File(plugin.getDataFolder(), "chests/" + File.separator);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.getName().contains(".yml")) {
                    String name = file.getName().replace(".yml", "");

                    ChestType chest = new ChestType(name);

                    FileConfiguration config = chest.getConfig();

                    for (final String line : config.getStringList("items")) {
                        final String[] space_split = line.split(" ");
                        final int chance = Integer.parseInt(space_split[0]);
                        final int min = Integer.parseInt(space_split[1]);
                        final int max = Integer.parseInt(space_split[2]);
                        final String item_string = space_split[3];
                        final ItemBuilder item = readItem(item_string, chest.getName());
                        if (item == null) {
                            continue;
                        }
                        chest.addItem(new RandomItem(chance, min, max, item));
                    }
                    final String[] item2 = config.getString("item.item").split(":");
                    short data = 0;
                    if (item2.length == 2) {
                        data = (short) Integer.parseInt(item2[1]);
                    }
                    chest.setItem(item2[0], data);
                    final String displayName = config.getString("item.name");
                    chest.setTitle(displayName);
                    final int slot = config.getInt("item.slot");
                    chest.setSlot(slot);
                    for (final String line2 : config.getStringList("item.description")) {
                        chest.addDescription(ChatColor.translateAlternateColorCodes('&', line2));
                    }
                }
            }
        }
    }

    public static ChestType getChestType(final String name) {
        return ChestTypeManager.chestTypeHashMap.get(name);
    }

    public static ItemBuilder readItem(final String str, final String name) {
        final String[] all_item_split = str.split(",");
        final String[] item_split = all_item_split[0].split(":");
        short data = 0;
        Material material = null;
        if (isNumeric(item_split[0])) {
            final int id = Integer.parseInt(item_split[0]);
            material = Material.getMaterial(id);
        } else {
            material = Material.getMaterial(item_split[0].toUpperCase());
        }
        if (item_split.length == 2) {
            data = (short) Integer.parseInt(item_split[1]);
        }
        if (material == null) {
            return null;
        }
        final ItemBuilder item = new ItemBuilder(material, data);
        final String[] split = all_item_split;
        for (int i = 1; i < split.length; ++i) {
            if (split[i].startsWith("name:")) {
                final String title = split[i].replace("name:", "");
                item.setTitle(title);
            }
            if (split[i].startsWith("lore:")) {
                final String lore = split[i].replace("lore:", "");
                item.addLore(lore);
            }
            for (final Enchantment enchant : Enchantment.values()) {
                if (split[i].toUpperCase().startsWith(enchant.getName().toUpperCase())) {
                    final int level = Integer.parseInt(split[i].replace(enchant.getName().toUpperCase() + ":", ""));
                    item.addEnchantment(enchant, level);
                }
            }
            if (split[i].startsWith("leather_color:")) {
                final String colors = split[i].replace("leather_color:", "");
                final String[] RGB = colors.split("-");
                final Color color = Color.fromRGB(Integer.parseInt(RGB[0]), Integer.parseInt(RGB[1]), Integer.parseInt(RGB[2]));
                item.setColor(color);
            }
        }
        return item;
    }

    public static boolean isNumeric(final String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
