package de.voxellabs.resourcepack.resourcepack.manager;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import de.voxellabs.resourcepack.resourcepack.hook.ViaVersionHook;
import de.voxellabs.resourcepack.resourcepack.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class ResourcePackManager {

    private final RPMG plugin;

    private String packUrl;
    private String fallbackUrl;
    private byte[] packHash;
    private String prompt;
    private boolean enforce;
    private int sendDelay;

    public ResourcePackManager(RPMG plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        packUrl     = plugin.getConfig().getString("resourcepack.url", "");
        fallbackUrl = plugin.getConfig().getString("resourcepack.fallback-url", "none");
        enforce     = plugin.getConfig().getBoolean("resourcepack.enforce", true);
        sendDelay   = plugin.getConfig().getInt("resourcepack.send-delay", 20);
        prompt      = ColorUtils.colorize(plugin.getConfig().getString("resourcepack.prompt", ""));

        String hashStr = plugin.getConfig().getString("resourcepack.hash", "none");
        if (hashStr != null && !hashStr.equalsIgnoreCase("none") && !hashStr.isEmpty()) {
            packHash = hexToBytes(hashStr);
        } else {
            packHash = new byte[0];
        }

        plugin.getLogger().info("Resourcepack neu geladen: " + packUrl);
    }

    /**
     * Setzt die aktive URL (wird vom PackValidator bei Fallback-Wechsel aufgerufen).
     */
    public void setActiveUrl(String url) {
        this.packUrl = url;
        plugin.getLogger().info("Aktive Resourcepack-URL gesetzt: " + url);
    }

    @SuppressWarnings("deprecation")
    public void sendResourcePack(Player player) {
        if (packUrl == null || packUrl.isEmpty()
                || packUrl.equals("https://example.com/resourcepack.zip")) {
            plugin.getLogger().warning("Keine gültige Resourcepack-URL konfiguriert!");
            return;
        }

        // ViaVersion: Unterstützt der Client überhaupt Resourcepacks?
        ViaVersionHook via = plugin.getViaVersionHook();
        if (!via.supportsResourcePack(player)) {
            int protocol = via.getProtocolVersion(player);
            plugin.getLogger().warning(player.getName() + " verbindet mit "
                    + via.getMinecraftVersion(protocol)
                    + " — Resourcepack wird nicht unterstützt, überspringe.");
            return;
        }

        // ViaVersion: Prompt nur senden wenn Client 1.17+ ist
        boolean sendPrompt = !prompt.isEmpty()
                && (via.isViaVersionEnabled() ? via.supportsPrompt(player) : true);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                if (packHash != null && packHash.length > 0) {
                    if (sendPrompt) {
                        player.setResourcePack(packUrl, packHash, prompt, enforce);
                    } else {
                        player.setResourcePack(packUrl, packHash, enforce);
                    }
                } else {
                    player.setResourcePack(packUrl);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Fehler beim Senden an " + player.getName()
                        + ": " + e.getMessage());
                try {
                    player.setResourcePack(packUrl);
                } catch (Exception ex) {
                    plugin.getLogger().severe("Kritischer Fehler beim Senden: " + ex.getMessage());
                }
            }
        }, sendDelay);
    }

    private byte[] hexToBytes(String hex) {
        if (hex == null || hex.length() % 2 != 0) return new byte[0];
        try {
            int len = hex.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                        + Character.digit(hex.charAt(i + 1), 16));
            }
            return data;
        } catch (Exception e) {
            plugin.getLogger().warning("Ungültiger SHA-1 Hash — verwende keinen Hash.");
            return new byte[0];
        }
    }
}
