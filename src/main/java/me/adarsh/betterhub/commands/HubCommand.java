package me.adarsh.betterhub.commands;

import me.adarsh.betterhub.config.WSConfig;
import me.adarsh.betterhub.models.Hub;
import me.adarsh.betterhub.models.Permissions;
import me.adarsh.betterhub.services.SpawnDelayService;
import me.adarsh.betterhub.services.WorldSpawnService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("not-a-player-error"));
            return true;
        }
        Player p = (Player) sender;
        if (Permissions.hasPermission(p, Permissions.USE)) {
            Hub hub = WorldSpawnService.getHub();
            if (hub == null || !hub.worldExists()) {
                p.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("hub-not-exists"));
                return true;
            }
            SpawnDelayService.delayTeleportHub(p);
        } else {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("command-no-permission"));
        }
        return true;
    }
}

