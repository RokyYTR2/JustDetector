package dev.meyba.justDetector.utils;

import dev.meyba.justDetector.JustDetector;
import org.bukkit.ChatColor;

public class ChatUtil {
    private final JustDetector plugin;

    public ChatUtil(JustDetector plugin) {
        this.plugin = plugin;
    }

    public String getMessage(String path) {
        String message = plugin.getConfig().getString("messages." + path);
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessageWithPrefix(String path) {
        String prefix = plugin.getConfig().getString("prefix", "");
        return ChatColor.translateAlternateColorCodes('&', prefix + getMessage(path));
    }
}
