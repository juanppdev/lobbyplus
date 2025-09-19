package com.mundocode.lobbyplus.commands;

import com.mundocode.lobbyplus.LobbyPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    private final LobbyPlus plugin;
    public HelpCommand(LobbyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Aceptamos ambos namespaces de permisos por compatibilidad
        if (!sender.hasPermission("lobbyplus.help") && !sender.hasPermission("lobbycore.help")) {
            sender.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.noPermission")));
            return true;
        }
        sender.sendMessage(plugin.color("&8==== &bLobby&fPlus &8===="));
        sender.sendMessage(plugin.color("&b/lobbyhelp &7- Muestra este menú."));
        sender.sendMessage(plugin.color("&b/setspawn [lobby] &7- Establece el spawn de un lobby."));
        sender.sendMessage(plugin.color("&b/spawn [lobby] &7- Te lleva al spawn de un lobby."));
        sender.sendMessage(plugin.color("&b/lobby set <nombre> &7- Crea/actualiza un lobby."));
        sender.sendMessage(plugin.color("&b/lobby delete <nombre> &7- Elimina un lobby."));
        sender.sendMessage(plugin.color("&b/lobby list &7- Lista lobbies."));
        sender.sendMessage(plugin.color("&b/lobby reload &7- Recarga la config."));
        sender.sendMessage(plugin.color("&b/lobby openmenu &7- Abre el menú."));
        return true;
    }
}
