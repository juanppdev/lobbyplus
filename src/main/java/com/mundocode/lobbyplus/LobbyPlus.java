package com.mundocode.lobbyplus;

import com.mundocode.lobbyplus.commands.HelpCommand;
import com.mundocode.lobbyplus.commands.LobbyCommand;
import com.mundocode.lobbyplus.commands.SetSpawnCommand;
import com.mundocode.lobbyplus.commands.SpawnCommand;
import com.mundocode.lobbyplus.lobby.LobbyManager;
import com.mundocode.lobbyplus.menu.MenuManager;
import com.mundocode.lobbyplus.listeners.InteractListener;
import com.mundocode.lobbyplus.listeners.WorldItemRestrictionListener;
import com.mundocode.lobbyplus.listeners.JoinListener;
import com.mundocode.lobbyplus.listeners.MenuListener;
import com.mundocode.lobbyplus.tasks.BroadcastTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class LobbyPlus extends JavaPlugin {

    private static LobbyPlus instance;
    private LobbyManager lobbyManager;
    private MenuManager menuManager;
    private NamespacedKey openerKey;
    private BroadcastTask broadcastTask;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        openerKey = new NamespacedKey(this, "menu-opener");

        lobbyManager = new LobbyManager(this);
        lobbyManager.load();

        menuManager = new MenuManager(this, lobbyManager);
        menuManager.loadFromConfig();

        // Commands
        getCommand("lobbyhelp").setExecutor(new HelpCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("lobby").setExecutor(new LobbyCommand(this));

        // Listeners
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(this, menuManager), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(this, menuManager), this);
        Bukkit.getPluginManager().registerEvents(new WorldItemRestrictionListener(this), this);

        // Broadcasts
        setupBroadcasts();

        getLogger().info("LobbyPlus habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        if (broadcastTask != null) broadcastTask.cancel();
        lobbyManager.save();
        getLogger().info("LobbyPlus deshabilitado.");
    }

    public void reloadAll() {
        reloadConfig();
        menuManager.loadFromConfig();
        getLobbyManager().load();
        setupBroadcasts();
    }

    private void setupBroadcasts() {
        if (broadcastTask != null) broadcastTask.cancel();
        FileConfiguration cfg = getConfig();
        boolean enabled = cfg.getBoolean("broadcasts.enabled", true);
        if (enabled) {
            long interval = cfg.getLong("broadcasts.intervalTicks", 6000L);
            List<String> msgs = cfg.getStringList("broadcasts.messages");
            if (msgs == null) msgs = new ArrayList<>();
            broadcastTask = new BroadcastTask(this, msgs);
            broadcastTask.runTaskTimer(this, interval, interval);
        }
    }

    public static LobbyPlus get() { return instance; }
    public LobbyManager getLobbyManager() { return lobbyManager; }
    public MenuManager getMenuManager() { return menuManager; }
    public NamespacedKey getOpenerKey() { return openerKey; }
    public String color(String s) { return ChatColor.translateAlternateColorCodes('&', s); }

    public ItemStack buildJoinItem() {
        FileConfiguration cfg = getConfig();
        String matName = cfg.getString("join.item.material", "NETHER_STAR");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.NETHER_STAR;

        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(cfg.getString("join.item.name", "&bSelector de Lobbies")));
            List<String> lore = cfg.getStringList("join.item.lore");
            if (lore != null && !lore.isEmpty()) {
                List<String> colored = new ArrayList<>();
                for (String l : lore) colored.add(color(l));
                meta.setLore(colored);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            meta.getPersistentDataContainer().set(openerKey, PersistentDataType.BYTE, (byte)1);
            is.setItemMeta(meta);
        }
        return is;
    }

    public String prefix() { return color(getConfig().getString("messages.prefix", "&8[&bLobby&fPlus&8]&r ")); }

    public void playOpenSound(org.bukkit.entity.Player p) {
        String soundName = getConfig().getString("menu.soundOnOpen", "UI_BUTTON_CLICK");
        try {
            Sound s = Sound.valueOf(soundName);
            p.playSound(p.getLocation(), s, 1f, 1f);
        } catch (IllegalArgumentException ignored) {}
    }
}
