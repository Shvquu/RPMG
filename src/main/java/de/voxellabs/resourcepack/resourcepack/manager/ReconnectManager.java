package de.voxellabs.resourcepack.resourcepack.manager;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import de.voxellabs.resourcepack.resourcepack.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ReconnectManager {

    private final RPMG plugin;

    // Spieler mit aktiven Wiederholungsversuchen: UUID → Anzahl verbleibende Versuche
    private final Map<UUID, Integer> pendingRetries = new ConcurrentHashMap<>();
    // Laufende Countdown-Tasks: UUID → Task
    private final Map<UUID, BukkitTask> countdownTasks = new ConcurrentHashMap<>();

    @Getter
    private int maxRetries;
    @Getter
    private int retryDelay;   // in Sekunden
    private String retryMessage;
    private String retryExpiredMessage;

    public ReconnectManager(RPMG plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        maxRetries       = plugin.getConfig().getInt("reconnect.max-retries", 2);
        retryDelay       = plugin.getConfig().getInt("reconnect.retry-delay", 10);
        retryMessage     = plugin.getConfig().getString("messages.retry-message",
                "&eResourcepack abgelehnt! Du hast noch &c%retries% Versuch(e)&e. Bitte erneut senden in &c%delay%s&e...");
        retryExpiredMessage = plugin.getConfig().getString("messages.retry-expired-message",
                "&cAlle Versuche aufgebraucht. Du wirst gekickt.");
    }

    /**
     * Wird aufgerufen wenn ein Spieler das Pack ablehnt.
     * Gibt ihm einen weiteren Versuch oder kickt ihn.
     *
     * @return true wenn der Spieler einen Retry bekommt, false wenn er direkt gekickt wird
     */
    public boolean handleDecline(Player player) {
        if (maxRetries <= 0) {
            // Reconnect-Feature deaktiviert
            return false;
        }

        UUID uuid = player.getUniqueId();
        int retriesLeft = pendingRetries.getOrDefault(uuid, maxRetries);

        if (retriesLeft <= 0) {
            // Keine Versuche mehr übrig
            cleanup(uuid);
            return false;
        }

        // Versuche reduzieren
        pendingRetries.put(uuid, retriesLeft - 1);

        // Nachricht senden
        String msg = retryMessage
                .replace("%retries%", String.valueOf(retriesLeft - 1))
                .replace("%delay%", String.valueOf(retryDelay));
        player.sendMessage(ColorUtils.colorize(msg));

        plugin.getLogger().info(player.getName() + " hat das Resourcepack abgelehnt. "
                + (retriesLeft - 1) + " Versuch(e) verbleibend.");

        // Countdown starten: nach X Sekunden Pack erneut senden
        cancelExistingTask(uuid);

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                cleanup(uuid);
                return;
            }

            int remaining = pendingRetries.getOrDefault(uuid, 0);

            if (remaining <= 0) {
                // Letzter Versuch aufgebraucht → kicken
                player.sendMessage(ColorUtils.colorize(retryExpiredMessage));
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        String rawKick = plugin.getConfig().getString("messages.kick-message",
                                "&c&lResourcepack abgelehnt!");
                        player.kickPlayer(ColorUtils.colorize(rawKick));
                        plugin.getLogger().info(player.getName()
                                + " wurde nach Ablauf aller Versuche gekickt.");
                    }
                }, 40L);
                cleanup(uuid);
            } else {
                // Pack erneut senden
                plugin.getLogger().info("Sende Resourcepack erneut an " + player.getName()
                        + " (" + remaining + " Versuch(e) verbleibend).");
                plugin.getPackManager().sendResourcePack(player);
            }

            countdownTasks.remove(uuid);

        }, retryDelay * 20L); // Sekunden → Ticks

        countdownTasks.put(uuid, task);
        return true;
    }

    /**
     * Wird aufgerufen wenn der Spieler das Pack erfolgreich geladen hat.
     * Bereinigt alle offenen Retry-Einträge.
     */
    public void onSuccess(Player player) {
        cleanup(player.getUniqueId());
    }

    /**
     * Wird aufgerufen wenn der Spieler den Server verlässt.
     */
    public void onQuit(Player player) {
        cleanup(player.getUniqueId());
    }

    private void cancelExistingTask(UUID uuid) {
        BukkitTask existing = countdownTasks.remove(uuid);
        if (existing != null) {
            existing.cancel();
        }
    }

    private void cleanup(UUID uuid) {
        pendingRetries.remove(uuid);
        cancelExistingTask(uuid);
    }

    public boolean isEnabled() {
        return maxRetries > 0;
    }

}