package com.mundocode.lobbyplus.menu;

import com.mundocode.lobbyplus.LobbyPlus;
import com.mundocode.lobbyplus.lobby.LobbyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuManager {

    public enum ActionType { WARP, COMMAND, MESSAGE }

    private final LobbyPlus plugin;
    private final LobbyManager lobbyManager;
    private String title;
    private int size;
    private boolean closeOnClick;
    private final Map<Integer, MenuAction> actions = new HashMap<>();

    public MenuManager(LobbyPlus plugin, LobbyManager lobbyManager) {
        this.plugin = plugin;
        this.lobbyManager = lobbyManager;
    }

    private static int asInt(Object o, int def) {
        if (o instanceof Number) return ((Number)o).intValue();
        if (o instanceof String) try { return Integer.parseInt((String)o); } catch (NumberFormatException ignored) {}
        return def;
    }
    private static String asString(Object o, String def) {
        return (o instanceof String) ? (String)o : (o != null ? o.toString() : def);
    }
    @SuppressWarnings("unchecked")
    private static List<String> asStringList(Object o) {
        return (o instanceof List) ? (List<String>) o : Collections.emptyList();
    }

    public void loadFromConfig() {
        actions.clear();
        title = plugin.color(plugin.getConfig().getString("menu.title", "&8Menu"));
        size = plugin.getConfig().getInt("menu.size", 27);
        closeOnClick = plugin.getConfig().getBoolean("menu.closeOnClick", true);

        List<Map<?, ?>> items = plugin.getConfig().getMapList("menu.items");
        for (Map<?, ?> raw : items) {
            try {
                int slot = asInt(raw.get("slot"), 0);
                String material = asString(raw.get("material"), "STONE");
                String name = asString(raw.get("name"), "&fItem");
                List<String> lore = asStringList(raw.get("lore"));
                String actionStr = asString(raw.get("action"), "MESSAGE");
                String value = asString(raw.get("value"), "");

                MenuAction.ActionType type = MenuAction.ActionType.valueOf(actionStr.toUpperCase(Locale.ROOT));
                MenuAction act = new MenuAction(type, value);
                actions.put(slot, act);
            } catch (Exception ignored) { }
        }
    }

    public void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(new PluginMenuHolder(), size, title);
        List<Map<?, ?>> items = plugin.getConfig().getMapList("menu.items");
        for (Map<?, ?> raw : items) {
            int slot = asInt(raw.get("slot"), 0);
            String material = asString(raw.get("material"), "STONE");
            String name = asString(raw.get("name"), "&fItem");
            List<String> lore = asStringList(raw.get("lore"));

            Material mat = Material.matchMaterial(material);
            if (mat == null) mat = Material.STONE;
            ItemStack is = new ItemStack(mat);
            ItemMeta meta = is.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.color(name));
                if (!lore.isEmpty()) {
                    List<String> colored = new ArrayList<>();
                    for (String l : lore) colored.add(plugin.color(l));
                    meta.setLore(colored);
                }
                is.setItemMeta(meta);
            }
            if (slot >= 0 && slot < inv.getSize()) inv.setItem(slot, is);
        }
        plugin.playOpenSound(p);
        p.openInventory(inv);
    }

    public boolean handleClick(Player p, int slot) {
        MenuAction action = actions.get(slot);
        if (action == null) return false;

        switch (action.type) {
            case WARP: {
                String lobby = action.value.isEmpty() ? "default" : action.value;
                boolean defined = lobbyManager.exists(lobby);
                var loc = lobbyManager.getLobby(lobby);
                if (loc == null) {
                    if (defined) {
                        p.sendMessage(plugin.prefix() + plugin.color("&cEl mundo del lobby '&e" + lobby + "&c' aún no está cargado."));
                    } else {
                        p.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.lobbyNotFound", "&cLobby no encontrado.").replace("%lobby%", lobby)));
                    }
                    return true;
                }
                p.teleport(loc);
                p.sendMessage(plugin.prefix() + plugin.color(plugin.getConfig().getString("messages.teleported", "&aTeletransportado a %lobby%.").replace("%lobby%", lobby)));
                break;
            }
            case COMMAND: {
                if (!action.value.isEmpty()) {
                    p.closeInventory();
                    p.performCommand(action.value);
                }
                break;
            }
            case MESSAGE: {
                if (!action.value.isEmpty()) {
                    p.sendMessage(plugin.prefix() + plugin.color(action.value));
                }
                break;
            }
        }
        return true;
    }

    public boolean closeOnClick() { return closeOnClick; }

    public static class PluginMenuHolder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }

    public static class MenuAction {
        public enum ActionType { WARP, COMMAND, MESSAGE }
        public final ActionType type;
        public final String value;
        public MenuAction(ActionType type, String value) { this.type = type; this.value = value; }
    }
}
