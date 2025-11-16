package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.scheduler.*;

import java.util.*;

public class RecipesGUI {
    private final PintoRecipes plugin;
    private Inventory inventory;
    private final ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private List<String> recipes = new ArrayList<>();
    private final int size = 6*9;

    public RecipesGUI(PintoRecipes plugin) {
        this.plugin = plugin;
        recipes = plugin.getConfigLoader().recipes;
        constructGUI();
    }

    private void constructGUI(){
        inventory = Bukkit.createInventory(null, size, color("&8&l&oCustom Recipes"));

        ItemMeta meta = unused_space.getItemMeta();
        meta.setItemName(color("&f"));
        unused_space.setItemMeta(meta);

        for(int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, unused_space);

        for(int i = 0; i < size-18; i++){
            try {
                ItemStack item = plugin.getConfigLoader().getResultItem(recipes.get(i));
                inventory.setItem(i, item);
            } catch (IndexOutOfBoundsException ignored){
                inventory.setItem(i, null);
            }
        }

        for(int i = size-9; i < size; i++)
            inventory.setItem(i, null);
    }

    public void sendToPlayer(Player player){
        InventoryView invView = player.openInventory(inventory);
    }
    public void onClick(InventoryClickEvent event){
        if(event.getClickedInventory() != inventory) return;
        if(event.getCurrentItem() != null) {
            event.setCancelled(true);
        }
    }
    public void deinit(){}

    private String color(String input){
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
