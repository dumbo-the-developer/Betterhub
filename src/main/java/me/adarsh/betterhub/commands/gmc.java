package me.adarsh.betterhub.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gmc implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(ChatColor.GREEN + "Set your camemode to creative");
        }
        return true;
    }
}

