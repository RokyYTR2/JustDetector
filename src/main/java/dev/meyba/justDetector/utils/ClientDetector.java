package dev.meyba.justDetector.utils;

import org.bukkit.entity.Player;

public class ClientDetector {
    public static String getClientType(String brand) {
        if (brand == null || brand.isEmpty()) {
            return "Unknown";
        }

        String lower = brand.toLowerCase();

        if (lower.contains("meteor")) return "Meteor Client";
        if (lower.contains("wurst")) return "Wurst Client";
        if (lower.contains("liquidbounce")) return "LiquidBounce";
        if (lower.contains("aristois")) return "Aristois";
        if (lower.contains("xynis")) return "Xynis";
        if (lower.contains("inertia")) return "Inertia Client";
        if (lower.contains("impact")) return "Impact";
        if (lower.contains("rusherhack")) return "RusherHack";
        if (lower.contains("future")) return "Future Client";
        if (lower.contains("sigma")) return "Sigma Client";
        if (lower.contains("novoline")) return "Novoline";

        if (lower.contains("neoforge")) return "NeoForge";
        if (lower.contains("quilt")) return "Quilt";
        if (lower.contains("fabric")) return "Fabric";
        if (lower.contains("forge") || lower.contains("fml")) return "Forge";

        if (lower.contains("lunarclient") || lower.contains("lunar")) return "Lunar Client";
        if (lower.contains("badlion") || lower.contains("blc")) return "Badlion Client";
        if (lower.contains("feather")) return "Feather Client";
        if (lower.contains("labymod")) return "LabyMod";
        if (lower.contains("pvplounge")) return "PvPLounge";
        if (lower.contains("essential")) return "Essential";
        if (lower.contains("cosmic")) return "Cosmic Client";
        if (lower.contains("salwyrr")) return "Salwyrr Client";
        if (lower.contains("cheatbreaker")) return "CheatBreaker";
        if (lower.contains("batmod")) return "BatMod";

        if (lower.contains("optifine")) return "OptiFine";

        if (lower.contains("vanilla") || lower.equals("minecraft")) return "Vanilla";

        return "Modified (" + brand + ")";
    }

    public static String getMinecraftVersion(Player player) {
        int protocol = player.getProtocolVersion();

        return switch (protocol) {
            case 774 -> "1.21.11";
            case 773 -> "1.21.9/1.21.10";
            case 772 -> "1.21.7/1.21.8";
            case 771 -> "1.21.6";
            case 770 -> "1.21.5";
            case 769 -> "1.21.4";
            case 768 -> "1.21.2/1.21.3";
            case 767 -> "1.21/1.21.1";
            case 766 -> "1.20.5/1.20.6";
            case 765 -> "1.20.3/1.20.4";
            case 764 -> "1.20.2";
            case 763 -> "1.20/1.20.1";
            case 762 -> "1.19.4";
            case 761 -> "1.19.3";
            case 760 -> "1.19.1/1.19.2";
            case 759 -> "1.19";
            case 758 -> "1.18.2";
            case 757 -> "1.18/1.18.1";
            case 756 -> "1.17.1";
            case 755 -> "1.17";
            case 754 -> "1.16.4/1.16.5";
            case 753 -> "1.16.3";
            case 751 -> "1.16.2";
            case 736 -> "1.16.1";
            case 735 -> "1.16";
            case 578 -> "1.15.2";
            case 575 -> "1.15.1";
            case 573 -> "1.15";
            case 498 -> "1.14.4";
            case 490 -> "1.14.3";
            case 485 -> "1.14.2";
            case 480 -> "1.14.1";
            case 477 -> "1.14";
            case 404 -> "1.13.2";
            case 401 -> "1.13.1";
            case 393 -> "1.13";
            case 340 -> "1.12.2";
            case 338 -> "1.12.1";
            case 335 -> "1.12";
            case 316 -> "1.11.2";
            case 315 -> "1.11";
            case 210 -> "1.10.2";
            case 110 -> "1.9.4";
            case 109 -> "1.9.2";
            case 107 -> "1.9";
            case 47 -> "1.8.x";
            default -> "Protocol " + protocol;
        };
    }
}