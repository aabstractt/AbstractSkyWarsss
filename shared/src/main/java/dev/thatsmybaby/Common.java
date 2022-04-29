package dev.thatsmybaby;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Common {

    private static final Map<String, String> messages = new HashMap<>();

    public static void loadMessages(File folder) throws IOException, InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();

        configuration.load(new File(folder, "messages.yml"));

        for (String key : configuration.getKeys(false)) {
            messages.put(key, configuration.getString(key));
        }
    }

    public static String translateString(String text, String... args) {
        if (messages.containsKey(text)) {
            text = messages.get(text);
        }

        for (int i = 0; i < args.length; i++) {
            text = text.replaceAll("\\{%" + i + "}", args[i]);
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean isUnderDevelopment() {
        return true;
    }
}