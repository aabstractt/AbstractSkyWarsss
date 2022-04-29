package dev.thatsmybaby.skywars.utils;

import org.bukkit.enchantments.*;
import org.bukkit.potion.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.*;

public class ItemBuilder
{
    private Material mat;
    private int amount;
    private short data;
    private String title;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;
    private Color color;
    private PotionType potion;
    private boolean potionUpgraded;
    private boolean potionExtended;
    private boolean potionSplash;
    private boolean hideFlags;
    private boolean glow;
    private String skull;
    
    public ItemBuilder(final Material mat) {
        this(mat, 1);
    }
    
    public ItemBuilder(final Material mat, final int amount) {
        this(mat, amount, (short)0);
    }
    
    public ItemBuilder(final Material mat, final short data) {
        this(mat, 1, data);
    }
    
    public ItemBuilder(final Material mat, final int amount, final short data) {
        this.title = null;
        this.lore = new ArrayList<String>();
        this.enchants = new HashMap<Enchantment, Integer>();
        this.mat = mat;
        if (this.mat == null) {
            this.mat = Material.BEDROCK;
        }
        this.amount = amount;
        this.data = data;
        this.hideFlags = false;
    }
    
    public ItemBuilder(final ItemStack item) {
        this.title = null;
        this.lore = new ArrayList<String>();
        this.enchants = new HashMap<Enchantment, Integer>();
        this.mat = item.getType();
        this.amount = item.getAmount();
        this.data = item.getDurability();
        final ItemMeta meta = item.getItemMeta();
        this.title = meta.getDisplayName();
        this.lore = (List<String>)meta.getLore();
        if (meta instanceof LeatherArmorMeta) {
            this.color = ((LeatherArmorMeta)meta).getColor();
        }
        if (meta instanceof PotionMeta) {

                final Potion p = Potion.fromItemStack(item);
                this.potion = p.getType();
                this.potionUpgraded = (p.getLevel() > 1);
                this.potionSplash = p.isSplash();
                this.potionExtended = p.hasExtendedDuration();
        }
        this.enchants.putAll(item.getEnchantments());
    }
    
    public ItemBuilder setType(final Material mat) {
        this.mat = mat;
        return this;
    }
    
    public ItemBuilder setData(final short data) {
        this.data = data;
        return this;
    }
    
    public ItemBuilder setTitle(final String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        return this;
    }
    
    public ItemBuilder addLore(final String lore) {
        this.lore.add(ChatColor.translateAlternateColorCodes('&', lore));
        return this;
    }
    
