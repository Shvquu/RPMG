package de.voxellabs.resourcepack.resourcepack.listeners;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import de.voxellabs.resourcepack.resourcepack.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackListener implements Listener {

    private final RPMG plugin;

    public ResourcePackListener(RPMG plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        PlayerResourcePackStatusEvent.Status status = event.getStatus();

        // Bypass-Permission prüfen
        if (player.hasPermission("resourcepack.bypass")) {
            return;
        }

        switch (status) {

            case SUCCESSFULLY_LOADED:
                // Resourcepack erfolgreich geladen
                String loadedMsg = plugin.getConfigManager().getString("messages.pack-loaded", "&a&lResourcepack erfolgreich geladen!");
                player.sendMessage(ColorUtils.colorize(loadedMsg));
                plugin.getLogger().info(player.getName() + " hat das Resourcepack akzeptiert und geladen.");
                break;

            case DECLINED:
                // Spieler hat das Resourcepack abgelehnt
                if (plugin.getPackManager().isEnforce()) {
                    kickPlayer(player);
                }
                break;

            case FAILED_DOWNLOAD:
                // Download fehlgeschlagen
                String failedMsg = plugin.getConfigManager().getString("messages.pack-failed",
                        "&c&lFehler beim Laden des Resourcepacks. Bitte neu verbinden!");
                player.sendMessage(ColorUtils.colorize(failedMsg));
                plugin.getLogger().warning(player.getName() + " hatte einen Fehler beim Laden des Resourcepacks.");

                // Optional: auch bei Fehler kicken wenn enforce aktiviert
                if (plugin.getPackManager().isEnforce()) {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (player.isOnline()) {
                            kickPlayer(player);
                        }
                    }, 40L);
                }
                break;

            case ACCEPTED:
                // Spieler hat angenommen, Download läuft
                plugin.getLogger().info(player.getName() + " hat das Resourcepack angenommen. Download läuft...");
                break;

            default:
                break;
        }
    }

    private void kickPlayer(Player player) {
        String rawKickMsg = plugin.getConfigManager().getString("messages.kick-message",
                "&c&lResourcepack abgelehnt!\n&7Du musst das Resourcepack akzeptieren.");

        // Zeilenumbrüche verarbeiten und Farben anwenden
        String kickMessage = ColorUtils.colorize(rawKickMsg);

        // Kick auf dem Main-Thread ausführen
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                player.kickPlayer(kickMessage);
                plugin.getLogger().info(player.getName() + " wurde wegen Ablehnung des Resourcepacks gekickt.");
            }
        });
    }
}
