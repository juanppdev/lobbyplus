package com.mundocode.lobbyplus.commands;

import com.mundocode.lobbyplus.LobbyPlus;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.StringJoiner;

public class LobbyCommand implements CommandExecutor {

    private final LobbyPlus plugin;

    public LobbyCommand(LobbyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("lobbyplus.admin") || sender.hasPermission("lobbycore.admin"))) {
            sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.noPermission")));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(plugin.prefix() + plugin.color("&7Uso: /lobby <set|delete|list|reload|openmenu>"));
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "set": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.onlyPlayers")));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(plugin.prefix() + plugin.color("&cUso: /lobby set <nombre>"));
                    return true;
                }
                String name = args[1];
                Location loc = ((Player) sender).getLocation();
                plugin.getLobbyManager().setLobby(name, loc);
                String msg = plugin.getConfig().getString("messages.lobbySet", "&aSpawn de %lobby% guardado.");
                msg = msg.replace("%lobby%", name)
                        .replace("%world%", loc.getWorld().getName())
                        .replace("%x%", String.format("%.2f", loc.getX()))
                        .replace("%y%", String.format("%.2f", loc.getY()))
                        .replace("%z%", String.format("%.2f", loc.getZ()));
                sender.sendMessage(plugin.prefix() + plugin.color(msg));
                return true;
            }
            case "delete": {
                if (args.length < 2) {
                    sender.sendMessage(plugin.prefix() + plugin.color("&cUso: /lobby delete <nombre>"));
                    return true;
                }
                String name = args[1];
                boolean ok = plugin.getLobbyManager().deleteLobby(name);
                if (ok) {
                    sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.lobbyDeleted", "&aLobby eliminado.").replace("%lobby%", name)));
                } else {
                    sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.lobbyNotFound", "&cLobby no encontrado.").replace("%lobby%", name)));
                }
                return true;
            }
            case "list": {
                List<String> list = plugin.getLobbyManager().list();
                StringJoiner sj = new StringJoiner(", ");
                for (String s : list) sj.add(s);
                sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.listHeader", "&7Lobbies: &f%list%").replace("%list%", sj.toString())));
                return true;
            }
            case "reload": {
                plugin.reloadAll();
                sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.reloaded", "&aConfig recargada.")));
                return true;
            }
            case "openmenu": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.onlyPlayers")));
                    return true;
                }
                plugin.getMenuManager().openMenu((Player) sender);
                return true;
            }
            default:
                sender.sendMessage(plugin.prefix() + plugin.color("&cUso: /lobby <set|delete|list|reload|openmenu>"));
                return true;
        }
    }
}
