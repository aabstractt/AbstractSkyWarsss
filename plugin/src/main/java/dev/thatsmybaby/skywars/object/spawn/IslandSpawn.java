package dev.thatsmybaby.skywars.object.spawn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter @Setter
@AllArgsConstructor
public class IslandSpawn {

    private Player byPlayer;
    private Location ballon;
    private Location location;

}
