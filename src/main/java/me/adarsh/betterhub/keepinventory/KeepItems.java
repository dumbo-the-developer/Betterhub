package me.adarsh.betterhub.keepinventory;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KeepItems extends JavaPlugin implements Listener {
    private final boolean hasMethod = hasMethod("setKeepInventory");

    @EventHandler
    public void onEntityDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("betterhub.keepitems"))
            if (!this.hasMethod) {
                InventoryHandler.getInstance().saveInventoryAndArmor(player);
                event.getDrops().clear();
            } else {
                event.setKeepInventory(true);
                event.getDrops().clear();
            }
        if (player.hasPermission("betterhub.keepxp")) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
    }

    private boolean hasMethod(String string) {
        boolean hasMethod = false;
        Method[] methods = PlayerDeathEvent.class.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(string)) {
                hasMethod = true;
                break;
            }
        }
        return hasMethod;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("keepitems.keep")) {
            InventoryHandler ih = InventoryHandler.getInstance();
            if (ih.hasInventorySaved(player) && ih.hasArmorSaved(player)) {
                player.getInventory().setContents(ih.loadInventory(player));
                player.getInventory().setArmorContents(ih.loadArmor(player));
                ih.removeInventoryAndArmor(player);
            }
        }
    }
}

