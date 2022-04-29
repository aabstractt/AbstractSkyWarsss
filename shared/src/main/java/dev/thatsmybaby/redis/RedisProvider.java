package dev.thatsmybaby.redis;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.thatsmybaby.object.RedisGameArena;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.*;

public class RedisProvider {

    public final static String GAMES_HASH = "sw_games";
    public final static String GAMES_STATUS_HASH = "sw_games_status:%s";
    public final static String PLAYER_MAP_HASH = "sw_player_map:%s";

    @Getter
    private final static RedisProvider instance = new RedisProvider();
    @Getter
    private static RedisTransaction redisTransaction;

    protected JedisPool jedisPool;

    public void init(String address, String password) {
        String[] addressSplit = address.split(":");
        String host = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : Protocol.DEFAULT_PORT;

        this.jedisPool = new JedisPool(new JedisPoolConfig() {{
            setMaxWaitMillis(1000 * 200000);
        }}, host, port, 1000 * 10);

        Jedis jedis = this.jedisPool.getResource();

        if (password != null && !password.isEmpty()) {
            jedis.auth(password);
        }

        redisTransaction = new RedisTransaction(this.jedisPool, password);
    }

    public void updateMap(String serverName, String folderName, String mapName, Integer status, Integer players, Integer maxSlots, boolean started) {
        redisTransaction.runTransaction(jedis -> {
            if (started) {
                return;
            }

            String hash = serverName + "%" + mapName;

            if (!jedis.sismember(GAMES_HASH, hash)) {
                jedis.sadd(GAMES_HASH, hash);
            }

            jedis.hset(String.format(GAMES_STATUS_HASH, hash), new HashMap<String, String>() {{
                put("folderName", folderName);
                put("mapName", mapName);
                put("serverName", serverName);
                put("status", status.toString());
                put("playersCount", players.toString());
                put("maxSlots", maxSlots.toString());
            }});
        });
    }

    public void removeMap(String serverName, String mapName) {
        redisTransaction.runTransaction(jedis -> {
            String hash = serverName + "%" + mapName;

            if (jedis.sismember(GAMES_HASH, hash)) {
                jedis.srem(GAMES_HASH, hash);
            }

            if (jedis.exists(String.format(GAMES_STATUS_HASH, hash))) {
                jedis.hdel(String.format(GAMES_STATUS_HASH, hash), jedis.hgetAll(String.format(GAMES_STATUS_HASH, hash)).keySet().toArray(new String[0]));
            }
        });
    }

    public List<RedisGameArena> getArenasAvailable() {
        return redisTransaction.runTransaction(jedis -> {
            List<RedisGameArena> arenas = new ArrayList<>();

            for (String hash : jedis.smembers(GAMES_HASH)) {
                if (!jedis.exists(String.format(GAMES_STATUS_HASH, hash))) {
                    continue;
                }

                Map<String, String> storage = jedis.hgetAll(String.format(GAMES_STATUS_HASH, hash));

                arenas.add(new RedisGameArena(
                        storage.get("serverName"),
                        storage.get("folderName"),
                        storage.get("mapName"),
                        Integer.parseInt(storage.get("status")),
                        Integer.parseInt(storage.get("playersCount")),
                        Integer.parseInt(storage.get("maxSlots"))
                ));
            }

            return arenas;
        });
    }

    public String getPlayerMap(UUID uniqueId) {
        return redisTransaction.runTransaction(jedis -> {
            return jedis.get(String.format(PLAYER_MAP_HASH, uniqueId.toString()));
        });
    }

    public void setPlayerMap(UUID uniqueId, String mapName) {
        this.setPlayerMap(uniqueId.toString(), mapName);
    }

    public void setPlayerMap(String uniqueId, String mapName) {
        redisTransaction.runTransaction(jedis -> {
            if (mapName != null) {
                jedis.set(String.format(PLAYER_MAP_HASH, uniqueId), mapName);
            } else {
                jedis.del(String.format(PLAYER_MAP_HASH, uniqueId));
            }
        });
    }

    public void connectTo(JavaPlugin plugin, Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(serverName);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}