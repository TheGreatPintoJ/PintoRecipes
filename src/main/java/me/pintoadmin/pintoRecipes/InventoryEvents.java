package me.pintoadmin.pintoRecipes;

import org.bukkit.event.*;
import org.bukkit.event.inventory.*;

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
}
