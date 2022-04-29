package dev.thatsmybaby.skywars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.world.WorldInitEvent;

public class GeneralListener implements Listener {

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent e){
        e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkInit(WorldInitEvent e){
        e.getWorld().setKeepSpawnInMemory(false);
    }
}
