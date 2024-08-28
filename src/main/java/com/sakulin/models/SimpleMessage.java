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

    public static class ResponseData {
        @Nullable String id;
        int code;
        Object data;

        public ResponseData(@Nullable String id, int code, Object data) {
            this.id = id;
            this.code = code;
            this.data = data;
        }

        @Nullable
        public String getId() {
            return id;
        }

        public void setId(@Nullable String id) {
            this.id = id;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    public static SimpleMessage responseType(@Nullable String id, int code, @Nullable Object data) {
        return new SimpleMessage("RESPONSE", new ResponseData(id, code, data));
    }
}

