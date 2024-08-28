package com.sakulin;

import com.sakulin.models.SimpleMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;

public final class Tracker extends JavaPlugin implements Listener {

    PluginManager pluginManager;

    WebsocketMessenger messenger;

    @Override
    public void onEnable() {
        getLogger().info("Tracker enabled");

        String hostname = "127.0.0.1";
        int port = 8887;

        pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);

        getLogger().info("WebSocket Service Start! Host: " + hostname + " Port: " + port);

        messenger = new WebsocketMessenger(new InetSocketAddress(hostname, port), getLogger());
        messenger.addListener(message -> getLogger().info("Websocket message received: " + message));
        messenger.start();
    }

    @Override
    public void onDisable() {
        getLogger().info("Tracker disabled");
        try {
            messenger.stop();
            getLogger().info("WebSocket Service Stop!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_JOINED", new PlayerInfo(event.getPlayer().getName())));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_LEFT", new PlayerInfo(event.getPlayer().getName())));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_DEATH", new PlayerInfo(event.getEntity().getName())));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_CHAT", new PlayerChatInfo(event.getPlayer().getName(), event.getMessage())));
    }

    @EventHandler
    public void onPlayerCmd(PlayerCommandPreprocessEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_CMD", new PlayerChatInfo(event.getPlayer().getName(), event.getMessage())));
    }

    record PlayerInfo(String name) {
    }

    record PlayerChatInfo(String name, String chat) {
    }

}
