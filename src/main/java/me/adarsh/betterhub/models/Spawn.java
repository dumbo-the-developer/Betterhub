package me.adarsh.betterhub.models;

import me.adarsh.betterhub.Main;
import me.adarsh.betterhub.config.WSConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class Spawn {
    private String spawnname;

    private String worldname;

    private Vector position;

    private float yaw;

    private float pitch;

    private Boolean respawn;

    public Spawn(String spawnname, String worldname, Vector position, float yaw, float pitch, Boolean respawn) {
        this.spawnname = spawnname;
        this.worldname = worldname;
        this.position = position;
        this.respawn = respawn;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Spawn(String spawnname, String worldname, double x, double y, double z, float yaw, float pitch, Boolean respawn) {
        this.spawnname = spawnname;
        this.worldname = worldname;
        this.position = new Vector(x, y, z);
        this.respawn = respawn;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.worldname);
        return new Location(world, this.position.getX(), this.position.getY(), this.position.getZ(), this.yaw, this.pitch);
    }

    public void setLocation(Location location) {
        this.position = location.toVector();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public boolean worldExists() {
        World world = Bukkit.getWorld(this.worldname);
        return (world != null);
    }

    public boolean isRespawn() {
        return (this.respawn == null) ? WSConfig.isSpawnOnRespawn() : this.respawn.booleanValue();
    }

    public void setSpawnOnRespawn(Boolean respawn) {
        this.respawn = Boolean.valueOf((respawn == null) ? true : respawn.booleanValue());
    }

    public void save() {
        FileConfiguration conf = Main.getPlugin().getConfig();
        conf.set("spawns." + this.spawnname + ".spawn-on-respawn", this.respawn);
        conf.set("spawns." + this.spawnname + ".world", this.worldname);
        conf.set("spawns." + this.spawnname + ".x", Double.valueOf(this.position.getX()));
        conf.set("spawns." + this.spawnname + ".y", Double.valueOf(this.position.getY()));
        conf.set("spawns." + this.spawnname + ".z", Double.valueOf(this.position.getZ()));
        conf.set("spawns." + this.spawnname + ".yaw", Float.valueOf(this.yaw));
        conf.set("spawns." + this.spawnname + ".pitch", Float.valueOf(this.pitch));
        Main.getPlugin().saveConfig();
    }
}

