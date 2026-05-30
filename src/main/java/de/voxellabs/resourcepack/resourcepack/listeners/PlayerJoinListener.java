package de.voxellabs.resourcepack.resourcepack.listeners;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final RPMG plugin;

    public PlayerJoinListener(RPMG plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Bypass-Permission prüfen
        if (event.getPlayer().hasPermission("resourcepack.bypass")) {
            return;
        }

        // Resourcepack an den Spieler senden
        plugin.getPackManager().sendResourcePack(event.getPlayer());
    }
}
