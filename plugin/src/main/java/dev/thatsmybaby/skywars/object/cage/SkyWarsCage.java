package dev.thatsmybaby.skywars.object.cage;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.world.World;
import dev.thatsmybaby.skywars.SkyWars;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Getter @Setter
@AllArgsConstructor
public class SkyWarsCage {
    private static Map<Player, EditSession> editSessionCache = new HashMap<>();

    private String schematic;
    private Schematic _schematic;
    private SkyWars plugin;

    public SkyWarsCage(SkyWars plugin, String schematic){
        this.schematic = schematic;
        String s = schematic.replaceAll(".schematic", "");

        File file = new File(plugin.getDataFolder(), "cages/" + s + ".schematic");

        ClipboardFormat cf = ClipboardFormat.findByFile(file);

        try {
            if (cf != null) {
                _schematic = cf.load(file);
            }
        } catch (IOException ignored) {
        }
    }

    public void paste(Player player, Location loc){
        if (_schematic == null) {
            return;
        }

        Vector v = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        World w = FaweAPI.getWorld(loc.getWorld().getName());

        EditSession editSession = _schematic.paste(w, v, true, true, null);
        editSession.flushQueue();
        editSessionCache.put(player, editSession);
    }

    public static void undoLastSession(Player player){
        if(editSessionCache.containsKey(player)){
            EditSession editSession = editSessionCache.get(player);
            editSession.undo(editSession);
            editSession.flushQueue();
            editSessionCache.remove(player);
        }
    }


}
