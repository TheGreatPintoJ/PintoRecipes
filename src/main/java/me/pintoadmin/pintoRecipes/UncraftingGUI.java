package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.*;

import javax.annotation.*;
import java.util.*;

public class UncraftingGUI {
    private final PintoRecipes plugin;
    private final Inventory inventory;
    private final Player player;

    private final List<Integer> craftingSlots =
            new ArrayList<>(List.of(10, 11, 12, 19, 20, 21, 28, 29, 30));
    private final int resultSlot = 24;

    private final NamespacedKey idNameKey = new NamespacedKey(PintoRecipes.thisPlugin(), "item_id");
    private final ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    public UncraftingGUI(PintoRecipes plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        inventory = Bukkit.createInventory(null, 5 * 9, color("&6&lUncrafting GUI"));

        ItemMeta unusedMeta = unused_space.getItemMeta();
        assert unusedMeta != null;
        unusedMeta.setItemName(color("&f"));
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
        List<Recipe> recipes = Bukkit.getRecipesFor(item);

        for(Recipe recipe : recipes){
            if(recipe instanceof ShapedRecipe shapedRecipe){
                Map<Character, ItemStack> ingredientMap = shapedRecipe.getIngredientMap();
                String[] recipeShape = shapedRecipe.getShape();
                plugin.getLogger().warning(Arrays.toString(recipeShape)); // TODO: REMOVE
                plugin.getLogger().warning(ingredientMap.toString()); // TODO: REMOVE

                int craftingSlot = 0;
                for (String set : recipeShape){
                    for (char character : set.toCharArray()){
                        int currentSlot = craftingSlots.get(craftingSlot);
                        ItemStack itemStack = ingredientMap.get(character);
                        inventory.setItem(currentSlot, itemStack);
                        craftingSlot++;
                    }
                }
            }
        }
    }

    public void onClick(InventoryClickEvent event){
        if (event.getClickedInventory() != inventory) return;
        int clickedSlot = event.getSlot();
        if (inventory.getItem(clickedSlot) == null) return;

        if (event.getCurrentItem().isSimilar(unused_space)) {
            event.setCancelled(true);
            return;
        }

        loadIngredients();

        if (craftingSlots.contains(clickedSlot)){
            // Remove "result"
            setItem(resultSlot, "AIR");
        }
    }

    public void onClose(){
        plugin.getUncraftingGUIS().remove(this);
    }

    private void setItem(int index, @Nullable String value) {
        if (value == null || value.equalsIgnoreCase("AIR")) {
            inventory.setItem(index, null);
        } else {
            Material material = Material.valueOf(value.toUpperCase());
            inventory.setItem(index, new ItemStack(material));
        }
    }

    private String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
