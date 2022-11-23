package me.adarsh.betterhub.commands;

import me.adarsh.betterhub.Main;
import me.adarsh.betterhub.utils.BowUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveBowCommand implements CommandExecutor {

    private final Main plugin;
    private final BowUtils bowUtils;

    public GiveBowCommand(Main plugin) {
        this.plugin = plugin;
        this.bowUtils = new BowUtils(plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("betterhub.tpbow"))
                if (args.length == 0) {
                    ItemStack bow = bowUtils.createTeleportBow();
                    player.getInventory().addItem(new ItemStack[]{bow});
                    player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.ARROW, 1)});
                    player.sendMessage(ChatColor.GREEN + "You have been given yourself a teleport bow");
                } else {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "This player does not exist");
                        return true;
                    }
                    ItemStack bow = bowUtils.createTeleportBow();
                    target.getInventory().addItem(new ItemStack[]{bow});
                    target.getInventory().addItem(new ItemStack[]{new ItemStack(Material.ARROW, 1)});
                    target.sendMessage(ChatColor.GREEN + "You have been given a teleport bow");
                }
        }
        return true;
    }
}