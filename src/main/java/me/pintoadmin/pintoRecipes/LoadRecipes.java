package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.inventory.*;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class LoadRecipes {
    private final PintoRecipes plugin;
    private final ConfigLoader configLoader;
    public LoadRecipes(PintoRecipes plugin){
        this.plugin = plugin;
        configLoader = plugin.configLoader;
    }

    public void loadRecipes(){
        for(String recipeName : configLoader.recipes){
            ItemStack item = configLoader.getResultItem(recipeName);
            List<Map<String, String>> recipeList = configLoader.getRecipe(recipeName);

            if(item == null) continue;
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item);

            newRecipe.shape("123","456","789");

            int slot = 1;
            for(Map<String, String> recipeMap : recipeList){
                if(slot >9) break;
                for(Map.Entry<String, String> mapEntry : recipeMap.entrySet()) {
                    String key = mapEntry.getKey();
                    String value = mapEntry.getValue().toUpperCase();
                    Material material = Material.valueOf(value);
                    if(value.equalsIgnoreCase("air")){
                        slot++;
                        continue;
                    }
                    newRecipe.setIngredient(String.valueOf(slot).charAt(0), material);

                    slot++;
                }
            }
            if(!newRecipe.getIngredientMap().isEmpty()){
                try {
                    getServer().addRecipe(newRecipe);
                    plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + recipeList);
                } catch (IllegalStateException e){
                    plugin.getLogger().severe("Duplicate result items found. Check out %s in the recipes.yml file".formatted(newRecipe));
                }
            }
        }
    }
}
