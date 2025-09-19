package com.mundocode.lobbyplus.commands;

import com.mundocode.lobbyplus.LobbyPlus;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final LobbyPlus plugin;

    public SpawnCommand(LobbyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.onlyPlayers")));
            return true;
        }
        if (!(sender.hasPermission("lobbyplus.spawn") || sender.hasPermission("lobbycore.spawn"))) {
            sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.noPermission")));
            return true;
        }
        Player p = (Player) sender;

        if (args.length == 0) {
            plugin.getLobbyManager().getDefault().ifPresent(loc -> {
                if (loc != null) {
                    p.teleport(loc);
                    p.sendMessage(plugin.prefix() + plugin.color("&aTeletransportado al lobby por defecto."));
                } else {
                    p.sendMessage(plugin.prefix() + plugin.color("&cEl lobby por defecto no está disponible (¿mundo sin cargar?)."));
                }
            });
            return true;
        }

        String name = args[0];
        Location loc = plugin.getLobbyManager().getLobby(name);
        if (loc == null) {
            String msg = plugin.getConfig().getString("messages.lobbyNotFound", "&cLobby no encontrado.");
            p.sendMessage(plugin.prefix() + plugin.color(msg.replace("%lobby%", name)));
            return true;
        }
        p.teleport(loc);
        String msg = plugin.getConfig().getString("messages.teleported", "&aTeletransportado a %lobby%.");
        p.sendMessage(plugin.prefix() + plugin.color(msg.replace("%lobby%", name)));
        return true;
    }
}