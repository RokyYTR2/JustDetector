package dev.meyba.justDetector.utils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ModChannelDatabase {
    private static final Map<String, ModInfo> CHANNEL_TO_MOD = new LinkedHashMap<>();
    private static final Map<String, ModInfo> PREFIX_TO_MOD = new LinkedHashMap<>();

    static {
        channel("fabric-screen-handler-api-v1:open_screen", "fabric-api", "Fabric API");
        channel("fabric:registry/sync", "fabric-api", "Fabric API");
        channel("fabric-networking-api-v1:early_registration", "fabric-api", "Fabric API");
        prefix("sodium", "sodium", "Sodium");
        prefix("indium", "indium", "Indium");
        prefix("iris", "iris", "Iris Shaders");
        prefix("lithium", "lithium", "Lithium");
        prefix("phosphor", "phosphor", "Phosphor");
        prefix("starlight", "starlight", "Starlight");
        prefix("modmenu", "modmenu", "Mod Menu");
        prefix("carpet", "carpet", "Carpet");
        prefix("worldedit", "worldedit", "WorldEdit");
        prefix("viafabric", "viafabric", "ViaFabric");
        prefix("viaversion", "viaversion", "ViaVersion");
        prefix("viabackwards", "viabackwards", "ViaBackwards");
        prefix("voicechat", "voicechat", "Simple Voice Chat");
        prefix("plasmo:voice", "plasmovoice", "Plasmo Voice");
        channel("plasmo:voice/v2", "plasmovoice", "Plasmo Voice");
        prefix("xaeromap", "xaeromap", "Xaero's Map");
        prefix("xaero-map", "xaeromap", "Xaero's Map");
        prefix("xaerominimap", "xaerominimap", "Xaero's Minimap");
        prefix("xaero-minimap", "xaerominimap", "Xaero's Minimap");
        prefix("journeymap", "journeymap", "JourneyMap");
        prefix("dynmap", "dynmap", "Dynmap");
        prefix("bluemap", "bluemap", "BlueMap");
        prefix("squaremap", "squaremap", "Squaremap");
        prefix("replaymod", "replaymod", "ReplayMod");
        prefix("meteor-client", "meteor-client", "Meteor Client");
        prefix("meteorclient", "meteor-client", "Meteor Client");
        prefix("wurst", "wurst", "Wurst Client");
        prefix("inertia", "inertia", "Inertia Client");
        prefix("aristois", "aristois", "Aristois");
        prefix("xynis", "xynis", "Xynis");
        prefix("liquidbounce", "liquidbounce", "LiquidBounce");
        prefix("rusherhack", "rusherhack", "RusherHack");
        prefix("impact", "impact", "Impact");
        prefix("future", "futureclient", "Future Client");
        prefix("baritone", "baritone", "Baritone");
        prefix("tweakeroo", "tweakeroo", "Tweakeroo");
        prefix("litematica", "litematica", "Litematica");
        prefix("minihud", "minihud", "MiniHUD");
        prefix("malilib", "malilib", "MaLiLib");
        prefix("itemscroller", "itemscroller", "Item Scroller");
        prefix("worlddownloader", "worlddownloader", "World Downloader");
        prefix("wdl", "worlddownloader", "World Downloader");
        prefix("schematica", "schematica", "Schematica");
        prefix("inventorysorter", "inventorysorter", "Inventory Sorter");
        prefix("roughlyenoughitems", "roughlyenoughitems", "Roughly Enough Items");
        prefix("rei", "roughlyenoughitems", "Roughly Enough Items");
        prefix("jei", "jei", "Just Enough Items");
        prefix("emi", "emi", "EMI");
        prefix("capes", "capes", "Capes Mod");
        prefix("ears", "ears", "Ears");
        prefix("skinlayers", "skinlayers", "3D Skin Layers");
        prefix("emotecraft", "emotecraft", "Emotecraft");
        prefix("debugify", "debugify", "Debugify");
        prefix("ferritecore", "ferritecore", "FerriteCore");
        prefix("lazydfu", "lazydfu", "LazyDFU");
        prefix("smoothboot", "smoothboot", "Smooth Boot");
        channel("fml:handshake", "forge", "Forge");
        channel("fml:loginwrapper", "forge", "Forge");
        channel("forge:handshake", "forge", "Forge");
        channel("forge:loginwrapper", "forge", "Forge");
        channel("fml:play", "forge", "Forge");
        channel("forge:tier_sorting", "forge", "Forge");
        channel("forge:split", "forge", "Forge");
        prefix("neoforge", "neoforge", "NeoForge");
        prefix("optifine", "optifine", "OptiFine");
        prefix("create", "create", "Create");
        prefix("jei", "jei", "Just Enough Items");
        prefix("waila", "waila", "WAILA");
        prefix("hwyla", "hwyla", "HWYLA");
        prefix("jade", "jade", "Jade");
        prefix("curios", "curios", "Curios API");
        prefix("apotheosis", "apotheosis", "Apotheosis");
        prefix("architectury", "architectury", "Architectury");
        prefix("tconstruct", "tconstruct", "Tinkers' Construct");
        prefix("botania", "botania", "Botania");
        prefix("ae2", "ae2", "Applied Energistics 2");
        prefix("mekanism", "mekanism", "Mekanism");
        prefix("thermal", "thermal", "Thermal Series");
        prefix("immersiveengineering", "immersiveengineering", "Immersive Engineering");
        prefix("essential", "essential", "Essential");
        prefix("mousewheelie", "mousewheelie", "Mouse Wheelie");
        prefix("labymod3", "labymod", "LabyMod");
        channel("labymod3:main", "labymod", "LabyMod");
        prefix("labymod", "labymod", "LabyMod");
        prefix("lunarclient", "lunarclient", "Lunar Client");
        channel("lunar:apollo", "lunarclient", "Lunar Client");
        prefix("badlion", "badlion", "Badlion Client");
        prefix("badoptimizations", "badoptimizations", "BadOptimizations");
        prefix("clumps", "clumps", "Clumps");
        prefix("consumableoptimizer", "consumableoptimizer", "Consumable Optimizer");
        prefix("consumable_optimizer", "consumableoptimizer", "Consumable Optimizer");
        prefix("forgeconfigapiport", "forgeconfigapiport", "Forge Config API Port");
        prefix("fpsreducer", "fpsreducer", "FpsReducer");
        prefix("ias", "ias", "In-Game Account Switcher");
        prefix("immediatelyfast", "immediatelyfast", "ImmediatelyFast");
        prefix("marlowscrystaloptimizer", "marlowscrystaloptimizer", "Marlow's Crystal Optimizer");
        prefix("mousetweaks", "mousetweaks", "Mouse Tweaks");
        prefix("nochatreports", "nochatreports", "No Chat Reports");
        prefix("shieldfixes", "shieldfixes", "Shield Fixes");
        prefix("shield_fixes", "shieldfixes", "Shield Fixes");
        prefix("shieldstatus", "shieldstatus", "Shield Status");
        prefix("shield_status", "shieldstatus", "Shield Status");
        prefix("viafabricplus", "viafabricplus", "ViaFabricPlus");
        prefix("walksylib", "walksylib", "Walksy Lib");
        prefix("zoomify", "zoomify", "Zoomify");
        prefix("appleskin", "appleskin", "AppleSkin");
        prefix("badpackets", "badpackets", "BadPackets");
        prefix("betterhurtcam", "betterhurtcam", "BetterHurtCam");
        prefix("c2me", "c2me", "C2ME");
        prefix("cloth-config", "cloth-config", "Cloth Config");
        prefix("cloth_config", "cloth-config", "Cloth Config");
        prefix("continuity", "continuity", "Continuity");
        prefix("crosshairindicator", "crosshairindicator", "Crosshair Indicator");
        prefix("cullleaves", "cullleaves", "Cull Leaves");
        prefix("dynamicfps", "dynamicfps", "Dynamic FPS");
        prefix("effecttimerplus", "effecttimerplus", "EffectTimerPlus");
        prefix("enhancedattackindicator", "enhancedattackindicator", "Enhanced Attack Indicator");
        prefix("entityculling", "entityculling", "Entity Culling");
        prefix("fabric-language-kotlin", "fabric-language-kotlin", "Fabric Language Kotlin");
        prefix("fast-ip-ping", "fast-ip-ping", "Fast IP Ping");
        prefix("fastquit", "fastquit", "FastQuit");
        prefix("herosanchoroptimizer", "herosanchoroptimizer", "Hero's Anchor Optimizer");
        prefix("krypton", "krypton", "Krypton");
        prefix("languagereload", "languagereload", "Language Reload");
        prefix("lazylanguageloader", "lazylanguageloader", "Lazy Language Loader");
        prefix("lazy-language-loader", "lazylanguageloader", "Lazy Language Loader");
        prefix("modelfix", "modelfix", "Model Fix");
        prefix("modernfix", "modernfix", "ModernFix");
        prefix("moreculling", "moreculling", "More Culling");
        prefix("no-telemetry", "notelemetry", "No Telemetry");
        prefix("notelemetry", "notelemetry", "No Telemetry");
        prefix("packetfixer", "packetfixer", "Packet Fixer");
        prefix("placeholderapi", "placeholderapi", "PlaceholderAPI");
        prefix("placeholder-api", "placeholderapi", "PlaceholderAPI");
        prefix("reeses-sodium-options", "reeses-sodium-options", "Reese's Sodium Options");
        prefix("rrls", "rrls", "Remove Reloading Screen");
        prefix("shulkerboxtooltip", "shulkerboxtooltip", "ShulkerBoxTooltip");
        prefix("smoothgui", "smoothgui", "Smooth GUI");
        prefix("smoothscroll", "smoothscroll", "Smooth Scroll");
        prefix("sodium-extra", "sodium-extra", "Sodium Extra");
        prefix("threadtweak", "threadtweak", "ThreadTweak");
        prefix("ukulib", "ukulib", "UkuLib");
        prefix("ukus-armor-hud", "ukus-armor-hud", "Uku's Armor HUD");
        prefix("armorhud", "ukus-armor-hud", "Uku's Armor HUD");
        prefix("vmp", "vmp", "VMP");
        prefix("yacl", "yacl", "YetAnotherConfigLib");
        prefix("yet-another-config-lib", "yacl", "YetAnotherConfigLib");
        prefix("bookshelf", "bookshelf", "Bookshelf");
        prefix("iceberg", "iceberg", "Iceberg");
        prefix("legendarytooltips", "legendarytooltips", "Legendary Tooltips");
        prefix("prism", "prism", "Prism");
        prefix("feather", "feather", "Feather Client");
        channel("voicechat:main", "voicechat", "Simple Voice Chat");
        prefix("simplevoicechat", "voicechat", "Simple Voice Chat");
        prefix("xaeroworldmap", "xaeroworldmap", "Xaero's World Map");
        prefix("xaerosworldmap", "xaeroworldmap", "Xaero's World Map");
        prefix("xaero-world-map", "xaeroworldmap", "Xaero's World Map");
        prefix("bobby", "bobby", "Bobby");
        prefix("lambdynamiclights", "lambdynamiclights", "LambDynamicLights");
        prefix("sodiumdynamiclights", "sodiumdynamiclights", "Sodium Dynamic Lights");
        prefix("notenoughanimations", "notenoughanimations", "Not Enough Animations");
        prefix("betterf3", "betterf3", "BetterF3");
        prefix("chatheads", "chatheads", "Chat Heads");
        prefix("chathistory", "chathistory", "Chat History");
        prefix("durabilityviewer", "durabilityviewer", "Durability Viewer");
        prefix("inventoryprofilesnext", "inventoryprofilesnext", "Inventory Profiles Next");
        prefix("invmove", "invmove", "Inventory Move");
        prefix("logical_zoom", "logicalzoom", "Logical Zoom");
        prefix("logicalzoom", "logicalzoom", "Logical Zoom");
        prefix("modchecker", "modchecker", "Mod Checker");
        prefix("modrinthify", "modrinthify", "Modrinthify");
        prefix("okzoomer", "okzoomer", "Ok Zoomer");
        prefix("presencefootsteps", "presencefootsteps", "Presence Footsteps");
        prefix("reeses_sodium_options", "reeses-sodium-options", "Reese's Sodium Options");
        prefix("status-effect-bars", "statuseffectbars", "Status Effect Bars");
        prefix("statuseffectbars", "statuseffectbars", "Status Effect Bars");
        prefix("wi_zoom", "wi_zoom", "WI Zoom");
        prefix("entity_model_features", "entitymodelfeatures", "Entity Model Features");
        prefix("entitymodelfeatures", "entitymodelfeatures", "Entity Model Features");
        prefix("entity_texture_features", "entitytexturefeatures", "Entity Texture Features");
        prefix("entitytexturefeatures", "entitytexturefeatures", "Entity Texture Features");
        prefix("memoryleakfix", "memoryleakfix", "Memory Leak Fix");
        prefix("memoryleak_fix", "memoryleakfix", "Memory Leak Fix");
        prefix("embeddium", "embeddium", "Embeddium");
        prefix("rubidium", "rubidium", "Rubidium");
        prefix("oculus", "oculus", "Oculus");
        prefix("kubejs", "kubejs", "KubeJS");
        prefix("ftblibrary", "ftblibrary", "FTB Library");
        prefix("ftbchunks", "ftbchunks", "FTB Chunks");
        prefix("ftbteams", "ftbteams", "FTB Teams");
        prefix("patchouli", "patchouli", "Patchouli");
        prefix("geckolib", "geckolib", "GeckoLib");
        prefix("citadel", "citadel", "Citadel");
        prefix("supplementaries", "supplementaries", "Supplementaries");
        prefix("farmersdelight", "farmersdelight", "Farmer's Delight");
        prefix("alexsmobs", "alexsmobs", "Alex's Mobs");
        prefix("biomesoplenty", "biomesoplenty", "Biomes O' Plenty");
        prefix("twilightforest", "twilightforest", "The Twilight Forest");
        prefix("sereneseasons", "sereneseasons", "Serene Seasons");
        prefix("createaddition", "createaddition", "Create Crafts & Additions");
        prefix("createbigcannons", "createbigcannons", "Create: Big Cannons");
        prefix("configured", "configured", "Configured");
        prefix("canary", "canary", "Canary");
        prefix("radium", "radium", "Radium Reforged");
        prefix("salwyrr", "salwyrr", "Salwyrr Client");
        prefix("cheatbreaker", "cheatbreaker", "CheatBreaker");
        prefix("pvplounge", "pvplounge", "PvPLounge");
        prefix("cosmicclient", "cosmicclient", "Cosmic Client");
        prefix("blazingpack", "blazingpack", "BlazingPack");
        prefix("melonclient", "melonclient", "Melon Client");
        prefix("paladium", "paladium", "Paladium Client");
        channel("fml:hs", "forge", "Forge");
        channel("fml:mp", "forge", "Forge");
        channel("fml:marker", "forge", "Forge");
        channel("neoforge:main", "neoforge", "NeoForge");
        channel("neoforge:network", "neoforge", "NeoForge");
        prefix("nvidium", "nvidium", "Nvidium");
        prefix("exordium", "exordium", "Exordium");
        prefix("sodiumoptionsapi", "sodiumoptionsapi", "Sodium Options API");
        prefix("sodium_extra", "sodium-extra", "Sodium Extra");
        prefix("stendhal", "stendhal", "Stendhal");
        prefix("waveycapes", "waveycapes", "WaveyCapes");
        prefix("betterpingdisplay", "betterpingdisplay", "Better Ping Display");
        prefix("bettermounthud", "bettermounthud", "Better Mount HUD");
        prefix("customhud", "customhud", "CustomHUD");
        prefix("inventoryhud", "inventoryhud", "Inventory HUD+");
        prefix("inventoryhudplus", "inventoryhud", "Inventory HUD+");
        prefix("firstperson", "firstpersonmodel", "First-Person Model");
        prefix("firstpersonmodel", "firstpersonmodel", "First-Person Model");
        prefix("freecam", "freecam", "Freecam");
        prefix("gammautils", "gammautils", "Gamma Utils");
        prefix("itemborders", "itemborders", "Item Borders");
        prefix("keystrokes", "keystrokes", "Keystrokes");
        prefix("keystrokesmod", "keystrokes", "Keystrokes");
        prefix("motionblur", "motionblur", "Motion Blur");
        prefix("modmenu-badges-lib", "modmenu-badges-lib", "Mod Menu Badges Lib");
        prefix("owo", "owo-lib", "owo-lib");
        prefix("owo-lib", "owo-lib", "owo-lib");
        prefix("trinkets", "trinkets", "Trinkets");
        prefix("cardinal-components", "cardinal-components", "Cardinal Components API");
        prefix("cardinal-components-api", "cardinal-components", "Cardinal Components API");
        prefix("balm", "balm", "Balm");
        prefix("moonlight", "moonlight", "Moonlight Lib");
        prefix("selene", "moonlight", "Moonlight Lib");
        prefix("puzzleslib", "puzzleslib", "Puzzles Lib");
        prefix("resourcefulconfig", "resourcefulconfig", "Resourceful Config");
        prefix("resourcefullib", "resourcefullib", "Resourceful Lib");
        prefix("framework", "framework", "MrCrayfish Framework");
        prefix("supermartijn642corelib", "supermartijn642corelib", "SuperMartijn642 Core Lib");
        prefix("supermartijn642configlib", "supermartijn642configlib", "SuperMartijn642 Config Lib");
        prefix("collective", "collective", "Collective");
        prefix("waystones", "waystones", "Waystones");
        prefix("gravestone", "gravestone", "Gravestone");
        prefix("tombstone", "tombstone", "Corail Tombstone");
        prefix("ars_nouveau", "ars_nouveau", "Ars Nouveau");
        prefix("bloodmagic", "bloodmagic", "Blood Magic");
        prefix("silentgear", "silentgear", "Silent Gear");
        prefix("silentlib", "silentlib", "Silent Lib");
        prefix("sophisticatedbackpacks", "sophisticatedbackpacks", "Sophisticated Backpacks");
        prefix("sophisticatedcore", "sophisticatedcore", "Sophisticated Core");
        prefix("storagedrawers", "storagedrawers", "Storage Drawers");
        prefix("sophisticatedstorage", "sophisticatedstorage", "Sophisticated Storage");
        prefix("productivebees", "productivebees", "Productive Bees");
        prefix("productive-lib", "productive-lib", "Productive Lib");
        prefix("alltheores", "alltheores", "AllTheOres");
        prefix("allthemodium", "allthemodium", "Allthemodium");
        prefix("ftbquests", "ftbquests", "FTB Quests");
        prefix("ftbultimine", "ftbultimine", "FTB Ultimine");
        prefix("ftbbackups2", "ftbbackups2", "FTB Backups 2");
        prefix("ftbessentials", "ftbessentials", "FTB Essentials");
        prefix("irons_spellbooks", "irons_spellbooks", "Iron's Spells 'n Spellbooks");
        prefix("minecolonies", "minecolonies", "MineColonies");
        prefix("structurize", "structurize", "Structurize");
        prefix("domum_ornamentum", "domum_ornamentum", "Domum Ornamentum");
        prefix("skyclientcosmetics", "skyclientcosmetics", "SkyClient Cosmetics");
        prefix("patcher", "patcher", "Patcher");
        prefix("polyfrost", "polyfrost", "Polyfrost");
        prefix("skytils", "skytils", "Skytils");
        prefix("dulkirmod", "dulkirmod", "Dulkir Mod");
        prefix("soopyv2", "soopyv2", "SoopyV2");
        prefix("neu", "notenoughupdates", "NotEnoughUpdates");
        prefix("notenoughupdates", "notenoughupdates", "NotEnoughUpdates");
        prefix("dungeonsguide", "dungeonsguide", "Dungeons Guide");
        prefix("ctjs", "ctjs", "ChatTriggers");
        prefix("chattriggers", "ctjs", "ChatTriggers");
    }

    private static void channel(String channelId, String modId, String displayName) {
        CHANNEL_TO_MOD.put(channelId.toLowerCase(Locale.ROOT), new ModInfo(modId, displayName));
    }

    private static void prefix(String channelPrefix, String modId, String displayName) {
        PREFIX_TO_MOD.put(channelPrefix.toLowerCase(Locale.ROOT), new ModInfo(modId, displayName));
    }

    public static ModInfo lookup(String channel) {
        if (channel == null || channel.isEmpty()) {
            return null;
        }
        String lower = channel.toLowerCase(Locale.ROOT);

        ModInfo exact = CHANNEL_TO_MOD.get(lower);
        if (exact != null) {
            return exact;
        }

        String namespace = lower;
        int colon = lower.indexOf(':');
        if (colon > 0) {
            namespace = lower.substring(0, colon);
        }

        ModInfo prefixMatch = PREFIX_TO_MOD.get(namespace);
        if (prefixMatch != null) {
            return prefixMatch;
        }

        for (Map.Entry<String, ModInfo> entry : PREFIX_TO_MOD.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    public static class ModInfo {
        private final String modId;
        private final String displayName;

        public ModInfo(String modId, String displayName) {
            this.modId = modId;
            this.displayName = displayName;
        }

        public String getModId() {
            return modId;
        }
    }
}