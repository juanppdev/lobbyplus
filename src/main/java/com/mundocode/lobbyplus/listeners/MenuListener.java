package com.mundocode.lobbyplus.listeners;

import com.mundocode.lobbyplus.LobbyPlus;
import com.mundocode.lobbyplus.menu.MenuManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuListener implements Listener {

    private final LobbyPlus plugin;
    private final MenuManager menuManager;

    public MenuListener(LobbyPlus plugin, MenuManager menuManager) {
        this.plugin = plugin;
        this.menuManager = menuManager;
    }

    // ================= Utilidades =================

    private Set<String> lobbyWorlds() {
        List<String> list = plugin.getConfig().getStringList("lobby.worlds");
        if (list == null || list.isEmpty()) return Set.of("world"); // fallback
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
        return tag != null && tag == (byte) 1;
    }

    // ================== Eventos ===================

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        // 1) Si es el inventario del menú del plugin, manejar como ya lo hacías.
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof MenuManager.PluginMenuHolder) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            int slot = e.getRawSlot();
            if (slot < 0 || slot >= e.getInventory().getSize()) return;

            boolean handled = menuManager.handleClick((Player) e.getWhoClicked(), slot);
            if (handled && menuManager.closeOnClick()) {
                e.getWhoClicked().closeInventory();
            }
            return;
        }

        // 2) En otros inventarios: evitar mover/colocar el ítem del menú si el jugador está en mundo lobby
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!isLobbyWorld(p.getWorld())) return;

        // Bloquear si el ítem seleccionado o el cursor es el del menú
        ItemStack current = e.getCurrentItem();
        ItemStack cursor = e.getCursor();
        boolean movingMenuItem = isMenuItem(current) || isMenuItem(cursor);

        // Bloquear también hotbar swap (número)
        if (e.getClick() == ClickType.NUMBER_KEY) {
            int hotbar = e.getHotbarButton();
            if (hotbar >= 0) {
                PlayerInventory inv = p.getInventory();
                ItemStack hb = inv.getItem(hotbar);
                if (isMenuItem(hb)) movingMenuItem = true;
            }
        }

        if (movingMenuItem) {
            e.setCancelled(true);
            e.setCurrentItem(current); // evita "comerse" el ítem en algunos clientes
            p.updateInventory();
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!isLobbyWorld(p.getWorld())) return;

        // Si en el cursor va el ítem del menú, bloquear el drag
        if (isMenuItem(e.getOldCursor())) {
            e.setCancelled(true);
            p.updateInventory();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!isLobbyWorld(p.getWorld())) return;
        if (isMenuItem(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
            p.updateInventory();
        }
    }
}
