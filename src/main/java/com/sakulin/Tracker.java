package com.sakulin;

import com.sakulin.models.PlayerInfo;
import com.sakulin.models.SimpleMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.util.concurrent.Semaphore;

public final class Tracker extends JavaPlugin implements Listener {

    PluginManager pluginManager;

    WebsocketMessenger messenger;

    @Override
    public void onEnable() {
        getLogger().info("Tracker enabled");

        String hostname = "0.0.0.0";
        int port = 8887;

        pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);

        getLogger().info("WebSocket Service Start! Host: " + hostname + " Port: " + port);

        messenger = new WebsocketMessenger(new InetSocketAddress(hostname, port), getLogger());
        messenger.addListener(new OperationHandler(getLogger(), new OperationHandler.ServerFunctionHandler() {
            @Override
            public boolean runCommand(String cmd) {
                Semaphore semaphore = new Semaphore(0);
                final boolean[] result = {false};
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
                        result[0] = true;
                        semaphore.release();
                    }
                }.runTask(Tracker.this);
                semaphore.acquireUninterruptibly();
                return result[0];
            }

            @Override
            public void broadcast(String msg) {
                getServer().broadcastMessage(msg);
            }

            @Override
            public PlayerInfo[] getPlayerList() {
                return getServer().getOnlinePlayers().stream().map(PlayerInfo::valueOf).toArray(PlayerInfo[]::new);
            }

            @Override
            public PlayerInfo[] getWhitelist() {
                return getServer().getWhitelistedPlayers().stream().map(PlayerInfo::valueOf).toArray(PlayerInfo[]::new);
            }

            @Override
            public PlayerInfo[] getOps() {
                return getServer().getOperators().stream().map(PlayerInfo::valueOf).toArray(PlayerInfo[]::new);
            }

            @Override
            public PlayerInfo[] getBanlist() {
                return getServer().getBannedPlayers().stream().map(PlayerInfo::valueOf).toArray(PlayerInfo[]::new);
            }
        }));
        messenger.addListener((message, sender, messenger) -> getLogger().info(sender + "'s Websocket message received: " + message));

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
        messenger.putMsg(new SimpleMessage("PLAYER_JOINED", PlayerInfo.valueOf(event.getPlayer())));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_LEFT",  PlayerInfo.valueOf(event.getPlayer())));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_DEATH", PlayerInfo.valueOf(event.getEntity())));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_CHAT", new PlayerChatInfo(PlayerInfo.valueOf(event.getPlayer()), event.getMessage())));
    }

    @EventHandler
    public void onPlayerCmd(PlayerCommandPreprocessEvent event) {
        messenger.putMsg(new SimpleMessage("PLAYER_CMD", new PlayerChatInfo(PlayerInfo.valueOf(event.getPlayer()), event.getMessage())));
    }

    record PlayerChatInfo(PlayerInfo player, String msg) {
    }

}
