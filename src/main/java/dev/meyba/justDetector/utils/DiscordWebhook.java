package dev.meyba.justDetector.utils;

import dev.meyba.justDetector.JustDetector;
import dev.meyba.justDetector.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscordWebhook {
    private static final int MODS_FIELD_LIMIT = 900;

    public static void sendDetection(JustDetector plugin, Player player, PlayerDataManager.PlayerData data) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("detection.discord-webhook.enabled", false)) {
            return;
        }

        String url = config.getString("detection.discord-webhook.url", "");
        if (url == null || url.isBlank()) {
            return;
        }

        String username = config.getString("detection.discord-webhook.username", "JustDetector");
        String avatarUrl = config.getString("detection.discord-webhook.avatar-url", "");
        int color = config.getInt("detection.discord-webhook.color", 0xD75555);

        String clientType = data.getClientType() != null ? data.getClientType() : "Unknown";
        String brand = data.getBrand() != null ? data.getBrand() : "Unknown";
        String version = data.getVersion() != null ? data.getVersion() : "Unknown";
        String modsValue = formatMods(data.getMods());

        String payload = buildJsonPayload(username, avatarUrl, player.getName(), clientType, brand, version, modsValue, color);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> postJson(plugin, url, payload));
    }

    private static String buildJsonPayload(String username, String avatarUrl, String playerName, String clientType,
                                           String brand, String version, String mods, int color) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"username\":\"").append(escapeJson(username)).append("\",");
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            json.append("\"avatar_url\":\"").append(escapeJson(avatarUrl)).append("\",");
        }
        json.append("\"embeds\":[{");
        json.append("\"title\":\"JustDetector Detection\",");
        json.append("\"color\":").append(color).append(",");
        json.append("\"fields\":[");
        json.append(field("Player", playerName)).append(",");
        json.append(field("Client", clientType)).append(",");
        json.append(field("Brand", brand)).append(",");
        json.append(field("Version", version)).append(",");
        json.append(field("Mods", mods));
        json.append("]}]}");
        json.append("}");
        return json.toString();
    }

    private static String field(String name, String value) {
        return "{\"name\":\"" + escapeJson(name) + "\",\"value\":\"" + escapeJson(value) + "\",\"inline\":false}";
    }

    private static String formatMods(Map<String, String> mods) {
        if (mods == null || mods.isEmpty()) {
            return "None";
        }
        List<String> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : mods.entrySet()) {
            items.add(entry.getKey() + " (" + entry.getValue() + ")");
        }
        String joined = String.join(", ", items);
        if (joined.length() > MODS_FIELD_LIMIT) {
            return joined.substring(0, MODS_FIELD_LIMIT) + "...";
        }
        return joined;
    }

    private static void postJson(JustDetector plugin, String url, String payload) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            try (OutputStream output = connection.getOutputStream()) {
                output.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int response = connection.getResponseCode();
            if (response >= 400) {
                plugin.getLogger().warning("Discord webhook responded with HTTP " + response);
            }
            connection.disconnect();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to send Discord webhook: " + e.getMessage());
        }
    }

    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}