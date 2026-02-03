package dev.meyba.justDetector.listeners;

import dev.meyba.justDetector.JustDetector;
import dev.meyba.justDetector.managers.PlayerDataManager;
import dev.meyba.justDetector.utils.ChatUtil;
import dev.meyba.justDetector.utils.ClientDetector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDetectionListener implements Listener {
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
            detectAndBroadcast(player);
        }, 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastModInfo(player);
        }, 60L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDataManager.removePlayerData(event.getPlayer());
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
        boolean broadcastToAll = plugin.getConfig().getBoolean("detection.broadcast-to-all", false);
        boolean logToConsole = plugin.getConfig().getBoolean("detection.log-to-console", true);

        if (isBlockedClient(clientType, brand)) {
            handleBlockedClient(player, clientType, brand, broadcastToOps, broadcastToAll, logToConsole);
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

        if (broadcastToAll) {
            Bukkit.broadcastMessage(broadcastMessage);
        } else if (broadcastToOps) {
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

    private void broadcastModInfo(Player player) {
        PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);

        if (!plugin.getConfig().getBoolean("detection.show-mods", true)) {
            return;
        }

        if (data.getModCount() > 0) {
            boolean broadcastToOps = plugin.getConfig().getBoolean("detection.broadcast-to-ops", true);
            boolean broadcastToAll = plugin.getConfig().getBoolean("detection.broadcast-to-all", false);
            boolean logToConsole = plugin.getConfig().getBoolean("detection.log-to-console", true);

            String modMessage = chatUtil.getMessageWithPrefix("detection.mods-detected-count")
                    .replace("%count%", String.valueOf(data.getModCount()));

            if (broadcastToAll) {
                Bukkit.broadcastMessage(modMessage);
            } else if (broadcastToOps) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.isOp()) {
                        onlinePlayer.sendMessage(modMessage);
                    }
                }
            }

            if (logToConsole) {
                plugin.getLogger().info("Detected " + data.getModCount() + " mods for player " + player.getName());
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
                                     boolean broadcastToAll, boolean logToConsole) {
        String clientLabel = clientType != null ? clientType : "Unknown";
        String brandLabel = brand != null ? brand : "Unknown";

        String alertMessage = chatUtil.getMessageWithPrefix("detection.blocked-client-alert")
                .replace("%player%", player.getName())
                .replace("%client%", clientLabel)
                .replace("%brand%", brandLabel);

        if (!alertMessage.isEmpty()) {
            if (broadcastToAll) {
                Bukkit.broadcastMessage(alertMessage);
            } else if (broadcastToOps) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.isOp()) {
                        onlinePlayer.sendMessage(alertMessage);
                    }
                }
            }
        }

        if (logToConsole) {
            plugin.getLogger().warning("Blocked client detected for " + player.getName() + ": " + clientLabel +
                    (brand != null ? " (Brand: " + brand + ")" : ""));
        }

        if (plugin.getConfig().getBoolean("detection.blocked-client-kick", true)) {
            String kickMessage = chatUtil.getMessage("detection.blocked-client-kick");
            if (kickMessage.isEmpty()) {
                kickMessage = "Blocked client.";
            }
            player.kickPlayer(kickMessage);
        }
    }
}
