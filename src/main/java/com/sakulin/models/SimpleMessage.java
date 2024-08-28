package com.sakulin.models;

import javax.annotation.Nullable;

public class SimpleMessage {
    String tag;
    long timestamp;
    @Nullable
    Object data;

    public SimpleMessage(String tag, @Nullable Object data) {
        this.tag = tag;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public SimpleMessage(String tag) {
        this.tag = tag;
        this.data = null;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTag() {
        return tag;
    }

    @Nullable
    public Object getData() {
        return data;
    }

    public void setData(@Nullable Object data) {
        this.data = data;
    }
}
