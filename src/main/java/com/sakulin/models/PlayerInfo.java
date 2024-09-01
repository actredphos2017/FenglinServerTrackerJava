package com.sakulin.models;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class PlayerInfo {

    String name;
    String uuid;
    @Nullable
    PlayerMeta meta;

    public PlayerInfo(String name, String uuid,@Nullable PlayerMeta meta) {
        this.name = name;
        this.uuid = uuid;
        this.meta = meta;
    }

    public static PlayerInfo valueOf(Player player) {

        var loc = player.getLocation();

        return new PlayerInfo(player.getName(), player.getUniqueId().toString(), new PlayerMeta(
                System.currentTimeMillis(),
                player.getHealthScale(),
                player.getHealth(),
                player.getFoodLevel(),
                player.getGameMode().ordinal(),
                player.getPing(),
                player.getLevel(),
                player.getWorld().getName(),
                new PlayerMetaLocation(loc.getX(), loc.getY(), loc.getZ())
        ));
    }

    public static PlayerInfo valueOf(OfflinePlayer player) {
        return new PlayerInfo(player.getName(), player.getUniqueId().toString(), null);
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

    public record PlayerMetaLocation(
            double x, double y, double z
    ) {
    }

    public record PlayerMeta(
            Long updateTime,
            double healthScale,
            double health,
            int foodLevel,
            int gameModeOrdinal,
            int ping,
            int level,
            String worldName,
            PlayerMetaLocation location
    ) {
    }
}
