package com.sakulin;

import com.google.gson.Gson;
import com.sakulin.models.PlayerInfo;
import com.sakulin.models.SimpleMessage;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.logging.Logger;

public class OperationHandler implements Messenger.Listener {

    ServerFunctionHandler server;
    Logger logger;
    Gson gson = new Gson();

    public OperationHandler(Logger logger, ServerFunctionHandler server) {
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void onMessageReceived(String message, String sender, Messenger messenger) {
        try {
            ReceivedData receivedData = gson.fromJson(message, ReceivedData.class);
            switch (receivedData.operation) {
                case "broadcast": {
                    @Nullable String cmdId = receivedData.args.get("id");
                    String msg = receivedData.args.get("msg");
                    assert msg != null;
                    server.broadcast(String.format("[Server] %s", msg));
                    messenger.putMsg(SimpleMessage.responseType(cmdId, 0, null));
                    break;
                }
                case "command": {
                    @Nullable String cmdId = receivedData.args.get("id");
                    String msg = receivedData.args.get("msg");
                    assert msg != null;
                    if (msg.startsWith("/")) msg = msg.substring(1);
                    boolean res = server.runCommand(msg);
                    messenger.putMsg(SimpleMessage.responseType(cmdId, res ? 0 : 1, null));
                    break;
                }
                case "players": {
                    @Nullable String cmdId = receivedData.args.get("id");
                    messenger.putMsg(SimpleMessage.responseType(cmdId, 0, server.getPlayerList()));
                    break;
                }
                case "whitelist": {
                    @Nullable String cmdId = receivedData.args.get("id");
                    messenger.putMsg(SimpleMessage.responseType(cmdId, 0, server.getWhitelist()));
                    break;
                }
                case "ops": {
                    @Nullable String cmdId = receivedData.args.get("id");
                    messenger.putMsg(SimpleMessage.responseType(cmdId, 0, server.getOps()));
                    break;
                }
                case "banlist": {
                    @Nullable String cmdId = receivedData.args.get("id");
                    messenger.putMsg(SimpleMessage.responseType(cmdId, 0, server.getBanlist()));
                    break;
                }
            }
        } catch (Exception e) {
            logger.warning("Meet Exception When Handling Upon Message: " + e.getMessage());
        }
    }

    public interface ServerFunctionHandler {
        boolean runCommand(String cmd);
        void broadcast(String msg);
        PlayerInfo[] getPlayerList();
        PlayerInfo[] getWhitelist();
        PlayerInfo[] getOps();
        PlayerInfo[] getBanlist();
    }

    static class ReceivedData {
        String operation;
        Map<String, String> args;
    }
}
