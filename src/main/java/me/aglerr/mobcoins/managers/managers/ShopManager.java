package me.aglerr.mobcoins.managers.managers;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.FastInvManager;
import me.aglerr.mobcoins.MobCoins;
import me.aglerr.mobcoins.configs.Config;
import me.aglerr.mobcoins.managers.Manager;
import me.aglerr.mobcoins.shops.inventory.MainMenuInventory;
import me.aglerr.mobcoins.shops.inventory.RotatingShopInventory;
import me.aglerr.mobcoins.shops.items.ItemsLoader;
import me.aglerr.mobcoins.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ShopManager implements Manager {

    private final ItemsLoader itemsLoader = new ItemsLoader();

    private final MobCoins plugin;
    public ShopManager(MobCoins plugin){
        this.plugin = plugin;
    }

    public void loadItems(){
        this.itemsLoader.loadMainMenuItems();
        this.itemsLoader.loadAdditionalRotatingItems();
        this.itemsLoader.loadRotatingItems();
        this.itemsLoader.loadConfirmationItems();
    }

    public void openInventory(Player player, InventoryType inventoryType){

        // Check if requested inventory is MAIN_MENU
        if(inventoryType == InventoryType.MAIN_MENU){
            FileConfiguration config = Config.MAIN_MENU_CONFIG.getConfig();

            // Create a new inventory instance and open it for player
            String title = config.getString("title");
            int size = config.getInt("size");

            FastInv inventory = new MainMenuInventory(plugin, player, size, title);
            inventory.open(player);
            return;

        }

        // Check if requested inventory is ROTATING_SHOP
        if(inventoryType == InventoryType.ROTATING_SHOP){
            FileConfiguration config = Config.ROTATING_SHOP_CONFIG.getConfig();

            // Create a new inventory instance and open it for player
            String title = config.getString("title");
            int size = config.getInt("size");

            FastInv inventory = new RotatingShopInventory(plugin, player, size, title);
            inventory.open(player);
            return;

        }

        // Check if requested inventory is CATEGORY_SHOP
        if(inventoryType == InventoryType.CATEGORY_SHOP){
            FileConfiguration config = Config.CATEGORY_SHOP_CONFIG.getConfig();

            // Create a new inventory instance and open it for player
            String title = config.getString("title");
            int size = config.getInt("size");

            Bukkit.broadcastMessage("Opening category inventory...");

        }

    }

    public ItemsLoader getItemsLoader() {
        return itemsLoader;
    }

    @Override
    public void load() {
        loadItems();
        FastInvManager.register(plugin);
    }

    @Override
    public void save() {

    }

    public enum InventoryType{
        MAIN_MENU,
        ROTATING_SHOP,
        CATEGORY_SHOP
    }

}
