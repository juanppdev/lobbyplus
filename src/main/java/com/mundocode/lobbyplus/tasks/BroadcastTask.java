package com.mundocode.lobbyplus.tasks;

import com.mundocode.lobbyplus.LobbyPlus;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BroadcastTask extends BukkitRunnable {

    private final LobbyPlus plugin;
    private final List<String> messages;
    private final AtomicInteger idx = new AtomicInteger(0);

    public BroadcastTask(LobbyPlus plugin, List<String> messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public void run() {
        if (messages == null || messages.isEmpty()) return;
        int i = idx.getAndUpdate(v -> (v + 1) % messages.size());
        String msg = plugin.color(messages.get(i));
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(plugin.prefix() + msg));
    }
}
