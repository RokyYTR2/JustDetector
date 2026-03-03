package dev.meyba.justDetector.listeners;

import dev.meyba.justDetector.JustDetector;
import dev.meyba.justDetector.managers.PlayerDataManager;
import dev.meyba.justDetector.utils.ChatUtil;
import dev.meyba.justDetector.utils.ClientDetector;
import dev.meyba.justDetector.utils.DiscordWebhook;
import dev.meyba.justDetector.utils.ModChannelDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;

import java.util.*;

public class PlayerDetectionListener implements Listener {
    private static final Set<String> DEFAULT_IGNORED_CHANNELS = Set.of(
            "minecraft:brand",
            "minecraft:register",
            "minecraft:unregister"
    );
    private static final Set<String> DEFAULT_IGNORED_NAMESPACES = Set.of(
            "minecraft",
            "bungeecord",
            "velocity"
    );
    private static final String INFERRED_VERSION = "inferred";

    private final JustDetector plugin;
    private final PlayerDataManager playerDataManager;
    private final ChatUtil chatUtil;

    public PlayerDetectionListener(JustDetector plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
        this.chatUtil = plugin.getChatUtil();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            scanPluginChannels(player);
        }, 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            detectAndBroadcast(player);
        }, 40L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastModInfo(player);
        }, 60L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDataManager.removePlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) {
        handlePluginChannel(event.getPlayer(), event.getChannel());
    }

    @EventHandler
    public void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent event) {
        String normalized = normalizeChannel(event.getChannel());
        if (normalized.isEmpty()) {
            return;
        }
        PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(event.getPlayer());
        data.removePluginChannel(normalized);
    }

    private void detectAndBroadcast(Player player) {
        String brand;

        brand = player.getClientBrandName();

        String clientType = ClientDetector.getClientType(brand);
        String version = ClientDetector.getMinecraftVersion(player);

        PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);
        data.setBrand(brand);
        data.setClientType(clientType);
        data.setVersion(version);

        boolean showClientType = plugin.getConfig().getBoolean("detection.show-client-type", true);
        boolean showVersion = plugin.getConfig().getBoolean("detection.show-version", true);
        boolean showBrand = plugin.getConfig().getBoolean("detection.show-brand", true);
        boolean broadcastToOps = plugin.getConfig().getBoolean("detection.broadcast-to-ops", true);
        boolean logToConsole = plugin.getConfig().getBoolean("detection.log-to-console", true);

        if (isBlockedClient(clientType, brand)) {
            handleBlockedClient(player, clientType, brand, broadcastToOps, logToConsole);
            return;
        }

        String finalMessage = chatUtil.getMessageWithPrefix("join.player-joined").replace("%player%", player.getName());
        StringBuilder detectionInfo = new StringBuilder();

        if (showClientType) {
            detectionInfo.append("\n").append(chatUtil.getMessageWithPrefix("detection.client-detected")
                    .replace("%client_type%", clientType));
        }

        if (showVersion) {
            detectionInfo.append("\n").append(chatUtil.getMessageWithPrefix("detection.version-detected")
                    .replace("%version%", version));
        }

        if (showBrand && brand != null) {
            detectionInfo.append("\n").append(chatUtil.getMessageWithPrefix("detection.brand-detected")
                    .replace("%brand%", brand));
        }

        String broadcastMessage = finalMessage + detectionInfo.toString();

        if (broadcastToOps) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp()) {
                    onlinePlayer.sendMessage(broadcastMessage);
                }
            }
        }

        if (logToConsole) {
            plugin.getLogger().info("Player " + player.getName() + " joined with client: " + clientType +
                    (brand != null ? " (Brand: " + brand + ")" : ""));
        }
    }

    private void scanPluginChannels(Player player) {
        if (!isChannelDetectionEnabled()) {
            return;
        }

        for (String channel : player.getListeningPluginChannels()) {
            handlePluginChannel(player, channel);
        }
    }

    private void handlePluginChannel(Player player, String channel) {
        if (!isChannelDetectionEnabled()) {
            return;
        }

        String normalized = normalizeChannel(channel);
        if (normalized.isEmpty()) {
            return;
        }

        Set<String> ignoredChannels = getNormalizedSet(
                "detection.channel-detection.ignored-channels",
                DEFAULT_IGNORED_CHANNELS
        );
        if (ignoredChannels.contains(normalized)) {
            return;
        }

        PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);
        boolean added = data.addPluginChannel(normalized);
        if (!added) {
            return;
        }

        ModChannelDatabase.ModInfo knownMod = ModChannelDatabase.lookup(normalized);
        if (knownMod != null) {
            if (!data.getMods().containsKey(knownMod.getModId())) {
                data.addMod(knownMod.getModId(), INFERRED_VERSION);
                data.setModsDetected(true);
            }
            return;
        }

        if (shouldInferModIds()) {
            Set<String> ignoredNamespaces = getNormalizedSet(
                    "detection.channel-detection.ignored-namespaces",
                    DEFAULT_IGNORED_NAMESPACES
            );
            String modId = inferModId(normalized, ignoredNamespaces);
            if (modId != null && !data.getMods().containsKey(modId)) {
                data.addMod(modId, INFERRED_VERSION);
                data.setModsDetected(true);
            }
        }
    }

    private boolean isChannelDetectionEnabled() {
        return plugin.getConfig().getBoolean("detection.channel-detection.enabled", true);
    }

    private boolean shouldInferModIds() {
        return plugin.getConfig().getBoolean("detection.channel-detection.infer-mod-ids", true);
    }

    private String inferModId(String channel, Set<String> ignoredNamespaces) {
        String modId = channel;
        int colon = channel.indexOf(':');
        if (colon > 0) {
            modId = channel.substring(0, colon);
        } else {
            int pipe = channel.indexOf('|');
            if (pipe > 0) {
                modId = channel.substring(0, pipe);
            }
        }

        if (modId.isBlank()) {
            return null;
        }
        if (ignoredNamespaces.contains(modId)) {
            return null;
        }
        return modId;
    }

    private Set<String> getNormalizedSet(String path, Set<String> fallback) {
        List<String> list = plugin.getConfig().getStringList(path);
        if (list == null || list.isEmpty()) {
            return fallback;
        }
        Set<String> normalized = new HashSet<>();
        for (String item : list) {
            if (item == null || item.isBlank()) {
                continue;
            }
            normalized.add(normalizeChannel(item));
        }
        return normalized.isEmpty() ? fallback : normalized;
    }

    private String normalizeChannel(String channel) {
        if (channel == null) {
            return "";
        }
        return channel.trim().toLowerCase(Locale.ROOT);
    }

    private void broadcastModInfo(Player player) {
        PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);

        DiscordWebhook.sendDetection(plugin, player, data);

        boolean blocked = handleBlockedMods(player, data);
        if (blocked) {
            return;
        }

        if (!plugin.getConfig().getBoolean("detection.show-mods", true)) {
            return;
        }

        if (data.getModCount() > 0) {
            boolean broadcastToOps = plugin.getConfig().getBoolean("detection.broadcast-to-ops", true);
            boolean logToConsole = plugin.getConfig().getBoolean("detection.log-to-console", true);

            List<String> modEntries = new java.util.ArrayList<>();
            for (Map.Entry<String, String> entry : data.getMods().entrySet()) {
                String ver = entry.getValue();
                if (ver == null || ver.equals("?") || ver.equals("inferred")) {
                    modEntries.add(entry.getKey());
                } else {
                    modEntries.add(entry.getKey() + " (" + ver + ")");
                }
            }
            String modList = String.join(", ", modEntries);

            String message = chatUtil.getMessageWithPrefix("detection.mods-detected-list")
                    .replace("%mods%", modList);

            if (broadcastToOps) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.isOp()) {
                        onlinePlayer.sendMessage(message);
                    }
                }
            }

            if (logToConsole) {
                plugin.getLogger().info("Detected " + data.getModCount() + " mods for player " + player.getName() + ": " + modList);
            }
        }
    }

    private boolean isBlockedClient(String clientType, String brand) {
        for (String blocked : plugin.getConfig().getStringList("detection.blocked-clients")) {
            if (blocked == null || blocked.isBlank()) {
                continue;
            }
            String needle = blocked.toLowerCase();
            if (clientType != null && clientType.toLowerCase().contains(needle)) {
                return true;
            }
            if (brand != null && brand.toLowerCase().contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private void handleBlockedClient(Player player, String clientType, String brand, boolean broadcastToOps,
                                     boolean logToConsole) {
        String clientLabel = clientType != null ? clientType : "Unknown";
        String brandLabel = brand != null ? brand : "Unknown";

        String alertMessage = chatUtil.getMessageWithPrefix("detection.blocked-client-alert")
                .replace("%player%", player.getName())
                .replace("%client%", clientLabel)
                .replace("%brand%", brandLabel);

        if (!alertMessage.isEmpty() && broadcastToOps) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp()) {
                    onlinePlayer.sendMessage(alertMessage);
                }
            }
        }

        if (logToConsole) {
            plugin.getLogger().warning("Blocked client detected for " + player.getName() + ": " + clientLabel +
                    (brand != null ? " (Brand: " + brand + ")" : ""));
        }

        if (plugin.getConfig().getBoolean("detection.blocked-client-kick", true) &&
                !player.hasPermission("justdetector.bypass")) {
            String kickMessage = chatUtil.getMessage("detection.blocked-client-kick");
            if (kickMessage.isEmpty()) {
                kickMessage = "Blocked client.";
            }
            player.kickPlayer(kickMessage);
        }
    }

    private boolean handleBlockedMods(Player player, PlayerDataManager.PlayerData data) {
        if (data.getMods().isEmpty()) {
            return false;
        }

        boolean broadcastToOps = plugin.getConfig().getBoolean("detection.broadcast-to-ops", true);
        boolean logToConsole = plugin.getConfig().getBoolean("detection.log-to-console", true);

        java.util.List<String> blockedMods = plugin.getConfig().getStringList("detection.blocked-mod-ids");
        if (blockedMods.isEmpty()) {
            return false;
        }

        java.util.List<String> matched = new java.util.ArrayList<>();
        for (String modId : data.getMods().keySet()) {
            String lower = modId.toLowerCase();
            for (String blocked : blockedMods) {
                if (blocked == null || blocked.isBlank()) {
                    continue;
                }
                if (lower.contains(blocked.toLowerCase())) {
                    matched.add(modId);
                    break;
                }
            }
        }

        if (matched.isEmpty()) {
            return false;
        }

        String list = String.join(", ", matched);
        String alertMessage = chatUtil.getMessageWithPrefix("detection.blocked-mod-alert")
                .replace("%player%", player.getName())
                .replace("%mods%", list);

        if (!alertMessage.isEmpty() && broadcastToOps) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp()) {
                    onlinePlayer.sendMessage(alertMessage);
                }
            }
        }

        if (logToConsole) {
            plugin.getLogger().warning("Blocked mods detected for " + player.getName() + ": " + list);
        }

        if (plugin.getConfig().getBoolean("detection.blocked-mod-kick", true) &&
                !player.hasPermission("justdetector.bypass")) {
            String kickMessage = chatUtil.getMessage("detection.blocked-mod-kick");
            if (kickMessage.isEmpty()) {
                kickMessage = "Blocked mods.";
            }
            player.kickPlayer(kickMessage);
        }
        return true;
    }
}