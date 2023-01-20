package me.adarsh.betterhub.keepinventory;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryHandler extends JavaPlugin {
    private static InventoryHandler instance = new InventoryHandler();

    private HashMap<Player, ItemStack[]> inventories = (HashMap) new HashMap<>();

    private HashMap<Player, ItemStack[]> armors = (HashMap) new HashMap<>();

    public static InventoryHandler getInstance() {
        return instance;
    }

    public void saveInventoryAndArmor(Player _player) {
        ItemStack[] tmpinv = new ItemStack[_player.getInventory().getSize()];
        ItemStack[] tmpArmor = new ItemStack[(_player.getInventory().getArmorContents()).length];
        tmpArmor = _player.getInventory().getArmorContents();
        tmpinv = _player.getInventory().getContents();
        this.inventories.put(_player, tmpinv);
        this.armors.put(_player, tmpArmor);
    }

    public ItemStack[] loadInventory(Player _player) {
        return this.inventories.get(_player);
    }

    public ItemStack[] loadArmor(Player _player) {
        return this.armors.get(_player);
    }

    public void removeInventoryAndArmor(Player _player) {
        this.inventories.remove(_player);
        this.armors.remove(_player);
    }

    public boolean hasInventorySaved(Player _player) {
        return this.inventories.containsKey(_player);
    }

    public boolean hasArmorSaved(Player _player) {
        return this.armors.containsKey(_player);
    }
}

