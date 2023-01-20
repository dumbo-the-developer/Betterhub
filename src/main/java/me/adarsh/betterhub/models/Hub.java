package me.adarsh.betterhub.models;

import me.adarsh.betterhub.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class Hub {
    public String worldname;

    public Vector position;

    public float yaw;

    public float pitch;

    public Hub(Location location) {
        setLocation(location);
    }

    public Hub(String worldname, double x, double y, double z, float yaw, float pitch) {
        this.worldname = worldname;
        this.position = new Vector(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.worldname);
        if (world == null)
            return null;
        return new Location(world, this.position.getX(), this.position.getY(), this.position.getZ(), this.yaw, this.pitch);
    }

    public void setLocation(Location location) {
        this.worldname = location.getWorld().getName();
        this.position = location.toVector();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public boolean worldExists() {
        World world = Bukkit.getWorld(this.worldname);
        return (world != null);
    }

    public void save() {
        FileConfiguration conf = Main.getPlugin().getConfig();
        conf.set("hub.world", this.worldname);
        conf.set("hub.x", Double.valueOf(this.position.getX()));
        conf.set("hub.y", Double.valueOf(this.position.getY()));
        conf.set("hub.z", Double.valueOf(this.position.getZ()));
        conf.set("hub.yaw", Float.valueOf(this.yaw));
        conf.set("hub.pitch", Float.valueOf(this.pitch));
        Main.getPlugin().saveConfig();
    }
}

