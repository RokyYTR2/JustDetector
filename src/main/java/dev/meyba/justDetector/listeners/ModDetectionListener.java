package dev.meyba.justDetector.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.meyba.justDetector.JustDetector;
import dev.meyba.justDetector.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;

public class ModDetectionListener implements PluginMessageListener {
    private final JustDetector plugin;
    private final PlayerDataManager playerDataManager;

    public ModDetectionListener(JustDetector plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("fml:handshake")) {
            handleForgeHandshake(player, message);
        } else if (channel.equals("fml:loginwrapper")) {
            handleForgeLoginWrapper(player, message);
        }
    }

    private void handleForgeHandshake(Player player, byte[] message) {
        try {
            PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);

            if (message.length > 1) {
                ByteArrayDataInput in = ByteStreams.newDataInput(message);
                byte discriminator = in.readByte();

                if (discriminator == 2) {
                    data.clearMods();

                    try {
                        int modCount = in.readByte() & 0xFF;

                        for (int i = 0; i < modCount; i++) {
                            try {
                                String modId = in.readUTF();
                                String modVersion = in.readUTF();
                                data.addMod(modId, modVersion);
                            } catch (Exception e) {
                                break;
                            }
                        }

                        data.setModsDetected(true);
                        plugin.getLogger().info("Detected " + data.getModCount() + " mods for player " + player.getName());
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error parsing mod list for " + player.getName() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error handling Forge handshake: " + e.getMessage());
        }
    }

    private void handleForgeLoginWrapper(Player player, byte[] message) {
        try {
            PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);

            if (message.length > 0) {
                String messageStr = new String(message, StandardCharsets.UTF_8);

                if (messageStr.contains("modList") || messageStr.contains("mods")) {
                    data.setModsDetected(true);
                    plugin.getLogger().info("Detected Forge mods in login wrapper for " + player.getName());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error handling Forge login wrapper: " + e.getMessage());
        }
    }
}