package dev.meyba.justDetector.utils;

import org.bukkit.entity.Player;

public class ClientDetector {
    public static String getClientType(String brand) {
        if (brand == null || brand.isEmpty()) {
            return "Unknown";
        }

        brand = brand.toLowerCase();

        if (brand.contains("fabric")) {
            return "Fabric";
        } else if (brand.contains("forge") || brand.contains("fml")) {
            return "Forge";
        } else if (brand.contains("quilt")) {
            return "Quilt";
        } else if (brand.contains("neoforge")) {
            return "NeoForge";
        } else if (brand.contains("vanilla") || brand.equals("minecraft")) {
            return "Vanilla";
        } else if (brand.contains("optifine")) {
            return "OptiFine";
        } else if (brand.contains("lunar")) {
            return "Lunar Client";
        } else if (brand.contains("badlion")) {
            return "Badlion Client";
        } else if (brand.contains("feather")) {
            return "Feather Client";
        } else if (brand.contains("labymod")) {
            return "LabyMod";
        } else if (brand.contains("pvplounge")) {
            return "PvPLounge";
        }

        return "Modified (" + brand + ")";
    }

    public static String getMinecraftVersion(Player player) {
        int protocol = player.getProtocolVersion();

        return switch (protocol) {
            case 767 -> "1.21";
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