package dev.meyba.justDetector.commands;

import dev.meyba.justDetector.JustDetector;
import dev.meyba.justDetector.managers.PlayerDataManager;
import dev.meyba.justDetector.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DetectorCommands implements CommandExecutor, TabCompleter {
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

            case "check":
                if (!sender.hasPermission("justdetector.view")) {
                    sender.sendMessage(chatUtil.getMessageWithPrefix("commands.no-permission"));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(chatUtil.getMessageWithPrefix("commands.usage-check"));
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
        String prefix = plugin.getConfig().getString("prefix", "");
        sender.sendMessage(chatUtil.colorize(prefix + "&#D75555&l━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage(chatUtil.colorize(prefix + "&#D75555&lᴊ&#D75555&lᴜ&#D75555&lꜱ&#D75555&lᴛ&#D75555&lᴅ&#D75555&lᴇ&#D75555&lᴛ&#D75555&lᴇ&#D75555&lᴄ&#D75555&lᴛ&#D75555&lᴏ&#D75555&lʀ &#FFFFFFᴄᴏᴍᴍᴀɴᴅꜱ"));
        sender.sendMessage(chatUtil.colorize(prefix + "&#D75555&l━━━━━━━━━━━━━━━━━━━━"));
        sender.sendMessage("");
        sender.sendMessage(chatUtil.colorize(prefix + " &#D75555&l● &#FFFFFFᴄᴏʀᴇ"));
        sender.sendMessage(chatUtil.colorize(prefix + "   &#D24D4D/ᴊᴅ ʀᴇʟᴏᴀᴅ &#8E8E8E- &#7F7F7Fʀᴇʟᴏᴀᴅ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ"));
        sender.sendMessage(chatUtil.colorize(prefix + "   &#D24D4D/ᴊᴅ ʜᴇʟᴘ &#8E8E8E- &#7F7F7Fꜱʜᴏᴡ ᴛʜɪꜱ ᴍᴇɴᴜ"));
        sender.sendMessage("");
        sender.sendMessage(chatUtil.colorize(prefix + " &#D75555&l● &#FFFFFFᴅᴇᴛᴇᴄᴛ"));
        sender.sendMessage(chatUtil.colorize(prefix + "   &#D24D4D/ᴊᴅ ᴄʜᴇᴄᴋ <ᴘʟᴀʏᴇʀ> &#8E8E8E- &#7F7F7Fꜱʜᴏᴡ ᴄʟɪᴇɴᴛ ɪɴꜰᴏ & ᴍᴏᴅꜱ"));
        sender.sendMessage("");
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("justdetector.help")) {
                completions.add("help");
            }
            if (sender.hasPermission("justdetector.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("justdetector.view")) {
                completions.add("check");
            }
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("check") && sender.hasPermission("justdetector.view")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}