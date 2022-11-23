package me.adarsh.betterhub.commands;

import me.adarsh.betterhub.Main;
import me.adarsh.betterhub.config.WSConfig;
import me.adarsh.betterhub.models.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WorldSpawnCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (Permissions.hasPermission(sender, Permissions.ADMIN)) {
            WSConfig.reload(Main.getPlugin());
            sender.sendMessage(WSConfig.getAdminPrefix() + WSConfig.getMessage("config-reload"));
        } else {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("command-no-permission"));
        }
        return true;
    }
}


