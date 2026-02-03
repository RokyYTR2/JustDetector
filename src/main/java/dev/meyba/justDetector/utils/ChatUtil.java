package dev.meyba.justDetector.utils;

import dev.meyba.justDetector.JustDetector;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private final JustDetector plugin;

    public ChatUtil(JustDetector plugin) {
        this.plugin = plugin;
    }

    public String getMessage(String path) {
        String message = plugin.getConfig().getString("messages." + path);
        if (message == null) {
            return "";
        }
        return colorize(message);
    }

    public String getMessageWithPrefix(String path) {
        String prefix = plugin.getConfig().getString("prefix", "");
        return colorize(prefix + getMessage(path));
    }

    public String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        String withHex = translateHexColorCodes(message);
        return ChatColor.translateAlternateColorCodes('&', withHex);
    }

    private String translateHexColorCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
