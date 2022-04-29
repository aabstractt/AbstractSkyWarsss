package dev.thatsmybaby.redis;

import lombok.AllArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
public class RedisTransaction {

    private JedisPool jedisPool;

    private String password;

    public <T> T runTransaction(Function<Jedis, T> action) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (this.password != null) {
                jedis.auth(this.password);
            }

            return action.apply(jedis);
        }
    }

    public void runTransaction(Consumer<Jedis> action) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (this.password != null) {
                jedis.auth(this.password);
            }

            action.accept(jedis);
        }
    }
}
