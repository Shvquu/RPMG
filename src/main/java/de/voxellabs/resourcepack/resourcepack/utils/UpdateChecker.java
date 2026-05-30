package de.voxellabs.resourcepack.resourcepack.utils;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final RPMG plugin;

    // Dein GitHub Repository: "Username/RepoName"
    private static final String GITHUB_REPO = "DeinGitHubName/ResourcePackEnforcer";
    private static final String API_URL = "https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest";

    @Getter
    private String latestVersion = null;
    @Getter
    private boolean updateAvailable = false;

    public UpdateChecker(RPMG plugin) {
        this.plugin = plugin;
    }

    /**
     * Führt den Update-Check asynchron aus.
     * Callback wird auf dem Main-Thread aufgerufen.
     */
    public void checkAsync(UpdateCheckCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", "ResourcePackEnforcer-UpdateChecker");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    plugin.getLogger().warning("Update-Check fehlgeschlagen: HTTP " + responseCode);
                    Bukkit.getScheduler().runTask(plugin, () -> callback.onResult(false, null));
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // JSON manuell parsen (kein extra JSON-Parse-Lib nötig)
                String json = response.toString();
                String tagName = extractJsonValue(json, "tag_name");

                if (tagName == null || tagName.isEmpty()) {
                    plugin.getLogger().warning("Update-Check: Kein 'tag_name' in GitHub-Antwort gefunden.");
                    Bukkit.getScheduler().runTask(plugin, () -> callback.onResult(false, null));
                    return;
                }

                // "v" Präfix entfernen (v1.0.0 → 1.0.0)
                latestVersion = tagName.startsWith("v") ? tagName.substring(1) : tagName;
                String currentVersion = plugin.getDescription().getVersion();

                updateAvailable = isNewerVersion(latestVersion, currentVersion);

                final boolean hasUpdate = updateAvailable;
                final String foundVersion = latestVersion;

                Bukkit.getScheduler().runTask(plugin, () -> callback.onResult(hasUpdate, foundVersion));

            } catch (Exception e) {
                plugin.getLogger().warning("Update-Check Fehler: " + e.getMessage());
                Bukkit.getScheduler().runTask(plugin, () -> callback.onResult(false, null));
            }
        });
    }

    /**
     * Sendet eine Update-Nachricht an einen Spieler (z.B. Admin beim Join)
     */
    public void notifyPlayer(Player player) {
        if (!updateAvailable || latestVersion == null) return;
        if (!player.hasPermission("resourcepack.admin")) return;

        String current = plugin.getDescription().getVersion();
        player.sendMessage(ColorUtils.colorize("&8[&6ResourcePack&8] &eUpdate verfügbar! &7(&f" + current + " &7→ &a" + latestVersion + "&7)"));
        player.sendMessage(ColorUtils.colorize("&8[&6ResourcePack&8] &7Download: &fhttps://github.com/" + GITHUB_REPO + "/releases/latest"));
    }

    /**
     * Simpel Versions-Vergleich: true wenn latest > current
     */
    private boolean isNewerVersion(String latest, String current) {
        try {
            int[] latestParts = parseVersion(latest);
            int[] currentParts = parseVersion(current);
            for (int i = 0; i < Math.max(latestParts.length, currentParts.length); i++) {
                int l = i < latestParts.length ? latestParts[i] : 0;
                int c = i < currentParts.length ? currentParts[i] : 0;
                if (l > c) return true;
                if (l < c) return false;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Versionsnummern konnten nicht verglichen werden.");
        }
        return false;
    }

    private int[] parseVersion(String version) {
        // Entfernt alles nach einem "-" (z.B. 1.0.0-SNAPSHOT → 1.0.0)
        String clean = version.split("-")[0];
        String[] parts = clean.split("\\.");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    /**
     * Minimalistischer JSON-String-Extraktor für ein einzelnes Feld
     */
    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    public String getRepoUrl() { return "https://github.com/" + GITHUB_REPO + "/releases/latest"; }

    public interface UpdateCheckCallback {
        void onResult(boolean updateAvailable, String latestVersion);
    }
}
