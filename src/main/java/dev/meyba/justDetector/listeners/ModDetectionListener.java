package dev.meyba.justDetector.listeners;

import dev.meyba.justDetector.JustDetector;
import dev.meyba.justDetector.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModDetectionListener implements PluginMessageListener {
    private static final String CHANNEL_FML_HANDSHAKE = "fml:handshake";
    private static final String CHANNEL_FML_LOGINWRAPPER = "fml:loginwrapper";
    private static final String CHANNEL_FORGE_HANDSHAKE = "forge:handshake";
    private static final String CHANNEL_FORGE_LOGINWRAPPER = "forge:loginwrapper";

    private final JustDetector plugin;
    private final PlayerDataManager playerDataManager;

    public ModDetectionListener(JustDetector plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (isHandshakeChannel(channel)) {
            handleForgeHandshake(player, message);
            return;
        }
        if (isLoginWrapperChannel(channel)) {
            handleForgeLoginWrapper(player, message);
        }
    }

    private boolean isHandshakeChannel(String channel) {
        return CHANNEL_FML_HANDSHAKE.equals(channel) || CHANNEL_FORGE_HANDSHAKE.equals(channel);
    }

    private boolean isLoginWrapperChannel(String channel) {
        return CHANNEL_FML_LOGINWRAPPER.equals(channel) || CHANNEL_FORGE_LOGINWRAPPER.equals(channel);
    }

    private void handleForgeHandshake(Player player, byte[] message) {
        try {
            if (tryParseVarIntHandshake(player, message)) {
                return;
            }
            if (tryParseLegacyHandshake(player, message)) {}
        } catch (Exception e) {
            plugin.getLogger().warning("Error handling Forge handshake: " + e.getMessage());
        }
    }

    private boolean tryParseVarIntHandshake(Player player, byte[] message) {
        if (message.length < 2) {
            return false;
        }

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message);
            int discriminator = readVarInt(in);
            if (discriminator != 2) {
                return false;
            }

            int modCount = readVarInt(in);
            Map<String, String> mods = new LinkedHashMap<>();

            for (int i = 0; i < modCount; i++) {
                String modId = readVarIntString(in);
                String modVersion = readVarIntString(in);
                mods.put(modId, modVersion);
            }

            applyMods(player, mods);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean tryParseLegacyHandshake(Player player, byte[] message) {
        if (message.length < 2) {
            return false;
        }

        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message))) {
            int discriminator = in.readUnsignedByte();
            if (discriminator != 2) {
                return false;
            }

            int modCount = in.readUnsignedByte();
            Map<String, String> mods = new LinkedHashMap<>();

            for (int i = 0; i < modCount; i++) {
                String modId = in.readUTF();
                String modVersion = in.readUTF();
                mods.put(modId, modVersion);
            }

            applyMods(player, mods);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void applyMods(Player player, Map<String, String> mods) {
        PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);
        data.clearMods();
        for (Map.Entry<String, String> entry : mods.entrySet()) {
            data.addMod(entry.getKey(), entry.getValue());
        }
        data.setModsDetected(true);
        plugin.getLogger().info("Detected " + data.getModCount() + " mods for player " + player.getName());
    }

    private void handleForgeLoginWrapper(Player player, byte[] message) {
        try {
            WrapperPayload payload = unwrapLoginWrapper(message);
            if (payload == null) {
                return;
            }

            if (isHandshakeChannel(payload.channel)) {
                handleForgeHandshake(player, payload.payload);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error handling Forge login wrapper: " + e.getMessage());
        }
    }

    private WrapperPayload unwrapLoginWrapper(byte[] message) throws IOException {
        if (message.length == 0) {
            return null;
        }

        ByteArrayInputStream in = new ByteArrayInputStream(message);
        in.mark(message.length);

        try {
            String channel = readVarIntString(in);
            byte[] payload = in.readAllBytes();
            return new WrapperPayload(channel, payload);
        } catch (Exception ignored) {
            in.reset();
            DataInputStream data = new DataInputStream(in);
            String channel = data.readUTF();
            byte[] payload = in.readAllBytes();
            return new WrapperPayload(channel, payload);
        }
    }

    private int readVarInt(ByteArrayInputStream in) throws IOException {
        int numRead = 0;
        int result = 0;
        int read;
        do {
            read = in.read();
            if (read == -1) {
                throw new IOException("Unexpected end of stream");
            }
            int value = read & 0x7F;
            result |= value << (7 * numRead);
            numRead++;
            if (numRead > 5) {
                throw new IOException("VarInt too big");
            }
        } while ((read & 0x80) != 0);
        return result;
    }

    private String readVarIntString(ByteArrayInputStream in) throws IOException {
        int length = readVarInt(in);
        if (length < 0) {
            throw new IOException("Invalid string length");
        }
        byte[] bytes = in.readNBytes(length);
        if (bytes.length != length) {
            throw new IOException("Unexpected end of stream");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static class WrapperPayload {
        private final String channel;
        private final byte[] payload;

        private WrapperPayload(String channel, byte[] payload) {
            this.channel = channel;
            this.payload = payload;
        }
    }
}