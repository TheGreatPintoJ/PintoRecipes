package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.*;

import javax.annotation.*;
import java.util.*;

public class UncraftingGUI {
    private final PintoRecipes plugin;
    private final Inventory inventory;
    private final Player player;

    private final List<Integer> craftingSlots =
            new ArrayList<>(List.of(14, 15, 16, 23, 24, 25, 32, 33, 34));
    private final int resultSlot = 20;

    private final ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    private Recipe currentRecipe;

    public UncraftingGUI(PintoRecipes plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        inventory = Bukkit.createInventory(null, 5 * 9, color("&6&lUncrafting GUI"));

        ItemMeta unusedMeta = unused_space.getItemMeta();
        assert unusedMeta != null;
        unusedMeta.setItemName(color("&f"));
        NamespacedKey idNameKey = new NamespacedKey(PintoRecipes.thisPlugin(), "item_id");
        unusedMeta
                .getPersistentDataContainer()
                .set(idNameKey, PersistentDataType.STRING, "unused_space");
        unused_space.setItemMeta(unusedMeta);

        createGUI();
        player.openInventory(inventory);
    }

    private void createGUI(){
        for (int i = 0; i < inventory.getSize(); i++)
            if(i != resultSlot && !craftingSlots.contains(i))
                inventory.setItem(i, unused_space);
    }

    private void loadIngredients(){
        ItemStack item = inventory.getItem(resultSlot);
        if(item == null) return;
        List<Recipe> recipes = Bukkit.getRecipesFor(item);

        currentRecipe = recipes.getFirst();

        //for(Recipe recipe : recipes) {
            int resultMult = getCurrentMult();

            if (currentRecipe instanceof ShapedRecipe shapedRecipe) {
                Map<Character, ItemStack> ingredientMap = shapedRecipe.getIngredientMap();
                String[] recipeShape = shapedRecipe.getShape();

                int craftingSlot = 0;
                for (String set : recipeShape) {
                    for (char character : set.toCharArray()) {
                        int currentSlot = craftingSlots.get(craftingSlot);
                        ItemStack newItemStack = getItemStack(ingredientMap.get(character), resultMult);
                        inventory.setItem(currentSlot, newItemStack);
                        craftingSlot++;
                    }
                }
            } else if(currentRecipe instanceof ShapelessRecipe shapelessRecipe){
                List<ItemStack> ingredients = shapelessRecipe.getIngredientList();
                int i = 0;
                try {
                    for (int slot : craftingSlots) {
                        ItemStack newItemStack = getItemStack(ingredients.get(i), resultMult);
                        inventory.setItem(slot, newItemStack);
                        i++;
                    }
                } catch (IndexOutOfBoundsException ignored){
                    for (int j = i; j < craftingSlots.size(); j++)
                        inventory.setItem(craftingSlots.get(j), null);
                }
            } else {
                for (int slot : craftingSlots)
                    inventory.setItem(slot, null);
            }
        //}
    }

    @NotNull
    private ItemStack getItemStack(ItemStack itemStack, int resultMult) {
        ItemStack newItemStack;
        if (itemStack != null) {
            int newAmnt = itemStack.getAmount() * resultMult;
            if (newAmnt == 0) {
                newItemStack = new ItemStack(Material.AIR);
            } else newItemStack = new ItemStack(itemStack.getType(), newAmnt);
        } else
            newItemStack = new ItemStack(Material.AIR);
        return newItemStack;
    }

    public void onClick(InventoryClickEvent event){
        if (event.getClickedInventory() != inventory) return;
        int clickedSlot = event.getSlot();

        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(unused_space)) {
            event.setCancelled(true);
            return;
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                if (craftingSlots.contains(clickedSlot)){
                    // Reduce "result"
                    ItemStack item = inventory.getItem(resultSlot);
                    if(item == null) {
                        inventory.setItem(resultSlot, null);
                        return;
                    }
                    int amount = item.getAmount();
                    int resultAmnt = currentRecipe.getResult().getAmount();
                    int currentMult = getCurrentMult();
                    if(currentMult > 0) item.setAmount(amount % resultAmnt);
                    inventory.setItem(resultSlot, item);
                }

                if(clickedSlot == resultSlot){
                    if(inventory.getItem(clickedSlot) != null)
                        loadIngredients();
                    else {
                        // Remove crafting items
                        for (int slot : craftingSlots) {
                            inventory.setItem(slot, null);
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 1);
    }

    public void onClose(){
        plugin.getUncraftingGUIS().remove(this);
    }

    private int getCurrentMult(){
        ItemStack item = inventory.getItem(resultSlot);
        if(item == null) return 0;
        if(currentRecipe.getResult().getAmount() > item.getAmount()) return 0; // Not enough to uncraft
        return (int) (double) (item.getAmount() / currentRecipe.getResult().getAmount());
    }

    public Inventory getInventory() {
        return inventory;
    }

    private String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
