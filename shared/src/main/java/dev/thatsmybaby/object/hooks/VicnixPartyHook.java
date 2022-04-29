package dev.thatsmybaby.object.hooks;

import dev.thatsmybaby.object.VicnixProxyParty;
import dev.thatsmybaby.redis.RedisProvider;
import dev.thatsmybaby.redis.RedisTransaction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VicnixPartyHook {

    public static String HASH_PLAYER_PARTY = "player#party:%s";
    public static String HASH_PARTY_MEMBERS = "party#members:%s";
    public static String HASH_PARTY_LEADER = "party#leader:%s";

    private static final RedisTransaction redisTransaction = RedisProvider.getRedisTransaction();

    public static boolean canHook() {
        return true;
    }

    public static VicnixProxyParty getParty(UUID uniqueId) {
        try {
            return redisTransaction.runTransaction(jedis -> {
                String partyUniqueId = jedis.get(String.format(HASH_PLAYER_PARTY, uniqueId.toString()));

                if (partyUniqueId == null) {
                    return null;
                }

                String leader = jedis.get(String.format(HASH_PARTY_LEADER, partyUniqueId));

                if (leader == null) {
                    return null;
                }

                return new VicnixProxyParty(partyUniqueId, leader, getPartyMembers(partyUniqueId));
            });
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Set<String> getPartyMembers(String partyUniqueId) {
        try {
            return redisTransaction.runTransaction(jedis -> {
                return jedis.smembers(String.format(HASH_PARTY_MEMBERS, partyUniqueId));
            });
        } catch (Exception ignored) {
            return new HashSet<>();
        }
    }
}