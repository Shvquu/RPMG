package de.voxellabs.resourcepack.resourcepack.utils;

import de.voxellabs.resourcepack.resourcepack.RPMG;

import java.net.HttpURLConnection;
import java.net.URL;

public class Validator {

    private final RPMG plugin;

    public Validator(RPMG plugin) {
        this.plugin = plugin;
    }

    /**
     * Prüft asynchron ob die primäre URL erreichbar ist.
     * Falls nicht, wird die Fallback-URL geprüft.
     * Das Ergebnis wird über den Callback zurückgegeben.
     */
    public void validateAsync(String primaryUrl, String fallbackUrl, ValidationCallback callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            plugin.getLogger().info("Prüfe Resourcepack-URL...");

            if (isUrlReachable(primaryUrl)) {
                plugin.getLogger().info("✔ Primäre URL erreichbar: " + primaryUrl);
                plugin.getServer().getScheduler().runTask(plugin,
                        () -> callback.onResult(true, primaryUrl, false));
                return;
            }

            // Primäre URL nicht erreichbar
            plugin.getLogger().warning("✘ Primäre URL nicht erreichbar: " + primaryUrl);

            // Fallback prüfen
            if (fallbackUrl != null && !fallbackUrl.isEmpty()
                    && !fallbackUrl.equalsIgnoreCase("none")
                    && !fallbackUrl.equals(primaryUrl)) {

                plugin.getLogger().warning("Prüfe Fallback-URL...");

                if (isUrlReachable(fallbackUrl)) {
                    plugin.getLogger().warning("✔ Fallback-URL erreichbar, wechsle zu: " + fallbackUrl);
                    plugin.getServer().getScheduler().runTask(plugin,
                            () -> callback.onResult(true, fallbackUrl, true));
                    return;
                }

                plugin.getLogger().severe("✘ Auch Fallback-URL nicht erreichbar: " + fallbackUrl);
            }

            // Beide URLs nicht erreichbar
            plugin.getLogger().severe("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            plugin.getLogger().severe(" WARNUNG: Resourcepack-URL nicht erreichbar!");
            plugin.getLogger().severe(" Spieler könnten das Pack nicht laden.");
            plugin.getLogger().severe(" Bitte URL in der config.yml prüfen.");
            plugin.getLogger().severe("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            plugin.getServer().getScheduler().runTask(plugin,
                    () -> callback.onResult(false, primaryUrl, false));
        });
    }

    /**
     * Führt einen HTTP HEAD-Request durch um zu prüfen ob die URL erreichbar ist.
     * Timeout: 5 Sekunden.
     */
    private boolean isUrlReachable(String urlStr) {
        if (urlStr == null || urlStr.isEmpty()
                || urlStr.equals("https://example.com/resourcepack.zip")) {
            return false;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "ResourcePackEnforcer-Validator");
            connection.setInstanceFollowRedirects(true);

            int responseCode = connection.getResponseCode();
            // 200-399 als erreichbar werten
            return responseCode >= 200 && responseCode < 400;

        } catch (Exception e) {
            plugin.getLogger().warning("URL-Check Fehler (" + urlStr + "): " + e.getMessage());
            return false;
        }
    }

    public interface ValidationCallback {
        /**
         * @param reachable   true wenn eine URL erreichbar ist
         * @param activeUrl   die aktive URL (primär oder Fallback)
         * @param usingFallback true wenn die Fallback-URL verwendet wird
         */
        void onResult(boolean reachable, String activeUrl, boolean usingFallback);
    }
}