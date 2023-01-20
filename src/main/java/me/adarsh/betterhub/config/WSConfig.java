package me.adarsh.betterhub.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import me.adarsh.betterhub.Main;
import me.adarsh.betterhub.commands.HubCommand;
import me.adarsh.betterhub.models.Hub;
import me.adarsh.betterhub.models.Spawn;
import me.adarsh.betterhub.services.WorldSpawnService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;

public class WSConfig {
    private static boolean hub_enabled = true;

    private static boolean hub_on_join = false;

    private static boolean spawn_on_join = false;

    private static boolean spawn_on_respawn = false;

    private static int spawn_delay = 0;

    private static int hub_delay = 0;

    private static String lang = "EN";

    private static HashMap<String, String> gMessages = new HashMap<>();

    public static void save() {
        Main.getPlugin().saveConfig();
    }

    public static void reload(Main plugin) {
        plugin.saveDefaultConfig();
        String[] langs = {"EN", "DE", "ES", "IT", "RU", "FR", "HU", "PL"};
        File msgFolder = new File(plugin.getDataFolder(), "Messages");
        if (!msgFolder.exists())
            msgFolder.mkdir();
        for (String l : langs) {
            if (!(new File(plugin.getDataFolder(), "Messages" + File.separator + l + "_messages.yml")).exists())
                plugin.saveResource("Messages" + File.separator + l + "_messages.yml", false);
        }
        plugin.reloadConfig();
        hub_enabled = plugin.getConfig().getBoolean("hub-enabled", true);
        spawn_on_join = plugin.getConfig().getBoolean("spawn-on-join", false);
        spawn_on_respawn = plugin.getConfig().getBoolean("spawn-on-respawn", true);
        hub_on_join = spawn_on_join ? false : plugin.getConfig().getBoolean("hub-on-join", true);
        spawn_delay = plugin.getConfig().getInt("spawn-delay", 0);
        hub_delay = plugin.getConfig().getInt("hub-delay", 0);
        lang = plugin.getConfig().getString("language", "EN");
        reloadMessages(plugin);
        String hubworldname = plugin.getConfig().getString("hub.world", "world");
        double hubx = plugin.getConfig().getDouble("hub.x", 0.0D);
        double huby = plugin.getConfig().getDouble("hub.y", 80.0D);
        double hubz = plugin.getConfig().getDouble("hub.z", 0.0D);
        float hubyaw = (float) plugin.getConfig().getDouble("hub.yaw", 90.0D);
        float hubpitch = (float) plugin.getConfig().getDouble("hub.pitch", 0.0D);
        WorldSpawnService.setHub(new Hub(hubworldname, hubx, huby, hubz, hubyaw, hubpitch));
        Bukkit.getConsoleSender().sendMessage(getMainPrefix() + "Hub loaded!");
        for (String aspawn : plugin.getConfig().getConfigurationSection("spawns").getKeys(false)) {
            String worldname = plugin.getConfig().getString("spawns." + aspawn + ".world");
            double spawnx = plugin.getConfig().getDouble("spawns." + aspawn + ".x");
            double spawny = plugin.getConfig().getDouble("spawns." + aspawn + ".y");
            double spawnz = plugin.getConfig().getDouble("spawns." + aspawn + ".z");
            float spawnyaw = (float) plugin.getConfig().getDouble("spawns." + aspawn + ".yaw");
            float spawnpitch = (float) plugin.getConfig().getDouble("spawns." + aspawn + ".pitch");
            Boolean respawn = null;
            if (plugin.getConfig().isSet("spawns." + aspawn + ".spawn-on-respawn"))
                respawn = Boolean.valueOf(plugin.getConfig().getBoolean("spawns." + aspawn + ".spawn-on-respawn"));
            Bukkit.getConsoleSender().sendMessage(getMainPrefix() + "Spawn for \"" + worldname + "\" loaded!");
            Spawn spawn = new Spawn(aspawn, worldname, spawnx, spawny, spawnz, spawnyaw, spawnpitch, respawn);
            WorldSpawnService.setSpawn(aspawn, spawn);
        }
        if (isHubEnabled()) {
            Bukkit.getPluginCommand("hub").setExecutor((CommandExecutor) new HubCommand());
        } else {
            Bukkit.getPluginCommand("hub").setExecutor(null);
        }
    }

