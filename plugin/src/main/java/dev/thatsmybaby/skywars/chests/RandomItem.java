package dev.thatsmybaby.skywars.chests;



import dev.thatsmybaby.skywars.utils.ItemBuilder;

import java.util.Random;

public class RandomItem
{
    private double chance;
    private int min;
    private int max;
    private ItemBuilder item;
    
    public RandomItem(final double chance, final int min, final int max, final ItemBuilder item) {
        this.chance = chance;
        this.min = min;
        this.max = max;
        this.item = item;
    }
    
    public ItemBuilder getItem() {
        final int amount = new Random().nextInt(this.max - this.min + 1) + this.min;
        return this.item.setAmount(amount);
    }
    
    public boolean hasChance() {
        return new Random().nextInt(10000) < this.chance * 100.0;
    }
}
