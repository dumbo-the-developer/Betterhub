package me.adarsh.betterhub.listeners;

import me.adarsh.betterhub.Main;
import me.adarsh.betterhub.utils.BowUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class TeleportBowListener implements Listener {

    private final Main plugin;
    private final BowUtils bowUtils;

    public TeleportBowListener(Main plugin) {
        this.plugin = plugin;
        this.bowUtils = new BowUtils(plugin);
    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = ((Player) event.getEntity().getShooter()).getPlayer();
            ItemStack itemInMainHand = player.getInventory().getItemInHand();
            if (itemInMainHand.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("bow-name")))) {
                Location location = event.getEntity().getLocation();
                player.teleport(location);
                event.getEntity().remove();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("teleported-message")));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        if (plugin.getConfig().getBoolean("bow-on-join")) {

            Player player = e.getPlayer();
            player.getInventory().addItem(bowUtils.createTeleportBow());
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));

        }

    }

}

