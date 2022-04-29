package dev.thatsmybaby.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class RedisGameArena {

    private final String serverName;

    private final String folderName;

    private final String mapName;

    @Setter
    private int status;
    @Setter
    private int players;
    @Setter
    private int maxSlots;

    public String getHash() {
        return this.serverName + "%" + this.mapName;
    }
}