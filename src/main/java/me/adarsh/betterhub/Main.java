package me.adarsh.betterhub;;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.adarsh.betterhub.commands.*;
import me.adarsh.betterhub.config.WSConfig;
import me.adarsh.betterhub.listeners.HungerListener;
import me.adarsh.betterhub.listeners.TeleportBowListener;
import me.adarsh.betterhub.services.SpawnDelayService;
import me.adarsh.betterhub.services.WorldSpawnService;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

  private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
  private static Main gPlugin;
  private LuckPerms luckPerms;

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
    getCommand("bow").setExecutor((CommandExecutor)new GiveBowCommand());
    getServer().getPluginManager().registerEvents((Listener)new TeleportBowListener(), (Plugin)this);
    getServer().getPluginManager().registerEvents(new HungerListener(), this);
    Bukkit.getPluginManager().registerEvents((Listener) new SpawnDelayService(), (Plugin) this);
    Bukkit.getPluginManager().registerEvents((Listener) new WorldSpawnService(), (Plugin) this);
    WSConfig.reload(getPlugin());
    this.luckPerms = (LuckPerms) getServer().getServicesManager().load(LuckPerms.class);
    saveDefaultConfig();
    getServer().getPluginManager().registerEvents(this, (Plugin) this);
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


}

