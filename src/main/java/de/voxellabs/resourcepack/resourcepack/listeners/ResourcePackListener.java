package de.voxellabs.resourcepack.resourcepack.listeners;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import de.voxellabs.resourcepack.resourcepack.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
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

        if (player.hasPermission("resourcepack.bypass")) return;

        switch (status) {

            case SUCCESSFULLY_LOADED:
                plugin.getReconnectManager().onSuccess(player);
                String loadedMsg = plugin.getConfig().getString("messages.pack-loaded",
                        "&a&lResourcepack erfolgreich geladen!");
                player.sendMessage(ColorUtils.colorize(loadedMsg));
                plugin.getLogger().info(player.getName() + " hat das Resourcepack geladen.");
                break;

            case DECLINED:
                if (!plugin.getPackManager().isEnforce()) break;

                // ReconnectManager versucht erst einen Retry — kickt nur wenn keine Versuche mehr übrig
                boolean retrying = plugin.getReconnectManager().handleDecline(player);
                if (!retrying) {
                    kickPlayer(player);
                }
                break;

            case FAILED_DOWNLOAD:
                String failedMsg = plugin.getConfig().getString("messages.pack-failed",
                        "&c&lFehler beim Laden des Resourcepacks. Bitte neu verbinden!");
                player.sendMessage(ColorUtils.colorize(failedMsg));
                plugin.getLogger().warning(player.getName() + " hatte einen Download-Fehler.");

                if (plugin.getPackManager().isEnforce()) {
                    boolean retryingFailed = plugin.getReconnectManager().handleDecline(player);
                    if (!retryingFailed) {
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            if (player.isOnline()) kickPlayer(player);
                        }, 40L);
                    }
                }
                break;

            case ACCEPTED:
                plugin.getLogger().info(player.getName() + " hat das Resourcepack angenommen. Download läuft...");
                break;

            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getReconnectManager().onQuit(event.getPlayer());
    }

    private void kickPlayer(Player player) {
        String rawKickMsg = plugin.getConfig().getString("messages.kick-message",
                "&c&lResourcepack abgelehnt!\n&7Du musst das Resourcepack akzeptieren.");
        String kickMessage = ColorUtils.colorize(rawKickMsg);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                player.kickPlayer(kickMessage);
                plugin.getLogger().info(player.getName() + " wurde gekickt (Resourcepack abgelehnt).");
            }
        });
    }
}
