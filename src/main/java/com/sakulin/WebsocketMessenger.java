package com.sakulin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sakulin.models.SimpleMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class WebsocketMessenger extends WebSocketServer implements Messenger {

    private final Set<Listener> listeners = new HashSet<>();

    private final Gson gson = new Gson();

    private final Logger logger;

    public WebsocketMessenger(InetSocketAddress address, Logger logger) {
        super(address);
        this.logger = logger;
    }

    // Messenger

    @Override
    public void putMsg(SimpleMessage message) {
        this.broadcast(gson.toJson(message));
    }

    // WebSocket

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info( webSocket.getRemoteSocketAddress() + "'s New Websocket Connection Established!");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean remote) {
        logger.info(webSocket.getRemoteSocketAddress() + "'s Websocket Connection closed!");
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        logger.info(webSocket.getRemoteSocketAddress() + "'s Message received: " + s);
        try {
            listeners.forEach((e) -> e.onMessageReceived(s, webSocket.getRemoteSocketAddress().getHostString(), this));
        } catch (JsonSyntaxException e) {
            logger.warning("Parse Upon Message Meet Json Syntax Exception!");
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.info(webSocket.getRemoteSocketAddress() + "'s Error: " + e.getMessage());
    }

    @Override
    public void onStart() {
    }
}
