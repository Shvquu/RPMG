package de.voxellabs.resourcepack.resourcepack.hook;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import lombok.Getter;
import org.bukkit.entity.Player;

public class ViaVersionHook {

    private final RPMG plugin;
    @Getter
    private boolean viaVersionEnabled = false;

    // Minimale Protokoll-Version die Resourcepacks unterstützt (Minecraft 1.7 = Protokoll 5)
    // Für Prompt-Support (1.17+) = Protokoll 755
    private static final int PROTOCOL_1_17 = 755;
    // Für Resourcepack-Support (1.7+) = Protokoll 5
    private static final int PROTOCOL_1_7 = 5;

    public ViaVersionHook(RPMG plugin) {
        this.plugin = plugin;
        detectViaVersion();
    }

    private void detectViaVersion() {
        if (plugin.getServer().getPluginManager().getPlugin("ViaVersion") != null) {
            viaVersionEnabled = true;
            plugin.getLogger().info("ViaVersion gefunden — Client-Versionen werden erkannt.");
        } else {
            plugin.getLogger().info("ViaVersion nicht gefunden — Client-Versionserkennung deaktiviert.");
        }
    }

    /**
     * Gibt die Protokoll-Version des Spielers zurück.
     * Ohne ViaVersion wird -1 zurückgegeben.
     */
    public int getProtocolVersion(Player player) {
        if (!viaVersionEnabled) return -1;

        try {
            Class<?> viaAPI = Class.forName("com.viaversion.viaversion.api.Via");
            Object api = viaAPI.getMethod("getAPI").invoke(null);
            return (int) api.getClass()
                    .getMethod("getPlayerVersion", Player.class)
                    .invoke(api, player);
        } catch (Exception e) {
            plugin.getLogger().warning("ViaVersion API Fehler: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Prüft ob der Spieler Resourcepacks empfangen kann (Minecraft 1.7+).
     */
    public boolean supportsResourcePack(Player player) {
        int protocol = getProtocolVersion(player);
        if (protocol == -1) return true; // Kein ViaVersion → annehmen dass es geht
        return protocol >= PROTOCOL_1_7;
    }

    /**
     * Prüft ob der Spieler den Prompt-Text anzeigen kann (Minecraft 1.17+).
     */
    public boolean supportsPrompt(Player player) {
        int protocol = getProtocolVersion(player);
        if (protocol == -1) return true; // Kein ViaVersion → annehmen dass es geht
        return protocol >= PROTOCOL_1_17;
    }

    /**
     * Gibt eine lesbare Minecraft-Version zur Protokoll-Nummer zurück.
     * Deckt die gängigsten Versionen ab.
     */
    public String getMinecraftVersion(int protocol) {
        switch (protocol) {
            case 767: return "1.21";
            case 766: return "1.20.6";
            case 765: return "1.20.4";
            case 764: return "1.20.2";
            case 763: return "1.20.1";
            case 762: return "1.19.4";
            case 761: return "1.19.3";
            case 760: return "1.19.1/1.19.2";
            case 759: return "1.19";
            case 758: return "1.18.2";
            case 757: return "1.18/1.18.1";
            case 756: return "1.17.1";
            case 755: return "1.17";
            case 754: return "1.16.4/1.16.5";
            case 753: return "1.16.3";
            case 751: return "1.16.2";
            case 736: return "1.16.1";
            case 735: return "1.16";
            case 578: return "1.15.2";
            case 575: return "1.15.1";
            case 573: return "1.15";
            case 498: return "1.14.4";
            case 477: return "1.14";
            case 404: return "1.13.2";
            case 401: return "1.13.1";
            case 393: return "1.13";
            case 340: return "1.12.2";
            case 338: return "1.12.1";
            case 335: return "1.12";
            case 316: return "1.11.2";
            case 315: return "1.11";
            case 210: return "1.10";
            case 110: return "1.9.4";
            case 109: return "1.9.2";
            case 107: return "1.9";
            case 47:  return "1.8.x";
            case 5:   return "1.7.x";
            default:  return "Unbekannt (Protokoll " + protocol + ")";
        }
    }

}