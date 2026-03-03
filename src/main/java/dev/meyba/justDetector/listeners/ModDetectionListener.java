package dev.meyba.justDetector.listeners;

import dev.meyba.justDetector.JustDetector;
import dev.meyba.justDetector.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ModDetectionListener implements PluginMessageListener {
    private final JustDetector plugin;
    private final PlayerDataManager playerDataManager;

    public ModDetectionListener(JustDetector plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (message == null || message.length == 0) {
            return;
        }

        String lower = channel.toLowerCase();

        if (lower.equals("fml:handshake") || lower.equals("forge:handshake")) {
            handleForgeHandshake(player, message, channel);
            return;
        }

        if (lower.equals("fml:loginwrapper") || lower.equals("forge:loginwrapper")) {
            handleForgeLoginWrapper(player, message);
            return;
        }

        if (lower.startsWith("neoforge:")) {
            handleNeoForge(player, message, channel);
            return;
        }

        if (lower.equals("fabric:registry/sync") || lower.startsWith("fabric:")) {
            handleFabricChannel(player, message, channel);
            return;
        }

        tryGenericModListParse(player, message, channel);
    }

    private void handleForgeHandshake(Player player, byte[] message, String channel) {
        try {
            if (tryParseVarIntHandshake(player, message)) {
                return;
            }
            if (tryParseLegacyHandshake(player, message)) {
                return;
            }
            tryParseExtendedHandshake(player, message);
        } catch (Exception e) {
            plugin.getLogger().warning("Error handling Forge handshake on " + channel + ": " + e.getMessage());
        }
    }

    private boolean tryParseVarIntHandshake(Player player, byte[] message) {
        if (message.length < 2) {
            return false;
        }

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message);
            int discriminator = readVarInt(in);

            if (discriminator == 2) {
                int modCount = readVarInt(in);
                if (modCount < 0 || modCount > 5000) {
                    return false;
                }
                Map<String, String> mods = new LinkedHashMap<>();
                for (int i = 0; i < modCount; i++) {
                    String modId = readVarIntString(in);
                    String modVersion = readVarIntString(in);
                    mods.put(modId, modVersion);
                }
                applyMods(player, mods);
                return true;
            }

            if (discriminator == 1 || discriminator == 3) {
                return tryParseRegistryData(player, in);
            }

            return false;
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
            if (modCount < 0 || modCount > 255) {
                return false;
            }
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

    private boolean tryParseExtendedHandshake(Player player, byte[] message) {
        if (message.length < 3) {
            return false;
        }

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message);
            int discriminator = readVarInt(in);

            if (discriminator == 0) {
                int remaining = in.available();
                if (remaining < 2) return false;

                int count = readVarInt(in);
                if (count <= 0 || count > 5000) return false;

                Map<String, String> mods = new LinkedHashMap<>();
                for (int i = 0; i < count; i++) {
                    String entry = readVarIntString(in);
                    int sep = entry.indexOf(':');
                    if (sep > 0 && sep < entry.length() - 1) {
                        mods.put(entry.substring(0, sep), entry.substring(sep + 1));
                    } else {
                        mods.put(entry, "?");
                    }
                }
                if (!mods.isEmpty()) {
                    applyMods(player, mods);
                    return true;
                }
            }

            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean tryParseRegistryData(Player player, ByteArrayInputStream in) {
        try {
            int count = readVarInt(in);
            if (count <= 0 || count > 5000) return false;

            Map<String, String> entries = new LinkedHashMap<>();
            for (int i = 0; i < count; i++) {
                String entry = readVarIntString(in);
                entries.put(entry, "?");
            }

            if (!entries.isEmpty()) {
                PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);
                for (Map.Entry<String, String> entry : entries.entrySet()) {
                    String key = entry.getKey();
                    int colon = key.indexOf(':');
                    if (colon > 0) {
                        String ns = key.substring(0, colon);
                        if (!ns.equals("minecraft") && !ns.equals("forge") && !ns.equals("fml")) {
                            if (!data.getMods().containsKey(ns)) {
                                data.addMod(ns, "?");
                                data.setModsDetected(true);
                            }
                        }
                    }
                }
                return !entries.isEmpty();
            }
        } catch (Exception ignored) {}

        return false;
    }

    private void handleNeoForge(Player player, byte[] message, String channel) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message);
            int count = readVarInt(in);
            if (count > 0 && count < 5000) {
                Map<String, String> mods = new LinkedHashMap<>();
                for (int i = 0; i < count; i++) {
                    String entry = readVarIntString(in);
                    mods.put(entry, "?");
                }
                if (!mods.isEmpty()) {
                    applyMods(player, mods);
                }
            }
        } catch (Exception ignored) {}
    }

    private void handleFabricChannel(Player player, byte[] message, String channel) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message);
            if (message.length > 4) {
                int count = readVarInt(in);
                if (count > 0 && count < 5000) {
                    Map<String, String> entries = new LinkedHashMap<>();
                    for (int i = 0; i < count; i++) {
                        String entry = readVarIntString(in);
                        entries.put(entry, "?");
                    }
                    if (!entries.isEmpty()) {
                        applyMods(player, entries);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void handleForgeLoginWrapper(Player player, byte[] message) {
        try {
            WrapperPayload payload = unwrapLoginWrapper(message);
            if (payload == null) {
                return;
            }

            String lower = payload.channel.toLowerCase();
            if (lower.equals("fml:handshake") || lower.equals("forge:handshake")) {
                handleForgeHandshake(player, payload.payload, payload.channel);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error handling Forge login wrapper: " + e.getMessage());
        }
    }

    private void tryGenericModListParse(Player player, byte[] message, String channel) {
        if (message.length < 3) return;

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(message);
            int possibleCount = readVarInt(in);
            if (possibleCount > 0 && possibleCount < 500 && in.available() > possibleCount) {
                Map<String, String> mods = new LinkedHashMap<>();
                for (int i = 0; i < possibleCount; i++) {
                    String entry = readVarIntString(in);
                    if (entry.length() > 100) return;
                    mods.put(entry, "?");
                }
                if (mods.size() > 1) {
                    applyMods(player, mods);
                }
            }
        } catch (Exception ignored) {}
    }

    private void applyMods(Player player, Map<String, String> mods) {
        if (mods.isEmpty()) return;

        PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(player);
        boolean addedAny = false;
        for (Map.Entry<String, String> entry : mods.entrySet()) {
            List<String> modIds = extractModIds(entry.getKey());
            if (modIds.isEmpty()) {
                continue;
            }

            String version = normalizeVersion(entry.getValue());
            for (String modId : modIds) {
                if (!data.getMods().containsKey(modId)) {
                    data.addMod(modId, version);
                    addedAny = true;
                }
            }
        }
        if (addedAny) {
            data.setModsDetected(true);
        }
    }

    private List<String> extractModIds(String rawModId) {
        List<String> result = new ArrayList<>();
        if (rawModId == null || rawModId.isBlank()) {
            return result;
        }

        String candidate = rawModId.trim();
        if ((candidate.startsWith("[") && candidate.endsWith("]"))
                || (candidate.startsWith("{") && candidate.endsWith("}"))) {
            candidate = candidate.substring(1, candidate.length() - 1).trim();
        }

        String[] parts = candidate.split(",");
        for (String part : parts) {
            String cleaned = sanitizeModId(part);
            if (cleaned != null && !result.contains(cleaned)) {
                result.add(cleaned);
            }
        }

        return result;
    }

    private String sanitizeModId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String value = token.trim();
        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
            value = value.substring(1, value.length() - 1).trim();
        }

        int atIndex = value.indexOf('@');
        if (atIndex > 0) {
            value = value.substring(0, atIndex).trim();
        }

        int equalsIndex = value.indexOf('=');
        if (equalsIndex > 0) {
            value = value.substring(0, equalsIndex).trim();
        }

        int colonIndex = value.indexOf(':');
        if (colonIndex > 0) {
            value = value.substring(0, colonIndex).trim();
        }

        value = value.toLowerCase(Locale.ROOT);
        if (value.length() < 2 || value.length() > 64) {
            return null;
        }
        if (value.matches("\\d+")) {
            return null;
        }
        if (!value.matches("[a-z0-9_.-]+")) {
            return null;
        }
        if (value.equals("minecraft") || value.equals("forge") || value.equals("fml")) {
            return null;
        }
        return value;
    }

    private String normalizeVersion(String rawVersion) {
        if (rawVersion == null || rawVersion.isBlank()) {
            return "?";
        }

        String version = rawVersion.trim();
        if ((version.startsWith("[") && version.endsWith("]"))
                || (version.startsWith("{") && version.endsWith("}"))) {
            version = version.substring(1, version.length() - 1).trim();
        }
        if (version.isBlank() || version.matches("\\d+")) {
            return "?";
        }
        return version;
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
        if (length < 0 || length > 32767) {
            throw new IOException("Invalid string length: " + length);
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