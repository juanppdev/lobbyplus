package com.mundocode.lobbyplus.listeners;

import com.mundocode.lobbyplus.LobbyPlus;
import com.mundocode.lobbyplus.menu.MenuManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class InteractListener implements Listener {

    private final LobbyPlus plugin;
    private final MenuManager menuManager;

    public InteractListener(LobbyPlus plugin, MenuManager menuManager) {
        this.plugin = plugin;
        this.menuManager = menuManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack inHand = e.getPlayer().getInventory().getItemInMainHand();
        if (inHand == null || inHand.getType() == Material.AIR) return;
        ItemMeta meta = inHand.getItemMeta();
        if (meta == null) return;

        Byte tag = meta.getPersistentDataContainer().get(plugin.getOpenerKey(), PersistentDataType.BYTE);
        if (tag != null && tag == (byte)1) {
            e.setCancelled(true);
            menuManager.openMenu(e.getPlayer());
        }
    }
}
