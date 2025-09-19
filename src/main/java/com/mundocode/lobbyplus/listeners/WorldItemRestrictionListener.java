package com.mundocode.lobbyplus.listeners;

import com.mundocode.lobbyplus.LobbyPlus;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldItemRestrictionListener implements Listener {

    private final LobbyPlus plugin;

    public WorldItemRestrictionListener(LobbyPlus plugin) {
        this.plugin = plugin;
    }

    // ===== Utilidades =====
    private Set<String> lobbyWorlds() {
        List<String> list = plugin.getConfig().getStringList("lobby.worlds");
        if (list == null || list.isEmpty()) return Set.of("world");
        Set<String> out = new HashSet<>();
        for (String s : list) out.add(s.toLowerCase());
        return out;
    }

    private boolean isLobbyWorld(World w) {
        return w != null && lobbyWorlds().contains(w.getName().toLowerCase());
    }

    private boolean isMenuItem(ItemStack it) {
        if (it == null || !it.hasItemMeta()) return false;
        Byte tag = it.getItemMeta().getPersistentDataContainer()
                .get(plugin.getOpenerKey(), PersistentDataType.BYTE);
        return tag != null && tag == (byte)1;
    }

    private void removeMenuItems(Player p) {
        PlayerInventory inv = p.getInventory();
        // Main inventory + hotbar
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack it = inv.getItem(i);
            if (isMenuItem(it)) inv.setItem(i, null);
        }
        // Offhand
        if (isMenuItem(inv.getItemInOffHand())) inv.setItemInOffHand(null);
        // Cursor (por si estaba moviéndolo)
        if (isMenuItem(p.getItemOnCursor())) p.setItemOnCursor(null);
    }

    private void ensureMenuItem(Player p) {
        // Si ya lo tiene, no duplicar
        PlayerInventory inv = p.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            if (isMenuItem(inv.getItem(i))) return;
        }
        if (isMenuItem(inv.getItemInOffHand())) return;

        // Darlo en el slot configurado
        int slot = Math.max(0, Math.min(8, plugin.getConfig().getInt("join.itemSlot", 4)));
        inv.setItem(slot, plugin.buildJoinItem());
    }

    // ===== Eventos =====

    // Al entrar: si es mundo de lobby → asegurar; si no → quitar
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (isLobbyWorld(p.getWorld())) {
            ensureMenuItem(p);
        } else {
            removeMenuItems(p);
        }
    }

    // Al cambiar de mundo: si va a lobby → asegurar; si sale de lobby → quitar
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (isLobbyWorld(p.getWorld())) {
            ensureMenuItem(p);
        } else {
            removeMenuItems(p);
        }
    }
}
