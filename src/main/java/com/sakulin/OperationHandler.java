package com.sakulin;

import com.google.gson.Gson;

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
    public void onMessageReceived(String message, String sender) {
        try {
            ReceivedData receivedData = gson.fromJson(message, ReceivedData.class);
            switch (receivedData.operation) {
                case "broadcast": {
                    String msg = receivedData.args.get("msg");
                    assert msg != null;
                    server.broadcast(String.format("[Server] %s", msg));
                    break;
                }
                case "command": {
                    String msg = receivedData.args.get("msg");
                    assert msg != null;
                    server.runCommand(msg);
                    break;
                }
            }
        } catch (Exception e) {
            logger.warning("Meet Exception When Handling Upon Message: " + e.getMessage());
        }
    }

    public interface ServerFunctionHandler {
        void runCommand(String cmd);

        void broadcast(String msg);
    }

    static class ReceivedData {
        String operation;
        Map<String, String> args;
    }
}
