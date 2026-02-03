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
    private final ItemStack recipeSelectItem = new ItemStack(Material.BARRIER);

    private Recipe currentRecipe;
    private final List<Recipe> possibleRecipes = new ArrayList<>();
    private final int recipeSelectSlot = 8;

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
        setRecipeSelectItem();
    }

    private void loadIngredients(){
        ItemStack item = inventory.getItem(resultSlot);
        if(item == null) return;
        List<Recipe> recipes = Bukkit.getRecipesFor(item);
        possibleRecipes.clear();
        possibleRecipes.addAll(recipes);

        if(recipes.isEmpty()) return;
        if(currentRecipe == null)
            currentRecipe = recipes.getFirst();

        int resultMult = getCurrentMult();

        if (currentRecipe instanceof ShapedRecipe shapedRecipe) {
            Map<Character, ItemStack> ingredientMap = shapedRecipe.getIngredientMap();
            String[] recipeShape = shapedRecipe.getShape();

            setRecipeSelectItem();

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
                setRecipeSelectItem();
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

    private void setRecipeSelectItem(){
        Material itemType = getFirstType(currentRecipe);
        if(itemType == null) itemType = Material.BARRIER;

        recipeSelectItem.setType(itemType);
        ItemMeta meta = recipeSelectItem.getItemMeta();
        assert meta != null;
        meta.setItemName(color("&fCurrent recipe: "+itemType));
        List<String> lore = new ArrayList<>();

        possibleRecipes.forEach(recipe -> {
            try {
                lore.add(color("&f&o"+getFirstType(recipe).toString()));
            } catch (NullPointerException ignored){}
        }); // List of other recipes

        lore.addAll(List.of(
                color("&f"),
                color("&r&2Left/Right-Click to change recipe")
        ));
        meta.setLore(lore);
        recipeSelectItem.setItemMeta(meta);
        inventory.setItem(recipeSelectSlot, recipeSelectItem);
    }

    private Material getFirstType(Recipe recipe){
        if(recipe instanceof ShapedRecipe shapedRecipe){
            return shapedRecipe.getIngredientMap().get('a').getType();
        } else if(recipe instanceof ShapelessRecipe shapelessRecipe){
            return shapelessRecipe.getIngredientList().getFirst().getType();
        } else return null;
    }

    public void onClick(InventoryClickEvent event){
        if (event.getClickedInventory() != inventory) return;
        int clickedSlot = event.getSlot();
        ClickType clickType = event.getClick();

        if (event.getCurrentItem() != null) {
            if (event.getCurrentItem().isSimilar(unused_space)) {
                event.setCancelled(true);
                return;
            }

            if (event.getCurrentItem().isSimilar(recipeSelectItem)) {
                event.setCancelled(true);
                // Recipe changing logic
                if(possibleRecipes.isEmpty()) return;
                int currentRecipeIndex = possibleRecipes.contains(currentRecipe) ? possibleRecipes.indexOf(currentRecipe) : 0;

                if(clickType == ClickType.LEFT){
                    // increment recipe
                    if(currentRecipeIndex < possibleRecipes.size())
                        currentRecipeIndex++;
                } else if(clickType == ClickType.RIGHT){
                    // decrement recipe
                    if(currentRecipeIndex > 0
                            && currentRecipeIndex <= possibleRecipes.size())
                        currentRecipeIndex--;
                }
                currentRecipe = possibleRecipes.get(currentRecipeIndex);
                loadIngredients();
                return;
            }
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                if (craftingSlots.contains(clickedSlot)){
                    // Reduce "result"
                    ItemStack item = inventory.getItem(resultSlot);
                    if(item == null) {
                        inventory.setItem(resultSlot, null);
                        currentRecipe = null;
                        possibleRecipes.clear();
                        setRecipeSelectItem();
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
                        currentRecipe = null;
                        possibleRecipes.clear();
                    }
                    setRecipeSelectItem();
                }
            }
        }.runTaskLater(plugin, 1); // Delay for updated slot values
    }

    public void onClose(){
        if(inventory.getItem(resultSlot) != null){
            player.getInventory().addItem(inventory.getItem(resultSlot));
        } else {
            for(int slot : craftingSlots){
                if(inventory.getItem(slot) != null)
                    player.getInventory().addItem(inventory.getItem(slot));
            }
        }
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
