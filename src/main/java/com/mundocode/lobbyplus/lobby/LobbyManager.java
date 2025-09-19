package com.mundocode.lobbyplus.lobby;

import com.mundocode.lobbyplus.LobbyPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LobbyManager {

    private final LobbyPlus plugin;
    private final Map<String, StoredLoc> lobbies = new HashMap<>();
    private final File file;
    private FileConfiguration data;

    public static class StoredLoc {
        public final String world;
        public final double x, y, z;
        public final float yaw, pitch;
        public StoredLoc(String world, double x, double y, double z, float yaw, float pitch) {
            this.world = world; this.x = x; this.y = y; this.z = z; this.yaw = yaw; this.pitch = pitch;
        }
        public Location toLocation() {
            World w = Bukkit.getWorld(world);
            if (w == null) return null;
            return new Location(w, x, y, z, yaw, pitch);
        }
    }

    public LobbyManager(LobbyPlus plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "lobbies.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public void load() {
        lobbies.clear();
        if (!file.exists()) {
            save();
            return;
        }
        data = YamlConfiguration.loadConfiguration(file);
        for (String key : data.getKeys(false)) {
            String path = key + ".";
            String worldName = data.getString(path + "world");
            double x = data.getDouble(path + "x");
            double y = data.getDouble(path + "y");
            double z = data.getDouble(path + "z");
            float yaw = (float) data.getDouble(path + "yaw");
            float pitch = (float) data.getDouble(path + "pitch");
            if (worldName != null && !worldName.isEmpty()) {
                lobbies.put(key.toLowerCase(Locale.ROOT), new StoredLoc(worldName, x, y, z, yaw, pitch));
            }
        }
    }

    public void save() {
        data = new YamlConfiguration();
        for (Map.Entry<String, StoredLoc> e : lobbies.entrySet()) {
            String key = e.getKey();
            StoredLoc loc = e.getValue();
            String path = key + ".";
            data.set(path + "world", loc.world);
            data.set(path + "x", loc.x);
            data.set(path + "y", loc.y);
            data.set(path + "z", loc.z);
            data.set(path + "yaw", loc.yaw);
            data.set(path + "pitch", loc.pitch);
        }
        try { data.save(file); } catch (IOException ex) { ex.printStackTrace(); }
    }

    public boolean setLobby(String name, Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        lobbies.put(name.toLowerCase(Locale.ROOT),
                new StoredLoc(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()));
        save();
        return true;
    }

    public Location getLobby(String name) {
        StoredLoc sl = lobbies.get(name.toLowerCase(Locale.ROOT));
        return (sl == null) ? null : sl.toLocation();
    }

    public boolean deleteLobby(String name) {
        if (lobbies.remove(name.toLowerCase(Locale.ROOT)) != null) {
            save(); return true;
        }
        return false;
    }

    public boolean exists(String name) { return lobbies.containsKey(name.toLowerCase(Locale.ROOT)); }

    public List<String> list() {
        return lobbies.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Optional<Location> getDefault() {
        String def = plugin.getConfig().getString("default-lobby", "mainlobby");
        return Optional.ofNullable(getLobby(def));
    }

}
