package me.adarsh.betterhub.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class TeleportBowListener implements Listener {

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event) {


        if (event.getEntity().getShooter() instanceof Player) {

            Player player = ((Player) event.getEntity().getShooter()).getPlayer();

            ItemStack itemInMainHand = player.getInventory().getItemInHand();

            if (itemInMainHand.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Teleport Bow")){

                Location location = event.getEntity().getLocation();

                player.teleport(location);
                event.getEntity().remove();
                player.sendMessage(ChatColor.GREEN + "You have been teteported by teleport bow");

            }

        }

    }

}
