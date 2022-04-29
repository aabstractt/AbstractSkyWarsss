package dev.thatsmybaby.nametag;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.thatsmybaby.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NameTag {

    private final static Map<Player, String> prefixcache = Maps.newConcurrentMap();

    private final static Map<Player, Integer> prioritycache = Maps.newConcurrentMap();

    private final static Map<Player, String> suffixcache = Maps.newConcurrentMap();

    private final static List<Player> loaded = Lists.newLinkedList();

    public static void setPrefix(Player user, String prefix) {
        if (loaded.contains(user))
            setNametag(prefix, suffixcache.get(user), prioritycache.get(user), user);
    }

    public static void setPrefix(Player user, String prefix, List<Player> players) {
        if (loaded.contains(user))
            setNametag(prefix, suffixcache.get(user), prioritycache.get(user), user, players);
    }

    public static void setSuffix(Player user, String suffix) {
        if (loaded.contains(user))
            setNametag(prefixcache.get(user), suffix, prioritycache.get(user), user);
    }

    public static void setSuffix(Player user, String suffix, List<Player> players) {
        if (loaded.contains(user))
            setNametag(prefixcache.get(user), suffix, prioritycache.get(user), user, players);
    }

    public static void setNametag(String prefix, String suffix, Integer priority, Player user) {
        voidForSettingTab(prefix, suffix, priority, user, null);
    }

    public static void setNametag(String prefix, String suffix, Integer priority, Player user, List<Player> players) {
        voidForSettingTab(prefix, suffix, priority, user, players);

        System.out.println("Prefix > " + prefix);
    }

    public static void clearCache() {
        suffixcache.clear();
        prefixcache.clear();
        prioritycache.clear();
        loaded.clear();
    }

    public static void clearCache(Player player) {
        suffixcache.remove(player);
        prefixcache.remove(player);
        prioritycache.remove(player);
        loaded.remove(player);
    }

    public static String getSuffix(Player user) {
        String suffix = suffixcache.get(user);
        if (suffix != null)
            return suffix;
        throw new NullPointerException("suffix cached for player " + user.getName());
    }

    public static Integer getPriority(Player user) {
        Integer suffix = prioritycache.get(user);
        if (suffix != null)
            return suffix;
        throw new NullPointerException("priority cached for player " + user.getName());
    }

    public static String getPrefix(Player user) {
        String prefix = prefixcache.get(user);
        if (prefix != null)
            return prefix;
        throw new NullPointerException("prefix cached for player " + user.getName());
    }

    private static void voidForSettingTab(String prefix, String suffix, Integer priority, Player user, List<Player> players) {
        clearTabStyle(user, priority, players);

        loaded.add(user);
        String team_name = priority + user.getName();

        if (team_name.length() > 16)
            team_name = team_name.substring(0, 16);
        if (suffix.length() > 16)
            suffix = suffix.substring(0, 16);
        if (prefix.length() > 16)
            prefix = prefix.substring(0, 16);
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        suffix = ChatColor.translateAlternateColorCodes('&', suffix);

        System.out.println("Prefix > " + prefix);
        System.out.println("Suffix > " + suffix);
        try {
            //setPlayerListName(user, prefix + user.getName() + suffix, players);
            prefixcache.put(user, prefix);
            suffixcache.put(user, suffix);
            prioritycache.put(user, priority);
            Constructor<?> constructor = Objects.requireNonNull(Utils.getNMSClass("PacketPlayOutScoreboardTeam")).getConstructor();
            Object packet = constructor.newInstance();
            List<String> contents = new ArrayList<>();
            contents.add(user.getName());
            try {
                Utils.setFieldValue(packet.getClass(), "a", packet, team_name);
                Utils.setFieldValue(packet.getClass(), "b", packet, team_name);
                Utils.setFieldValue(packet.getClass(), "c", packet, prefix);
                Utils.setFieldValue(packet.getClass(), "d", packet, suffix);
                Utils.setFieldValue(packet.getClass(), "e", packet, "ALWAYS");
                Utils.setFieldValue(packet.getClass(), "h", packet, 0);
                Utils.setFieldValue(packet.getClass(), "g", packet, contents);
            } catch (Exception ex) {
                Utils.setFieldValue(packet.getClass(), "a", packet, team_name);
                Utils.setFieldValue(packet.getClass(), "b", packet, team_name);
                Utils.setFieldValue(packet.getClass(), "c", packet, prefix);
                Utils.setFieldValue(packet.getClass(), "d", packet, suffix);
                Utils.setFieldValue(packet.getClass(), "e", packet, "ALWAYS");
                Utils.setFieldValue(packet.getClass(), "i", packet, 0);
                Utils.setFieldValue(packet.getClass(), "h", packet, contents);
            }
            if (players == null) {
                for (Player t : Bukkit.getOnlinePlayers())
                    sendPacket(t, packet);
            } else {
                for (Player p : players)
                    sendPacket(p, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO FOR LENGTH
    //private void setPlayerListName(Player user, String name, List<Player> ps) {}

    private static void clearTabStyle(Player user, Integer priority, List<Player> players) {
        try {
            String team_name = priority + user.getName();
            if (team_name.length() > 16)
                team_name = team_name.substring(0, 16);
            Constructor<?> constructor = Objects.requireNonNull(Utils.getNMSClass("PacketPlayOutScoreboardTeam")).getConstructor();
            Object packet = constructor.newInstance();
            List<String> contents = new ArrayList<>();
            contents.add(priority + user.getName());
            try {
                Utils.setFieldValue(packet.getClass(), "a", packet, team_name);
                Utils.setFieldValue(packet.getClass(), "b", packet, team_name);
                Utils.setFieldValue(packet.getClass(), "e", packet, "ALWAYS");
                Utils.setFieldValue(packet.getClass(), "h", packet, 1);
                Utils.setFieldValue(packet.getClass(), "g", packet, contents);
            } catch (Exception ex) {
                Utils.setFieldValue(packet.getClass(), "a", packet, team_name);
                Utils.setFieldValue(packet.getClass(), "b", packet, team_name);
                Utils.setFieldValue(packet.getClass(), "e", packet, "ALWAYS");
                Utils.setFieldValue(packet.getClass(), "i", packet, 1);
                Utils.setFieldValue(packet.getClass(), "h", packet, contents);
            }
            if (players == null) {
                for (Player t : Bukkit.getOnlinePlayers())
                    sendPacket(t, packet);
            } else {
                for (Player p : players)
                    sendPacket(p, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendPacket(Player to, Object packet) {
        try {
            Object playerHandle = to.getClass().getMethod("getHandle").invoke(to);

            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);

            playerConnection.getClass().getMethod("sendPacket", Utils.getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static enum NameTags {

        OWNER,
        ADMIN,
        DEV,
        MOD,
        HELPER,
        DEFAULT;
    }
}
