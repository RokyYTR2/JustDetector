package dev.meyba.justDetector.commands;

import dev.meyba.justDetector.JustDetector;
import dev.meyba.justDetector.managers.PlayerDataManager;
import dev.meyba.justDetector.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class DetectorCommands implements CommandExecutor {
    private final JustDetector plugin;
    private final PlayerDataManager playerDataManager;
    private final ChatUtil chatUtil;

    public DetectorCommands(JustDetector plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
        this.chatUtil = plugin.getChatUtil();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                if (!sender.hasPermission("justdetector.reload")) {
                    sender.sendMessage(chatUtil.getMessageWithPrefix("commands.no-permission"));
                    return true;
                }
                plugin.reloadConfig();
                chatUtil.reloadMessages();
                sender.sendMessage(chatUtil.getMessageWithPrefix("commands.reload-success"));
                return true;

            case "help":
                showHelp(sender);
                return true;

            case "mods":
                if (!sender.hasPermission("justdetector.view")) {
                    sender.sendMessage(chatUtil.getMessageWithPrefix("commands.no-permission"));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(chatUtil.getMessageWithPrefix("commands.usage-mods"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage(chatUtil.getMessageWithPrefix("commands.player-not-found"));
                    return true;
                }

                showPlayerMods(sender, target);
                return true;

            default:
                sender.sendMessage(chatUtil.getMessageWithPrefix("commands.unknown-command"));
                return true;
        }
    }

    private void showHelp(CommandSender sender) {
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix", ""));
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&7ʜᴇʟᴘ ᴍᴇɴᴜ:"));
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&7/detector help - ꜱʜᴏᴡꜱ ᴛʜɪꜱ ʜᴇʟᴘ ᴍᴇɴᴜ"));
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&7/detector reload - ʀᴇʟᴏᴀᴅꜱ ᴛʜᴇ ᴄᴏɴꜰɪɢ"));
        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&7/detector mods <ᴘʟᴀʏᴇʀ> - ꜱʜᴏᴡꜱ ᴘʟᴀʏᴇʀ'ꜱ ᴍᴏᴅꜱ"));
    }

    private void showPlayerMods(CommandSender sender, Player target) {
        PlayerDataManager.PlayerData data = playerDataManager.getPlayerData(target);

        sender.sendMessage(chatUtil.getMessageWithPrefix("mods-list.header").replace("%player%", target.getName()));

        sender.sendMessage(chatUtil.getMessageWithPrefix("mods-list.client-type")
                .replace("%client_type%", (data.getClientType() != null ? data.getClientType() : "Unknown")));

        sender.sendMessage(chatUtil.getMessageWithPrefix("mods-list.brand")
                .replace("%brand%", (data.getBrand() != null ? data.getBrand() : "Unknown")));

        sender.sendMessage(chatUtil.getMessageWithPrefix("mods-list.version")
                .replace("%version%", (data.getVersion() != null ? data.getVersion() : "Unknown")));

        Map<String, String> mods = data.getMods();
        if (mods.isEmpty()) {
            sender.sendMessage(chatUtil.getMessageWithPrefix("mods-list.no-mods"));
        } else {
            sender.sendMessage(chatUtil.getMessageWithPrefix("mods-list.mods-count")
                    .replace("%count%", String.valueOf(mods.size())));
            for (Map.Entry<String, String> mod : mods.entrySet()) {
                sender.sendMessage(chatUtil.getMessageWithPrefix("mods-list.mod-format")
                        .replace("%mod_id%", mod.getKey())
                        .replace("%mod_version%", mod.getValue()));
            }
        }
    }
}