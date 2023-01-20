package me.adarsh.betterhub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerListener implements Listener {

    @EventHandler
    public void onLoseHunger(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof org.bukkit.entity.Player)
            e.setCancelled(true);
    }

}
