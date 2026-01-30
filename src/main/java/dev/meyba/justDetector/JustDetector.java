package dev.meyba.justDetector;

import dev.meyba.justDetector.commands.DetectorCommands;
import dev.meyba.justDetector.listeners.ModDetectionListener;
import dev.meyba.justDetector.listeners.PlayerDetectionListener;
import dev.meyba.justDetector.managers.PlayerDataManager;
import dev.meyba.justDetector.utils.ChatUtil;
import dev.meyba.justDetector.utils.VersionChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class JustDetector extends JavaPlugin {
    private PlayerDataManager playerDataManager;
    private ChatUtil chatUtil;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chatUtil = new ChatUtil(this);

        playerDataManager = new PlayerDataManager();

        getCommand("detector").setExecutor(new DetectorCommands(this, playerDataManager));

        getServer().getPluginManager().registerEvents(new PlayerDetectionListener(this, playerDataManager), this);

        registerPluginChannels();

        new VersionChecker(this, "RokyYTR2", "JustDetector").checkForUpdates();

        getLogger().info("JustDetector has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JustDetector has been disabled!");
    }

    private void registerPluginChannels() {
        ModDetectionListener modListener = new ModDetectionListener(this, playerDataManager);

        getServer().getMessenger().registerIncomingPluginChannel(this, "fml:handshake", modListener);
        getServer().getMessenger().registerIncomingPluginChannel(this, "fml:loginwrapper", modListener);
    }

    public ChatUtil getChatUtil() {
        return chatUtil;
    }
}