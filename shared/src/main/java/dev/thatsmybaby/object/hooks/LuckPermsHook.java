package dev.thatsmybaby.object.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsHook {

    private static LuckPerms luckPerms;

    public static void init() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            luckPerms = provider.getProvider();
        }
    }

    public static boolean canHook() {
        return Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
    }

    private static CachedMetaData getCachedMetaData(Player player) {
        return luckPerms.getPlayerAdapter(Player.class).getUser(player).getCachedData().getMetaData();
    }

    public static String getPlayerPrefix(Player player) {
        return getPlayerPrefix(player, true);
    }

    public static String getPlayerPrefix(Player player, boolean includeName) {
        if (!canHook()) {
            return ChatColor.WHITE + (includeName ? player.getName() : "");
        }

        CachedMetaData cachedMetaData = getCachedMetaData(player);

        if (cachedMetaData.getPrefix() == null) {
            return ChatColor.WHITE + (includeName ? player.getName() : "");
        }

        String prefix = ChatColor.translateAlternateColorCodes('&', cachedMetaData.getPrefix());

        return includeName ? ChatColor.getLastColors(prefix) + player.getName() : prefix;
    }

    public static String getPlayerSuffix(Player player) {
        if (!canHook()) {
            return ChatColor.WHITE + player.getName();
        }

        CachedMetaData cachedMetaData = getCachedMetaData(player);

        if (cachedMetaData.getSuffix() == null) {
            return ChatColor.WHITE + player.getName();
        }

        return ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', cachedMetaData.getSuffix())) + player.getName();
    }

    public static String getPlayerGroup(Player player) {
        if (!canHook()) {
            return "Default";
        }

        return luckPerms.getPlayerAdapter(Player.class).getUser(player).getPrimaryGroup();
    }
}