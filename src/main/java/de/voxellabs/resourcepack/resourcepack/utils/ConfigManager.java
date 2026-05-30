package de.voxellabs.resourcepack.resourcepack.utils;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {

    private final RPMG plugin;
    private final File configFile;
    private FileConfiguration config;

    // Standard-Werte
    private static final Map<String, Object> DEFAULTS = new LinkedHashMap<>();

    static {
        DEFAULTS.put("resourcepack.url",        "https://example.com/resourcepack.zip");
        DEFAULTS.put("resourcepack.hash",        "none");
        DEFAULTS.put("resourcepack.prompt",      "&eBitte akzeptiere das Resourcepack, um den Server zu betreten!");
        DEFAULTS.put("resourcepack.enforce",     true);
        DEFAULTS.put("resourcepack.send-delay",  20);
        DEFAULTS.put("resourcepack.update-check", true);

        DEFAULTS.put("messages.kick-message",    "&c&lResourcepack abgelehnt!\n&7Du musst das Resourcepack akzeptieren,\n&7um diesen Server betreten zu können.\n&8&oTreten Sie dem Server erneut bei und akzeptieren Sie das Pack.");
        DEFAULTS.put("messages.pack-loaded",     "&a&lResourcepack erfolgreich geladen!");
        DEFAULTS.put("messages.pack-failed",     "&c&lFehler beim Laden des Resourcepacks. Bitte neu verbinden!");
        DEFAULTS.put("messages.prefix",          "&8[&6ResourcePack&8] &r");
        DEFAULTS.put("messages.reload-success",  "&aKonfiguration erfolgreich neu geladen!");
        DEFAULTS.put("messages.pack-sent",       "&aDas Resourcepack wurde an %player% gesendet!");
    }

    public ConfigManager(RPMG plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    /**
     * Lädt die Config — erstellt sie mit Kommentaren falls sie nicht existiert,
     * und ergänzt fehlende Schlüssel bei bestehender Config.
     */
    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!configFile.exists()) {
            createDefaultConfig();
            plugin.getLogger().info("config.yml wurde neu erstellt.");
        } else {
            addMissingKeys();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Schreibt die vollständige config.yml mit allen Kommentaren neu.
     */
    private void createDefaultConfig() {
        String content = buildConfigContent();
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), StandardCharsets.UTF_8)) {
            writer.write(content);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte config.yml nicht erstellen: " + e.getMessage());
            // Fallback: Bukkit-Standard saveDefaultConfig
            plugin.saveDefaultConfig();
        }
    }

    /**
     * Liest die bestehende Config und ergänzt jeden fehlenden Schlüssel
     * samt Kommentar am Ende der Datei.
     */
    private void addMissingKeys() {
        FileConfiguration existing = YamlConfiguration.loadConfiguration(configFile);
        boolean changed = false;

        StringBuilder additions = new StringBuilder();

        for (Map.Entry<String, Object> entry : DEFAULTS.entrySet()) {
            if (!existing.contains(entry.getKey())) {
                plugin.getLogger().warning("Fehlender Config-Schlüssel gefunden, wird ergänzt: " + entry.getKey());
                existing.set(entry.getKey(), entry.getValue());
                changed = true;
            }
        }

        if (changed) {
            try {
                existing.save(configFile);
                plugin.getLogger().info("Fehlende Schlüssel wurden zur config.yml hinzugefügt.");
            } catch (IOException e) {
                plugin.getLogger().severe("Konnte config.yml nicht speichern: " + e.getMessage());
            }
        }
    }

    /**
     * Baut den vollständigen Inhalt der config.yml als String auf — inklusive Kommentaren.
     */
    private String buildConfigContent() {
        return "# ============================================\n"
                + "#        ResourcePackEnforcer - Config\n"
                + "#        Version: " + plugin.getDescription().getVersion() + "\n"
                + "# ============================================\n"
                + "\n"
                + "resourcepack:\n"
                + "\n"
                + "  # Die direkte Download-URL zum Resourcepack (.zip Datei)\n"
                + "  url: \"https://example.com/resourcepack.zip\"\n"
                + "\n"
                + "  # SHA-1 Hash des Resourcepacks zur Validierung.\n"
                + "  # Leer lassen oder 'none' wenn kein Hash genutzt werden soll.\n"
                + "  hash: \"none\"\n"
                + "\n"
                + "  # Prompt-Nachricht, die dem Spieler beim Anfordern des Packs angezeigt wird.\n"
                + "  # Farb-Codes mit & werden unterstützt. (ab Minecraft 1.17+)\n"
                + "  prompt: \"&eBitte akzeptiere das Resourcepack, um den Server zu betreten!\"\n"
                + "\n"
                + "  # Soll das Resourcepack erzwungen werden?\n"
                + "  # true  → Spieler werden bei Ablehnung gekickt\n"
                + "  # false → Spieler können das Pack ablehnen\n"
                + "  enforce: true\n"
                + "\n"
                + "  # Verzögerung in Ticks bevor das Pack gesendet wird.\n"
                + "  # 20 Ticks = 1 Sekunde. Empfohlen: 20-40\n"
                + "  send-delay: 20\n"
                + "\n"
                + "  # Checkt ob es eine neue Version gibt, wenn ja. Wird das Plugin deaktiviert.\n"
                + "  update-check: true\n"
                + "\n"
                + "# ============================================\n"
                + "#                  Nachrichten\n"
                + "# ============================================\n"
                + "# Farb-Codes: & gefolgt von einem Buchstaben/Zahl\n"
                + "# Hex-Farben: &#RRGGBB (nur 1.16+)\n"
                + "# Zeilenumbruch: \\n\n"
                + "\n"
                + "messages:\n"
                + "\n"
                + "  # Kick-Nachricht wenn ein Spieler das Resourcepack ablehnt.\n"
                + "  kick-message: |-\n"
                + "    &c&lResourcepack abgelehnt!\n"
                + "    &7Du musst das Resourcepack akzeptieren,\n"
                + "    &7um diesen Server betreten zu können.\n"
                + "    &8&oTreten Sie dem Server erneut bei und akzeptieren Sie das Pack.\n"
                + "\n"
                + "  # Nachricht wenn das Resourcepack erfolgreich geladen wurde.\n"
                + "  pack-loaded: \"&a&lResourcepack erfolgreich geladen!\"\n"
                + "\n"
                + "  # Nachricht wenn der Download des Resourcepacks fehlschlägt.\n"
                + "  pack-failed: \"&c&lFehler beim Laden des Resourcepacks. Bitte neu verbinden!\"\n"
                + "\n"
                + "  # Prefix der vor allen Admin-Nachrichten angezeigt wird.\n"
                + "  prefix: \"&8[&6ResourcePack&8] &r\"\n"
                + "\n"
                + "  # Nachricht nach erfolgreichem /rp reload.\n"
                + "  reload-success: \"&aKonfiguration erfolgreich neu geladen!\"\n"
                + "\n"
                + "  # Nachricht wenn das Pack manuell an einen Spieler gesendet wird.\n"
                + "  # Platzhalter: %player%\n"
                + "  pack-sent: \"&aDas Resourcepack wurde an %player% gesendet!\"\n";
    }

    /**
     * Lädt die Config neu (z.B. nach /rp reload).
     */
    public void reload() {
        addMissingKeys();
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    // ── Getter-Methoden ────────────────────────────────────────────────────────

    public String getString(String path) {
        return config.getString(path, (String) DEFAULTS.getOrDefault(path, ""));
    }

    public String getString(String path, String fallback) {
        return config.getString(path, fallback);
    }

    public boolean getBoolean(String path) {
        Object def = DEFAULTS.getOrDefault(path, false);
        return config.getBoolean(path, def instanceof Boolean ? (Boolean) def : false);
    }

    public int getInt(String path) {
        Object def = DEFAULTS.getOrDefault(path, 0);
        return config.getInt(path, def instanceof Integer ? (Integer) def : 0);
    }

    public FileConfiguration getRaw() {
        return config;
    }
}