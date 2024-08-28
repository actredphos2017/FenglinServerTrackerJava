package com.sakulin;

import com.sakulin.models.SimpleMessage;

public interface Messenger {
    void putMsg(SimpleMessage message);
    void addListener(Listener listener);
    interface Listener {
        void onMessageReceived(String message, String sender);
    }
}
