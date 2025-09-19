package com.mundocode.lobbyplus.commands;

import com.mundocode.lobbyplus.LobbyPlus;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final LobbyPlus plugin;

    public SetSpawnCommand(LobbyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.onlyPlayers")));
            return true;
        }
        if (!(sender.hasPermission("lobbyplus.setspawn") || sender.hasPermission("lobbycore.setspawn"))) {
            sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.noPermission")));
            return true;
        }
        Player p = (Player) sender;
        String name = (args.length >= 1) ? args[0] : "default";
        Location loc = p.getLocation();
        plugin.getLobbyManager().setLobby(name, loc);
        String msg = plugin.getConfig().getString("messages.lobbySet", "&aSpawn de %lobby% guardado.");
        msg = msg.replace("%lobby%", name)
                 .replace("%world%", loc.getWorld().getName())
                 .replace("%x%", String.format("%.2f", loc.getX()))
                 .replace("%y%", String.format("%.2f", loc.getY()))
                 .replace("%z%", String.format("%.2f", loc.getZ()));
        p.sendMessage(plugin.prefix() + plugin.color(msg));
        return true;
    }
}