    public ItemBuilder addLore(final List<String> lore) {
        for (final String line : lore) {
            this.lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return this;
    }
    
    public ItemBuilder removeLastLoreLine() {
        this.lore.remove(this.lore.size() - 1);
        return this;
    }
    
    public ItemBuilder setLore(final List<String> lore) {
        this.lore.clear();
        for (final String line : lore) {
            this.lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return this;
    }
    
    public ItemBuilder addEnchantment(final Enchantment enchant, final int level) {
        if (this.enchants.containsKey(enchant)) {
            this.enchants.remove(enchant);
        }
        this.enchants.put(enchant, level);
        return this;
    }
    
    public ItemBuilder setColor(final Color color) {
        if (this.mat.name().contains("LEATHER_")) {
            this.color = color;
        }
        return this;
    }
    
    public ItemBuilder setHideFlags(final boolean value) {
        this.hideFlags = value;
        return this;
    }
    
    public boolean isHideFlags() {
        return this.hideFlags;
    }
    
    public boolean isGlow() {
        return this.glow;
    }
    
    public void setGlow(final boolean glow) {
        this.glow = glow;
    }
    
    public ItemBuilder setPotion(final String type, final Material potionMat, final boolean upgraded, final boolean extended) {
        this.mat = potionMat;
        try {
            if (potionMat == Material.POTION) {
                this.potionSplash = true;
            }
        }
        catch (NoSuchFieldError e) {
            this.mat = Material.POTION;
            this.potionSplash = true;
        }
        this.potion = PotionType.valueOf(type);
        this.potionUpgraded = upgraded;
        this.potionExtended = extended;
        return this;
    }
    
    public ItemBuilder setAmount(final int amount) {
        this.amount = amount;
        return this;
    }
    
    public ItemBuilder setSkullOwner(final String owner) {
        if (this.mat != Material.SKULL_ITEM) {
            this.mat = Material.SKULL_ITEM;
            this.data = 3;
        }
        this.skull = owner;
        return this;
    }
    
    public ItemStack build() {
        if (this.mat == null) {
            this.mat = Material.AIR;
        }
        final ItemStack item = new ItemStack(this.mat, this.amount, this.data);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof LeatherArmorMeta && this.color != null) {
            ((LeatherArmorMeta)meta).setColor(this.color);
        }
        if (meta instanceof SkullMeta && this.skull != null) {
            ((SkullMeta)meta).setOwner(this.skull);
        }
        if (meta instanceof PotionMeta && this.potion != null) {

                final Potion pt = new Potion(this.potion, this.potionUpgraded ? 2 : 1, this.potionSplash, this.potionExtended);
                pt.apply(item);

        }
        if (this.title != null) {
            meta.setDisplayName(this.title);
        }
        if (!this.lore.isEmpty()) {
            meta.setLore((List)this.lore);
        }
        if (this.hideFlags) {
            meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS });
        }
        if (this.glow) {
        }
        item.setItemMeta(meta);
        item.addUnsafeEnchantments((Map)this.enchants);
        return item;
    }
    
    public ItemBuilder clone() {
        final ItemBuilder newBuilder = new ItemBuilder(this.mat, this.amount, this.data);
        newBuilder.setTitle(this.title);
        newBuilder.setLore(this.lore);
        for (final Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
            newBuilder.addEnchantment(entry.getKey(), entry.getValue());
        }
        newBuilder.setColor(this.color);
        newBuilder.potion = this.potion;
        newBuilder.potionExtended = this.potionExtended;
        newBuilder.potionUpgraded = this.potionUpgraded;
        newBuilder.potionSplash = this.potionSplash;
        return newBuilder;
    }
    
    public Material getType() {
        return this.mat;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public List<String> getLore() {
        return this.lore;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public boolean hasEnchantment(final Enchantment enchant) {
        return this.enchants.containsKey(enchant);
    }
    
    public int getEnchantmentLevel(final Enchantment enchant) {
        return this.enchants.get(enchant);
    }
    
    public Map<Enchantment, Integer> getAllEnchantments() {
        return this.enchants;
    }
    
    public boolean isItem(final ItemStack item) {
        if (item == null) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        if (item.getType() != this.getType()) {
            return false;
        }
        if (!meta.hasDisplayName() && this.getTitle() != null) {
            return false;
        }
        if (!meta.getDisplayName().equals(this.getTitle())) {
            return false;
        }
        if (!meta.hasLore() && !this.getLore().isEmpty()) {
            return false;
        }
        if (meta.hasLore()) {
            for (final String lore : meta.getLore()) {
                if (!this.getLore().contains(lore)) {
                    return false;
                }
            }
        }
        for (final Enchantment enchant : item.getEnchantments().keySet()) {
            if (!this.hasEnchantment(enchant)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        String res = "";
        res += this.mat.toString();
        if (this.data != 0) {
            res = res + ":" + this.data;
        }
        if (this.amount > 1) {
            res = res + "," + this.amount;
        }
        if (this.title != null) {
            res = res + ",name:" + this.title;
        }
        if (!this.lore.isEmpty()) {
            for (final String line : this.lore) {
                res = res + ",lore:" + line;
            }
        }
        for (final Map.Entry<Enchantment, Integer> enc : this.getAllEnchantments().entrySet()) {
            res = res + "," + enc.getKey().getName() + ((enc.getValue() > 1) ? (":" + enc.getValue()) : "");
        }
        if (this.color != null) {
            res = res + ",leather_color:" + this.color.getRed() + "-" + this.color.getGreen() + "-" + this.color.getBlue();
        }
        if (this.potion != null) {
            res = res + ",potion:" + this.potion.toString() + ":" + this.potionUpgraded + ":" + this.potionExtended;
        }
        if (this.glow) {
            res += ",glowing";
        }
        return res;
    }
}
