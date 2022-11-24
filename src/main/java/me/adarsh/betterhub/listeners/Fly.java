package me.adarsh.betterhub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Fly implements Listener {

    @EventHandler
    public void onJoin(PlayerMoveEvent event){

        Player player = event.getPlayer();

        if (player.hasPermission("betterhub.fly")){
            player.setAllowFlight(true);
        }else {
            player.setAllowFlight(false);
        }

    }

}
