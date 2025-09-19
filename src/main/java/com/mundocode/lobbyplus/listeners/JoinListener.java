package com.mundocode.lobbyplus.listeners;

import com.mundocode.lobbyplus.LobbyPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;

public class JoinListener implements Listener {

    private final LobbyPlus plugin;

    public JoinListener(LobbyPlus plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        var cfg = plugin.getConfig();

        // 1) Teleport al lobby por defecto si está habilitado (se hace ANTES de dar el ítem)
        if (cfg.getBoolean("join.teleportToDefault", false)) {
            plugin.getLobbyManager().getDefault().ifPresent(loc -> {
                if (loc != null) {
                    p.teleport(loc);
                } else if (cfg.getBoolean("debug", false)) {
                    p.sendMessage(plugin.prefix() + plugin.color("&cEl mundo del lobby por defecto aún no está cargado."));
                }
            });
        }

        // 2) Determinar si el jugador está en un mundo de lobby (config: lobby.worlds)
        //    Si la lista está vacía/no existe, por defecto usamos "world".
        Set<String> lobbyWorlds = new HashSet<>();
        for (String s : cfg.getStringList("lobby.worlds")) lobbyWorlds.add(s.toLowerCase());
        if (lobbyWorlds.isEmpty()) lobbyWorlds.add("world"); // fallback

        boolean isInLobbyWorld = lobbyWorlds.contains(p.getWorld().getName().toLowerCase());

        // 3) Dar el ítem SOLO si está en mundo de lobby
        if (cfg.getBoolean("join.giveItem", true) && isInLobbyWorld) {
            int slot = Math.max(0, Math.min(8, cfg.getInt("join.itemSlot", 4)));
            PlayerInventory inv = p.getInventory();
            inv.setItem(slot, plugin.buildJoinItem());
            if (cfg.getBoolean("debug", false)) {
                p.sendMessage(plugin.prefix() + plugin.color(cfg.getString("messages.joinItemGiven", "&7Menú entregado.")));
            }
        }
    }
}
