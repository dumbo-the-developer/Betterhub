package me.adarsh.betterhub.commands;

import me.adarsh.betterhub.config.WSConfig;
import me.adarsh.betterhub.models.Permissions;
import me.adarsh.betterhub.services.WorldSpawnService;
import me.adarsh.betterhub.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnOnRespawnCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("not-a-player-error"));
            return true;
        }
        Player p = (Player) sender;
        if (Permissions.hasPermission(p, Permissions.ADMIN)) {
            Boolean respawn = null;
            if (args.length >= 1) {
                try {
                    respawn = Boolean.valueOf(Utils.parseBool(args[0]));
                } catch (IllegalArgumentException ex) {
                    p.sendMessage(WSConfig.getAdminPrefix() + "§6/" + label + " <true|false>");
                    return true;
                }
            } else {
                p.sendMessage(WSConfig.getAdminPrefix() + "§6/" + label + " <true|false> [world]");
                return true;
            }
            String pWorld = p.getLocation().getWorld().getName();
            if (args.length >= 2) {
                World w = Bukkit.getWorld(args[1]);
                if (w == null) {
                    p.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("specified-world-not-exist"));
                    return true;
                }
                pWorld = w.getName();
            }
            Location spawnloc = WorldSpawnService.getWorldSpawnLocation(pWorld);
            if (spawnloc == null) {
                p.sendMessage(WSConfig.getAdminPrefix() + WSConfig.getMessage("no-spawn-world"));
                return true;
            }
            boolean success = WorldSpawnService.setSpawnOnRespawn(pWorld, respawn);
            if (success) {
                p.sendMessage(WSConfig.getAdminPrefix() + WSConfig.getMessage("spawn-configure-success").replace("%w", spawnloc.getWorld().getName()));
            } else {
                p.sendMessage(WSConfig.getAdminPrefix() + WSConfig.getMessage("spawn-configure-fail").replace("%w", spawnloc.getWorld().getName()));
            }
        } else {
            sender.sendMessage(WSConfig.getErrorPrefix() + WSConfig.getMessage("command-no-permission"));
        }
        return true;
    }
}

