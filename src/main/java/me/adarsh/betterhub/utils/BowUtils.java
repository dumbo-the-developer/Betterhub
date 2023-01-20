package me.adarsh.betterhub.utils;

import java.util.ArrayList;
import java.util.List;

import me.adarsh.betterhub.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BowUtils {

    private final Main plugin;

    public BowUtils(Main plugin) {
        this.plugin = plugin;
    }

    public ItemStack createTeleportBow() {
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("bow-name")));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("bow-description")));
        bowMeta.setLore(lore);
        bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        bowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bowMeta.addEnchant(Enchantment.DURABILITY, 999999999, true);
        bow.setItemMeta(bowMeta);
        return bow;
    }

}

