package de.voxellabs.resourcepack.resourcepack;

import de.voxellabs.resourcepack.resourcepack.commands.ResourcePackCommand;
import de.voxellabs.resourcepack.resourcepack.hook.ViaVersionHook;
import de.voxellabs.resourcepack.resourcepack.listeners.PlayerJoinListener;
import de.voxellabs.resourcepack.resourcepack.listeners.ResourcePackListener;
import de.voxellabs.resourcepack.resourcepack.manager.ReconnectManager;
import de.voxellabs.resourcepack.resourcepack.manager.ResourcePackManager;
import de.voxellabs.resourcepack.resourcepack.utils.ConfigManager;
import de.voxellabs.resourcepack.resourcepack.utils.ConsoleBanner;
import de.voxellabs.resourcepack.resourcepack.utils.UpdateChecker;
import de.voxellabs.resourcepack.resourcepack.utils.Validator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class RPMG extends JavaPlugin {

    @Getter private static RPMG instance;

    private ResourcePackManager packManager;
    private ConfigManager configManager;
    private UpdateChecker updateChecker;
    private ReconnectManager reconnectManager;
    private Validator validator;
    private ViaVersionHook viaVersionHook;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.load();

        viaVersionHook = new ViaVersionHook(this);

        packManager = new ResourcePackManager(this);
        reconnectManager = new ReconnectManager(this);


        // Listener registrieren
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ResourcePackListener(this), this);

        // Command registrieren
        ResourcePackCommand cmd = new ResourcePackCommand(this);
        getCommand("resourcepack").setExecutor(cmd);
        getCommand("resourcepack").setTabCompleter(cmd);

        getLogger().info("ResourcePackEnforcer wurde erfolgreich gestartet!");
        getLogger().info("Resourcepack URL: " + packManager.getPackUrl());

        ConsoleBanner.printStartBanner(
                getLogger(),
                getDescription().getVersion(),
                packManager.getPackUrl(),
                packManager.isEnforce()
        );

        // Pack-URL validieren (asynchron) und ggf. auf Fallback wechseln
        validator = new Validator(this);
        validator.validateAsync(
                packManager.getPackUrl(),
                packManager.getFallbackUrl(),
                (reachable, activeUrl, usingFallback) -> {
                    if (reachable && usingFallback) {
                        packManager.setActiveUrl(activeUrl);
                        getLogger().warning("Fallback-URL wird aktiv verwendet: " + activeUrl);
                    }
                }
        );

        updateChecker = new UpdateChecker(this);
        if (configManager.getBoolean("resourcepack.update-check")) {
            updateChecker.checkAsync((updateAvailable, latestVersion) -> {
                if (updateAvailable && latestVersion != null) {
                    ConsoleBanner.printUpdateBanner(
                            getLogger(),
                            getDescription().getVersion(),
                            latestVersion,
                            updateChecker.getRepoUrl()
                    );
                    Bukkit.getPluginManager().disablePlugin(this);
                } else {
                    getLogger().info("Kein Update verfügbar. Du verwendest die neueste Version!");
                }
            });
        }
    }

    @Override
    public void onDisable() {
        ConsoleBanner.printStopBanner(getLogger(), getDescription().getVersion());
    }

    public void reloadPlugin() {
        configManager.reload();
        packManager.reload();
    }
}
