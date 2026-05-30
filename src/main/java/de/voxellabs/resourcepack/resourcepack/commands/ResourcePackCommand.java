package de.voxellabs.resourcepack.resourcepack.commands;

import de.voxellabs.resourcepack.resourcepack.RPMG;
import de.voxellabs.resourcepack.resourcepack.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcePackCommand implements CommandExecutor, TabCompleter {

    private final RPMG plugin;

    public ResourcePackCommand(RPMG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = ColorUtils.colorize(plugin.getConfig().getString("messages.prefix", "&8[&6ResourcePack&8] &r"));

        if (!sender.hasPermission("resourcepack.admin")) {
            sender.sendMessage(prefix + ColorUtils.colorize("&cDu hast keine Berechtigung für diesen Befehl!"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender, prefix, label);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload":
                plugin.reloadPlugin();
                String reloadMsg = plugin.getConfig().getString("messages.reload-success", "&aKonfiguration erfolgreich neu geladen!");
                sender.sendMessage(prefix + ColorUtils.colorize(reloadMsg));
                break;

            case "send":
                if (args.length < 2) {
                    // Pack an sich selbst senden
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(prefix + ColorUtils.colorize("&cBitte gib einen Spielernamen an!"));
                        return true;
                    }
                    Player self = (Player) sender;
                    plugin.getPackManager().sendResourcePack(self);
                    sender.sendMessage(prefix + ColorUtils.colorize("&aDas Resourcepack wurde an dich gesendet!"));
                } else {
                    // Pack an bestimmten Spieler senden
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(prefix + ColorUtils.colorize("&cSpieler &e" + args[1] + " &cnicht gefunden!"));
                        return true;
                    }
                    plugin.getPackManager().sendResourcePack(target);
                    String sentMsg = plugin.getConfig().getString("messages.pack-sent", "&aDas Resourcepack wurde an %player% gesendet!")
                            .replace("%player%", target.getName());
                    sender.sendMessage(prefix + ColorUtils.colorize(sentMsg));
                }
                break;

            case "sendall":
                int count = 0;
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!online.hasPermission("resourcepack.bypass")) {
                        plugin.getPackManager().sendResourcePack(online);
                        count++;
                    }
                }
                sender.sendMessage(prefix + ColorUtils.colorize("&aDas Resourcepack wurde an &e" + count + " &aSpieler gesendet!"));
                break;

            case "info":
                sender.sendMessage(ColorUtils.colorize("&8&m----&8[ &6ResourcePackEnforcer &8]&m----"));
                sender.sendMessage(ColorUtils.colorize("&7URL: &f" + plugin.getPackManager().getPackUrl()));
                sender.sendMessage(ColorUtils.colorize("&7Erzwungen: " + (plugin.getPackManager().isEnforce() ? "&aJa" : "&cNein")));
                sender.sendMessage(ColorUtils.colorize("&7Send-Delay: &f" + plugin.getPackManager().getSendDelay() + " Ticks"));
                sender.sendMessage(ColorUtils.colorize("&7Online Spieler: &f" + Bukkit.getOnlinePlayers().size()));
                sender.sendMessage(ColorUtils.colorize("&8&m--------------------------"));
                break;

            default:
                sendHelp(sender, prefix, label);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender, String prefix, String label) {
        sender.sendMessage(ColorUtils.colorize("&8&m----&8[ &6ResourcePack Hilfe &8]&m----"));
        sender.sendMessage(ColorUtils.colorize("&e/" + label + " reload &7- Config neu laden"));
        sender.sendMessage(ColorUtils.colorize("&e/" + label + " send [Spieler] &7- Pack senden"));
        sender.sendMessage(ColorUtils.colorize("&e/" + label + " sendall &7- Pack an alle senden"));
        sender.sendMessage(ColorUtils.colorize("&e/" + label + " info &7- Plugin Informationen"));
        sender.sendMessage(ColorUtils.colorize("&8&m-----------------------------"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!sender.hasPermission("resourcepack.admin")) return new ArrayList<>();

        if (args.length == 1) {
            return Stream.of("reload", "send", "sendall", "info")
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
