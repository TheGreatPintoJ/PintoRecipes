package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.*;

public class InventoryEvents implements Listener {
    private final PintoRecipes plugin;
    public InventoryEvents(PintoRecipes plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        for(CreateRecipeGUI gui : plugin.getEditGUIs().values()){
            gui.onClick(event);
        }
        plugin.getRecipesGUI().onClick(event);
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){}

    @EventHandler
    public void onCraft(CraftItemEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack craftedItem = event.getCurrentItem();
        if(craftedItem == null) return;
        String craftedRecipe = "";
        for(String recipeName : plugin.getConfigLoader().recipes) {
            if(craftedItem.isSimilar(plugin.getConfigLoader().getResultItem(recipeName)))
                craftedRecipe = recipeName;
        }

        if(craftedRecipe.isEmpty()) return;
        if(!player.hasPermission("pintorecipes.craft."+craftedRecipe)) {
            player.sendMessage(ChatColor.RED+"You don't have permission to craft this");
            event.setCancelled(true);
            return;
        }

        String limitType = plugin.getConfigLoader().getLimitType(craftedRecipe);
        int limitNum = plugin.getConfigLoader().getLimit(craftedRecipe);
        if(limitNum == -1) return;
        int alreadyCrafted = plugin.getSqLiteManager().getPlayerCrafts(craftedRecipe, player.getUniqueId());
        int serverCrafted = plugin.getSqLiteManager().getServerCrafts(craftedRecipe);

        switch(limitType){
            case "SERVER":
                if(limitNum <= serverCrafted){
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED+"The maximum amount of this item has already been crafted on this server");
                } else plugin.getSqLiteManager().incrementPlayerCrafts(craftedRecipe, player.getUniqueId());
                break;
            case "PLAYER":
                if(limitNum <= alreadyCrafted){
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED+"You have already crafted the maximum number of this item on this server");
                } else plugin.getSqLiteManager().incrementPlayerCrafts(craftedRecipe, player.getUniqueId());
                break;
            default:
                plugin.getLogger().severe("Invalid limit-type: "+limitType);
        }
    }
}