    public static void reloadMessages(Main plugin) {
        String filename = "Messages/" + lang + "_messages.yml";
        File file = new File(Main.getPlugin().getDataFolder(), filename);
        if (!file.exists()) {
            filename = "Messages/EN_messages.yml";
            file = new File(Main.getPlugin().getDataFolder(), filename);
        }
        YamlConfiguration dMessages = null;
        YamlConfiguration yamlConfiguration1 = YamlConfiguration.loadConfiguration(file);
        try {
            InputStream stream = plugin.getResource(filename);
            Reader defConfigStream = new InputStreamReader(stream, "UTF8");
            if (defConfigStream != null)
                dMessages = YamlConfiguration.loadConfiguration(defConfigStream);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        gMessages.put("betterhub-main-prefix", yamlConfiguration1.getString("betterhub-main-prefix", dMessages.getString("betterhub-main-prefix")));
        gMessages.put("betterhub-admin-prefix", yamlConfiguration1.getString("betterhub-admin-prefix", dMessages.getString("betterhub-admin-prefix")));
        gMessages.put("betterhub-error-prefix", yamlConfiguration1.getString("betterhub-error-prefix", dMessages.getString("betterhub-error-prefix")));
        gMessages.put("command-no-permission", yamlConfiguration1.getString("command-no-permission", dMessages.getString("command-no-permission")));
        gMessages.put("not-a-player-error", yamlConfiguration1.getString("not-a-player-error", dMessages.getString("not-a-player-error")));
        gMessages.put("specified-world-not-exist", yamlConfiguration1.getString("specified-world-not-exist", dMessages.getString("specified-world-not-exist")));
        gMessages.put("no-spawn-world", yamlConfiguration1.getString("no-spawn-world", dMessages.getString("no-spawn-world")));
        gMessages.put("config-reload", yamlConfiguration1.getString("config-reload", dMessages.getString("config-reload")));
        gMessages.put("hub-spawning", yamlConfiguration1.getString("hub-spawning", dMessages.getString("hub-spawning")));
        gMessages.put("spawn-link-fail", yamlConfiguration1.getString("spawn-link-fail", dMessages.getString("spawn-link-fail")));
        gMessages.put("spawn-link-success", yamlConfiguration1.getString("spawn-link-success", dMessages.getString("spawn-link-success")));
        gMessages.put("hub-set-success", yamlConfiguration1.getString("hub-set-success", dMessages.getString("hub-set-success")));
        gMessages.put("set-spawn-success", yamlConfiguration1.getString("set-spawn-success", dMessages.getString("set-spawn-success")));
        gMessages.put("spawn-delete-success", yamlConfiguration1.getString("spawn-delete-success", dMessages.getString("spawn-delete-success")));
        gMessages.put("spawning-delay-message", yamlConfiguration1.getString("spawning-delay-message", dMessages.getString("spawning-delay-message")));
        gMessages.put("spawning-hub-delay-message", yamlConfiguration1.getString("spawning-hub-delay-message", dMessages.getString("spawning-hub-delay-message")));
        gMessages.put("spawning-message", yamlConfiguration1.getString("spawning-message", dMessages.getString("spawning-message")));
        gMessages.put("spawning-cancelled", yamlConfiguration1.getString("spawning-cancelled", dMessages.getString("spawning-cancelled")));
        gMessages.put("hub-not-exists", yamlConfiguration1.getString("hub-not-exists", dMessages.getString("hub-not-exists")));
        gMessages.put("spawn-delete-fail", yamlConfiguration1.getString("spawn-delete-fail", dMessages.getString("spawn-delete-fail")));
        gMessages.put("spawn-configure-success", yamlConfiguration1.getString("spawn-configure-success", dMessages.getString("spawn-configure-success")));
        gMessages.put("spawn-configure-fail", yamlConfiguration1.getString("spawn-configure-fail", dMessages.getString("spawn-configure-fail")));
    }

    public static String getMainPrefix() {
        return ((String) gMessages.get("betterhub-main-prefix")).replace("&", "§");
    }

    public static String getErrorPrefix() {
        return ((String) gMessages.get("betterhub-error-prefix")).replace("&", "§");
    }

    public static String getAdminPrefix() {
        return ((String) gMessages.get("betterhub-admin-prefix")).replace("&", "§");
    }

    public static String getMessage(String pKey) {
        return ((String) gMessages.get(pKey)).replace("&", "§");
    }

    public static boolean isSpawnOnJoin() {
        return spawn_on_join;
    }

    public static boolean isSpawnOnRespawn() {
        return spawn_on_respawn;
    }

    public static boolean isHubEnabled() {
        return hub_enabled;
    }

    public static boolean isHubOnJoin() {
        return hub_on_join;
    }

    public static boolean isSpawnDelayOn() {
        return (spawn_delay > 0);
    }

    public static int getSpawnDelayTime() {
        return spawn_delay;
    }

    public static boolean isHubDelayOn() {
        return (hub_delay > 0);
    }

    public static int getHubDelayTime() {
        return hub_delay;
    }
}

