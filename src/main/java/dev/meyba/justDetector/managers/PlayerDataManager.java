package dev.meyba.justDetector.managers;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {
    private final Map<UUID, PlayerData> playerDataMap;

    public PlayerDataManager() {
        this.playerDataMap = new HashMap<>();
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), k -> new PlayerData(player.getUniqueId()));
    }

    public void removePlayerData(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }

    public static class PlayerData {
        private final UUID uuid;
        private String clientType;
        private String version;
        private String brand;
        private final Map<String, String> mods;
        private final Set<String> pluginChannels;
        private boolean modsDetected;

        public PlayerData(UUID uuid) {
            this.uuid = uuid;
            this.mods = new HashMap<>();
            this.pluginChannels = new HashSet<>();
            this.modsDetected = false;
        }

        public String getClientType() {
            return clientType;
        }

        public void setClientType(String clientType) {
            this.clientType = clientType;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public Map<String, String> getMods() {
            return mods;
        }

        public void addMod(String modId, String version) {
            mods.put(modId, version);
        }

        public void setModsDetected(boolean modsDetected) {
            this.modsDetected = modsDetected;
        }

        public int getModCount() {
            return mods.size();
        }

        public boolean addPluginChannel(String channel) {
            return pluginChannels.add(channel);
        }

        public void removePluginChannel(String channel) {
            pluginChannels.remove(channel);
        }
    }
}