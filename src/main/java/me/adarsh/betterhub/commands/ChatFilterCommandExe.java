package me.adarsh.betterhub.commands;

import me.adarsh.betterhub.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatFilterCommandExe implements CommandExecutor {
    public Main plugin;

    public ChatFilterCommandExe(Main instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ChatFilter")) {
            if (args.length > 0 && args[0].toLowerCase().equals("reload")) {
                Boolean doReload = false;
                if (sender instanceof Player) {
                    if (sender.hasPermission("chatfilter.reload")) {
                        doReload = true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to reload.");
                        return true;
                    }
                } else {
                    doReload = true;
                }
                if (doReload) {
                    plugin.loadConfiguration(false);
                    if (sender instanceof Player) {
                        sender.sendMessage(ChatColor.GREEN + "[ChatFilter] Reloaded.");
                    }
                    System.out.print(ChatColor.GREEN + "[ChatFilter] Reloaded.");
                    return true;
                }
            }
        }
        return false;
    }
}