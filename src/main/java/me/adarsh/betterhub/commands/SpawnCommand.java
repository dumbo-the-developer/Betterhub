package me.adarsh.betterhub.commands;

import me.adarsh.betterhub.config.WSConfig;
import me.adarsh.betterhub.models.Permissions;
import me.adarsh.betterhub.models.Spawn;
import me.adarsh.betterhub.services.SpawnDelayService;
import me.adarsh.betterhub.services.WorldSpawnService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("not-a-player-error"));
            return true;
        }
        Player p = (Player) sender;
        if (Permissions.hasPermission(p, Permissions.USE)) {
            Location loc = p.getLocation();
            Spawn spawn = WorldSpawnService.getSpawn(loc.getWorld().getName(), true);
            if (spawn == null) {
                p.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("no-spawn-world"));
                return true;
            }
            SpawnDelayService.delayTeleport(p);
        } else {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("command-no-permission"));
        }
        return true;
    }
}


