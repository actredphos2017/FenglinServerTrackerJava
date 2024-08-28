package com.sakulin.models;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class PlayerInfo {
    String name;
    String uuid;

    public static PlayerInfo valueOf(Player player) {
        return new PlayerInfo(player.getName(), player.getUniqueId().toString());
    }

    public static PlayerInfo valueOf(OfflinePlayer player) {
        return new PlayerInfo(player.getName(), player.getUniqueId().toString());
    }

    public PlayerInfo(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
