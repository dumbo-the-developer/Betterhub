package me.adarsh.betterhub.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

//Create and provide teleport bows
public class BowUtils {

    public static ItemStack createTeleportBow(){

        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.GREEN + "Teleport Bow");
        List<String> lore = new ArrayList<>();
        lore.add("Shoot your arrow ");
        lore.add("and get teleported anywhere.");
        bowMeta.setLore(lore);
        bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        bow.setItemMeta(bowMeta);

        return bow;
    }

}
