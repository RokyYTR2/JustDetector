package dev.meyba.justDetector.utils;

import dev.meyba.justDetector.JustDetector;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private final JustDetector plugin;
    private FileConfiguration messagesConfig = null;
    private File messagesFile = null;

    public ChatUtil(JustDetector plugin) {
        this.plugin = plugin;
        saveDefaultMessages();
    }

    public String getMessage(String path) {
        String message = getMessages().getString(path);
        if (message == null) {
            return "";
        }
        return colorize(message);
    }

    public String getMessageWithPrefix(String path) {
        String prefix = plugin.getConfig().getString("prefix", "");
        return colorize(prefix + getMessage(path));
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getMessages() {
        if (messagesConfig == null) {
            reloadMessages();
        }
        return messagesConfig;
    }

    public void saveDefaultMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
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
        StringBuilder buffer = new StringBuilder();
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