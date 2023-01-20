package me.adarsh.betterhub.models;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permissions {
    ADMIN("betterhub.spawn.admin"),
    BYPASS_DELAY("betterhub.bypass.delay"),
    USE("betterhub.spawn.use");

    private String permissionNode;

    Permissions(String permission) {
        this.permissionNode = permission;
    }

    public static boolean hasPermission(Player player, Permissions permission) {
        return player.hasPermission(permission.getPermissionNode());
    }

    public static boolean hasPermission(CommandSender sender, Permissions permission) {
        return sender.hasPermission(permission.getPermissionNode());
    }

    public String getPermissionNode() {
        return this.permissionNode;
    }
}

