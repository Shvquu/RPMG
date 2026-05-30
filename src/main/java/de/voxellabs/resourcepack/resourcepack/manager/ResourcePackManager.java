package de.voxellabs.resourcepack.resourcepack.manager;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import de.voxellabs.resourcepack.resourcepack.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class ResourcePackManager {

    private final RPMG plugin;

    private String packUrl;
    private byte[] packHash;
    private String prompt;
    private boolean enforce;
    private int sendDelay;

    public ResourcePackManager(RPMG plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        packUrl = plugin.getConfigManager().getString("resourcepack.url", "");
        enforce = plugin.getConfigManager().getBoolean("resourcepack.enforce");
        sendDelay = plugin.getConfigManager().getInt("resourcepack.send-delay");
        prompt = ColorUtils.colorize(plugin.getConfigManager().getString("resourcepack.prompt", ""));

        String hashStr = plugin.getConfigManager().getString("resourcepack.hash", "none");
        if (!hashStr.equalsIgnoreCase("none") && !hashStr.isEmpty()) {
            packHash = hexToBytes(hashStr);
        } else {
            packHash = new byte[0];
        }

        plugin.getLogger().info("Resourcepack neu geladen: " + packUrl);
    }

    @SuppressWarnings("deprecation")
    public void sendResourcePack(Player player) {
        if (packUrl == null || packUrl.isEmpty() || packUrl.equals("https://example.com/resourcepack.zip")) {
            plugin.getLogger().warning("Keine gültige Resourcepack-URL konfiguriert!");
            return;
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                if (packHash != null && packHash.length > 0) {
                    if (!prompt.isEmpty()) {
                        // Mit Hash, Prompt und enforce: setResourcePack(String, byte[], boolean, String)
                        player.setResourcePack(packUrl, packHash, prompt, enforce);
                    } else {
                        // Mit Hash und enforce, ohne Prompt: setResourcePack(String, byte[], boolean)
                        player.setResourcePack(packUrl, packHash, enforce);
                    }
                } else {
                    // Nur URL: setResourcePack(String)
                    player.setResourcePack(packUrl);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Fehler beim Senden des Resourcepacks an " + player.getName() + ": " + e.getMessage());
                // Fallback auf einfaches setResourcePack
                try {
                    player.setResourcePack(packUrl);
                } catch (Exception ex) {
                    plugin.getLogger().severe("Kritischer Fehler beim Senden des Resourcepacks: " + ex.getMessage());
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
            plugin.getLogger().warning("Ungültiger SHA-1 Hash in der Config! Verwende keinen Hash.");
            return new byte[0];
        }
    }
}
