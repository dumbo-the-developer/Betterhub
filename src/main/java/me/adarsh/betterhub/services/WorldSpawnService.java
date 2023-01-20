package me.adarsh.betterhub.services;

import java.util.HashMap;

import me.adarsh.betterhub.Main;
import me.adarsh.betterhub.config.WSConfig;
import me.adarsh.betterhub.models.Hub;
import me.adarsh.betterhub.models.Spawn;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class WorldSpawnService implements Listener {
    private static HashMap<String, Spawn> gWorldSpawns = new HashMap<>();

    private static Hub gHubLocation;

    public static Location getWorldSpawnLocation(String worldName) {
        return spawnExists(worldName) ? getSpawn(worldName, true).getLocation() : null;
    }

    public static Location getWorldSpawnLocation(World world) {
        return getWorldSpawnLocation(world.getName());
    }

    public static Hub getHub() {
        return (gHubLocation != null && gHubLocation.worldExists()) ? gHubLocation : null;
    }

    public static void setHub(Location location) {
        Hub hub = getHub();
        if (hub == null) {
            hub = new Hub(location);
        } else {
            hub.setLocation(location);
        }
        hub.save();
    }

    public static void setHub(Hub hub) {
        gHubLocation = hub;
        hub.save();
    }

    public static boolean spawnExists(String pSpawnLocationName) {
        return gWorldSpawns.containsKey(pSpawnLocationName);
    }

    public static boolean setSpawnLink(String pWorldName, String pSpawnLocationName) {
        Spawn loc = getSpawn(pSpawnLocationName);
        if (loc == null)
            return false;
        setSpawn(pWorldName, loc.getLocation(), Boolean.valueOf(loc.isRespawn()));
        return true;
    }

    public static Spawn getSpawn(String pSpawnLocationName, boolean deep) {
        if (spawnExists(pSpawnLocationName)) {
            Spawn spawn = gWorldSpawns.get(pSpawnLocationName);
            if (spawn.worldExists()) {
                Spawn finalSpawn = spawn;
                if (deep && !pSpawnLocationName.equals(spawn.getLocation().getWorld().getName())) {
                    Spawn topSpawn = getSpawn(spawn.getLocation().getWorld().getName(), true);
                    if (topSpawn != null)
                        finalSpawn = topSpawn;
                }
                return finalSpawn;
            }
        }
        return null;
    }

    public static Spawn getSpawn(String pSpawnLocationName) {
        return getSpawn(pSpawnLocationName, false);
    }

    public static void setSpawn(String pSpawnLocationName, Location pLocation, Boolean pRespawn) {
        Spawn spawn = getSpawn(pSpawnLocationName);
        if (spawn == null) {
            spawn = new Spawn(pSpawnLocationName, pLocation.getWorld().getName(), pLocation.toVector(), pLocation.getYaw(), pLocation.getPitch(), pRespawn);
            gWorldSpawns.put(pSpawnLocationName, spawn);
        } else {
            spawn.setLocation(pLocation);
            spawn.setSpawnOnRespawn(pRespawn);
        }
        spawn.save();
    }

    public static void setSpawn(String name, Spawn spawn) {
        gWorldSpawns.put(name, spawn);
    }

    public static boolean deleteSpawn(String pSpawnLocationName) {
        if (!spawnExists(pSpawnLocationName))
            return false;
        gWorldSpawns.remove(pSpawnLocationName);
        Main.getPlugin().getConfig().set("spawns." + pSpawnLocationName, null);
        Main.getPlugin().saveConfig();
        return true;
    }

    public static boolean setSpawnOnRespawn(String pSpawnLocationName, Boolean respawn) {
        Spawn spawn = getSpawn(pSpawnLocationName, true);
        if (spawn != null) {
            spawn.setSpawnOnRespawn(respawn);
            spawn.save();
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        Player p = evt.getPlayer();
        if (WSConfig.isHubOnJoin()) {
            Hub hub = getHub();
            if (hub != null && hub.worldExists())
                p.teleport(hub.getLocation());
        } else if (WSConfig.isSpawnOnJoin()) {
            Spawn spawn = getSpawn(p.getWorld().getName(), true);
            if (spawn != null && spawn.worldExists())
                p.teleport(spawn.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
        Spawn respawnLocation = getSpawn(evt.getRespawnLocation().getWorld().getName(), true);
        if (respawnLocation == null)
            return;
        Boolean respawn = Boolean.valueOf(respawnLocation.isRespawn());
        if (respawnLocation != null && (respawn.booleanValue() || (!respawn.booleanValue() && !evt.isBedSpawn() && evt.getRespawnLocation().getWorld().getEnvironment() != World.Environment.NETHER)))
            evt.setRespawnLocation(respawnLocation.getLocation());
    }
}


