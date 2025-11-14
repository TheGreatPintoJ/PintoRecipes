package me.pintoadmin.pintoRecipes;

import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

public class InventoryEvents implements Listener {
    private final PintoRecipes plugin;
    public InventoryEvents(PintoRecipes plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        plugin.getRecipeGUI().onClick(event);
    }
}
