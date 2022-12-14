package me.adarsh.betterhub;

;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.adarsh.betterhub.commands.*;
import me.adarsh.betterhub.config.WSConfig;
import me.adarsh.betterhub.listeners.Fly;
import me.adarsh.betterhub.listeners.HungerListener;
import me.adarsh.betterhub.listeners.PlayerChatListener;
import me.adarsh.betterhub.listeners.TeleportBowListener;
import me.adarsh.betterhub.services.SpawnDelayService;
import me.adarsh.betterhub.services.WorldSpawnService;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static Main plugin;
    public static Logger logger;
    public static boolean debugMode;
    public static boolean showInConsole;
    public static boolean kick;
    public static boolean censor;
    public static boolean agressiveMatching;
    public static List<String> langProfanity;
    public static List<String> profanityWordMatch;
    public static String profanityMessage;
    public static List<String> langTriggers;
    public static String eleven;
    public static String censorText;
    public static String blockMessage;
    public static List<String> commands;
    private static Main gPlugin;
    public final PlayerChatListener playerListener = new PlayerChatListener(this);
    private LuckPerms luckPerms;
    private FileConfiguration LangConfig = null;
    private File LangConfigurationFile = null;

    public static Main getPlugin() {
        return gPlugin;
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, (Plugin) this);
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new LeaveEvent(), (Plugin) this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), (Plugin) this);
        getCommand("gmc").setExecutor((CommandExecutor) new gmc());
        getCommand("gms").setExecutor((CommandExecutor) new gms());
        getCommand("gma").setExecutor((CommandExecutor) new gma());
        getCommand("gmsp").setExecutor((CommandExecutor) new gmsp());
        gPlugin = this;
        Bukkit.getPluginCommand("spawn").setExecutor((CommandExecutor) new SpawnCommand());
        Bukkit.getPluginCommand("setspawn").setExecutor((CommandExecutor) new SetSpawnCommand());
        Bukkit.getPluginCommand("sethub").setExecutor((CommandExecutor) new SetHubCommand());
        Bukkit.getPluginCommand("hub").setExecutor((CommandExecutor) new HubCommand());
        Bukkit.getPluginCommand("linkspawn").setExecutor((CommandExecutor) new LinkSpawnCommand());
        Bukkit.getPluginCommand("spawn").setExecutor((CommandExecutor) new SpawnCommand());
        Bukkit.getPluginCommand("delspawn").setExecutor((CommandExecutor) new DelSpawnCommand());
        Bukkit.getPluginCommand("worldspawn").setExecutor((CommandExecutor) new WorldSpawnCommand());
        Bukkit.getPluginCommand("spawnonrespawn").setExecutor((CommandExecutor) new SpawnOnRespawnCommand());
        getCommand("bow").setExecutor((CommandExecutor) new GiveBowCommand(this));
        getServer().getPluginManager().registerEvents((Listener) new TeleportBowListener(this), (Plugin) this);
        getServer().getPluginManager().registerEvents(new HungerListener(), this);
        Bukkit.getPluginManager().registerEvents((Listener) new SpawnDelayService(), (Plugin) this);
        Bukkit.getPluginManager().registerEvents((Listener) new WorldSpawnService(), (Plugin) this);
        WSConfig.reload(getPlugin());
        this.luckPerms = (LuckPerms) getServer().getServicesManager().load(LuckPerms.class);
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, (Plugin) this);
        logger = this.getLogger();

        plugin = this;

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this.playerListener, this);
        getServer().getPluginManager().registerEvents(new Fly(), this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".equals(args[0])) {
            reloadConfig();
            sender.sendMessage(colorize("&aBH Chat formatter has been reloaded."));
            return true;
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
            return Collections.singletonList("reload");
        return new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent event) {
        final String message = event.getMessage();
        final Player player = event.getPlayer();

        // Get a LuckPerms cached metadata for the player.
        final CachedMetaData metaData = this.luckPerms.getPlayerAdapter(Player.class).getMetaData(player);
        final String group = metaData.getPrimaryGroup();

        String format = getConfig().getString(getConfig().getString("group-formats." + group) != null ? "group-formats." + group : "chat-format")
                .replace("{prefix}", metaData.getPrefix() != null ? metaData.getPrefix() : "")
                .replace("{suffix}", metaData.getSuffix() != null ? metaData.getSuffix() : "")
                .replace("{prefixes}", metaData.getPrefixes().keySet().stream().map(key -> metaData.getPrefixes().get(key)).collect(Collectors.joining()))
                .replace("{suffixes}", metaData.getSuffixes().keySet().stream().map(key -> metaData.getSuffixes().get(key)).collect(Collectors.joining()))
                .replace("{world}", player.getWorld().getName())
                .replace("{name}", player.getName())
                .replace("{displayname}", player.getDisplayName())
                .replace("{username-color}", metaData.getMetaValue("username-color") != null ? metaData.getMetaValue("username-color") : "")
                .replace("{message-color}", metaData.getMetaValue("message-color") != null ? metaData.getMetaValue("message-color") : "");

        format = colorize(translateHexColorCodes(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders(player, format) : format));

        event.setFormat(format.replace("{message}", player.hasPermission("betterhub.colorcodes") && player.hasPermission("betterhub.rgbcodes")
                ? colorize(translateHexColorCodes(message)) : player.hasPermission("betterhub.colorcodes") ? colorize(message) : player.hasPermission("betterhub.rgbcodes")
                ? translateHexColorCodes(message) : message).replace("%", "%%"));
    }

    private String colorize(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String translateHexColorCodes(final String message) {
        final char colorChar = ChatColor.COLOR_CHAR;

        final Matcher matcher = HEX_PATTERN.matcher(message);
        final StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

    public void onDisable() {
        saveDefaultConfig();
    }

    @EventHandler
    public void stopCommands(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String errorMSG = getConfig().getString("errorMSG");
        List<String> disabledCommands = getConfig().getStringList("disabledCommands");
        for (String blah : disabledCommands) {
            if (event.getMessage().equalsIgnoreCase("/" + blah)) {
                player.sendMessage(errorMSG);
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadConfiguration(Boolean firstrun) {
        plugin.getConfig().options().copyDefaults(true);

        if (firstrun) {
            String headertext;
            headertext = "Default Better Hub ChatFilter Config file\r\n\r\n";
            headertext += "debugMode: [true|false] Enable extra debug info in logs.\r\n";
            headertext += "kick: [true|false] Kick players after warning.\r\n";
            headertext += "showInConsole: [true|false] Show offending player and message in console.\r\n";
            headertext += "censor:  [true|false] Replace offending text instead of blocking message.\r\n";
            headertext += "aggressive: [true|false]  Attempts to match more words by looking for 3=e 0=o etc.\r\n";
            headertext += "\r\n";

            plugin.getConfig().options().header(headertext);
            plugin.getConfig().options().copyHeader(true);
        } else {
            plugin.reloadConfig();
        }

        Main.debugMode = plugin.getConfig().getBoolean("debugMode");
        Main.showInConsole = plugin.getConfig().getBoolean("showInConsole");
        Main.kick = plugin.getConfig().getBoolean("kick");
        Main.censor = plugin.getConfig().getBoolean("censor");
        Main.agressiveMatching = plugin.getConfig().getBoolean("aggressive");

        plugin.saveConfig();

        if (firstrun) {
            plugin.getLangConfig();
        } else {
            plugin.reloadLangConfig();
        }

        Main.langProfanity = (List<String>) plugin.getLangConfig().getList("profanity");
        Main.profanityWordMatch = (List<String>) plugin.getLangConfig().getList("profanityWordMatch");
        Main.profanityMessage = plugin.getLangConfig().getString("profanityMessage");
        Main.langTriggers = (List<String>) plugin.getLangConfig().getList("triggers");
        Main.eleven = plugin.getLangConfig().getString("triggerPhrase");
        Main.censorText = plugin.getLangConfig().getString("censorText");
        Main.blockMessage = plugin.getLangConfig().getString("blockMessage");
        Main.commands = (List<String>) plugin.getLangConfig().getList("commands");

        plugin.saveLangConfig();
    }

    public void reloadLangConfig() {
        if (LangConfigurationFile == null) {
            LangConfigurationFile = new File(getDataFolder(), "lang.yml");
        }
        LangConfig = YamlConfiguration.loadConfiguration(LangConfigurationFile);
        LangConfig.options().copyDefaults(true);

        // Look for defaults in the jar
        InputStream defConfigStream = getResource("lang.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            LangConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getLangConfig() {
        if (LangConfig == null) {
            reloadLangConfig();
        }
        return LangConfig;
    }

    public void saveLangConfig() {
        if (LangConfig == null || LangConfigurationFile == null) {
            return;
        }
        try {
            LangConfig.save(LangConfigurationFile);
        } catch (IOException ex) {
            logger.severe("Could not save Lang config to " + LangConfigurationFile + " " + ex);
        }
    }

}

