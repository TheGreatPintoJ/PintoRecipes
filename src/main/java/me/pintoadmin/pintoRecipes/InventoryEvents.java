package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

public record InventoryEvents(PintoRecipes plugin) implements Listener {
    public InventoryEvents(PintoRecipes plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (CreateRecipeGUI gui : plugin.getEditGUIs().values()) {
            gui.onClick(event);
            if(plugin.debugEnabled) plugin.getLogger().warning("CREATERECIPEGUI - Click: "+event.isCancelled()+"; clicktype: "+event.getClick()+"; clickedinventory: "+event.getClickedInventory()+"; inventory: "+gui.getInventory());
        }
        plugin.getRecipesGUI().onClick(event);
        if(plugin.debugEnabled) plugin.getLogger().warning("RECIPESGUI - Click: "+event.isCancelled()+"; clicktype: "+event.getClick()+"; clickedinventory: "+event.getClickedInventory());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {}

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack craftedItem = event.getCurrentItem();
        if (craftedItem == null) return;
        int crafts = getCrafted(event);

        String craftedRecipe = "";
        for (String recipeName : plugin.getConfigLoader().recipes) {
            if (craftedItem.isSimilar(plugin.getConfigLoader().getResultItem(recipeName)))
                craftedRecipe = recipeName;
        }

        if (craftedRecipe.isEmpty()) return;
        if (!player.hasPermission("pintorecipes.craft." + craftedRecipe)
                && !player.hasPermission("pintorecipes.craftbypass")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to craft this");
            event.setCancelled(true);
            return;
        }

        String limitType = plugin.getConfigLoader().getLimitType(craftedRecipe);
        int limitNum = plugin.getConfigLoader().getLimit(craftedRecipe);

        int alreadyCrafted =
                plugin.getSqLiteManager().getPlayerCrafts(craftedRecipe, player.getUniqueId());
        int serverCrafted = plugin.getSqLiteManager().getServerCrafts(craftedRecipe);

        if(limitNum > -1) {
            switch (limitType) {
                case "SERVER":
                    if (limitNum <= serverCrafted
                            && !player.hasPermission("pintorecipes.craftbypass")) {
                        event.setCancelled(true);
                        player.sendMessage(
                                ChatColor.RED
                                        + "The maximum amount of this item has already been crafted on this server");
                    }
                    break;
                case "PLAYER":
                    if (limitNum <= alreadyCrafted
                            && !player.hasPermission("pintorecipes.craftbypass")) {
                        event.setCancelled(true);
                        player.sendMessage(
                                ChatColor.RED
                                        + "You have already crafted the maximum number of this item on this server");
                    }
                    break;
                default:
                    plugin.getLogger().severe("Invalid limit-type: " + limitType);
            }
        }
        if(!event.isCancelled()){
            plugin.getSqLiteManager()
                    .incrementPlayerCrafts(craftedRecipe, player.getUniqueId(), crafts);
        }
    }

    public static int getCrafted(CraftItemEvent event) {
        if (event.getCurrentItem() == null) return 0;

        if (!event.isShiftClick()) return 1; // Normal click = exactly one craft

        if (!(event.getWhoClicked() instanceof Player player)) return 0;

        CraftingInventory inv = event.getInventory();
        ItemStack result = event.getRecipe().getResult().clone();
        int itemsPerCraft = result.getAmount();

        // ----- Ingredient limit -----
        int ingredientCrafts = Integer.MAX_VALUE;

        for (ItemStack item : inv.getMatrix()) {
            if (item == null || item.getType().isAir()) continue;
            ingredientCrafts = Math.min(ingredientCrafts, item.getAmount());
        }

        if (ingredientCrafts == Integer.MAX_VALUE) return 0;

        // ----- Inventory space limit -----
        int spaceForItems = getFitAmount(player.getInventory(), result);
        int inventoryCrafts = spaceForItems / itemsPerCraft;

        return Math.min(ingredientCrafts, inventoryCrafts);
    }

    private static int getFitAmount(PlayerInventory inv, ItemStack item) {
        int maxStack = item.getMaxStackSize();
        int space = 0;

        for (ItemStack slot : inv.getStorageContents()) {
            if (slot == null || slot.getType().isAir()) {
                space += maxStack;
            } else if (slot.isSimilar(item)) {
                space += (maxStack - slot.getAmount());
            }
        }
        return space;
    }
}
