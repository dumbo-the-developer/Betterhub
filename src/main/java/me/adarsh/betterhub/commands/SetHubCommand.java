package me.adarsh.betterhub.commands;

import me.adarsh.betterhub.config.WSConfig;
import me.adarsh.betterhub.models.Permissions;
import me.adarsh.betterhub.services.WorldSpawnService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHubCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("not-a-player-error"));
            return true;
        }
        Player p = (Player) sender;
        if (Permissions.hasPermission(p, Permissions.ADMIN)) {
            Location loc = p.getLocation();
            WorldSpawnService.setHub(loc);
            p.sendMessage(WSConfig.getAdminPrefix() + WSConfig.getMessage("hub-set-success"));
        } else {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("command-no-permission"));
        }
        return true;
    }
}
