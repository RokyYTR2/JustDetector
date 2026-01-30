package dev.meyba.justDetector.utils;

import dev.meyba.justDetector.JustDetector;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ChatUtil {
    private final JustDetector plugin;
    private FileConfiguration messagesConfig = null;
    private File messagesFile = null;

    public ChatUtil(JustDetector plugin) {
        this.plugin = plugin;
        saveDefaultMessages();
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            messagesConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getMessages() {
        if (messagesConfig == null) {
            reloadMessages();
        }
        return messagesConfig;
    }

    public void saveMessages() {
        if (messagesConfig == null || messagesFile == null) {
            return;
        }
        try {
            getMessages().save(messagesFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save config to " + messagesFile);
        }
    }

    public void saveDefaultMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public String getMessage(String path) {
        String message = getMessages().getString(path);
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessageWithPrefix(String path) {
        String prefix = plugin.getConfig().getString("prefix", "");
        return ChatColor.translateAlternateColorCodes('&', prefix + getMessage(path));
    }
}