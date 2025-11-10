package me.pintoadmin.pintoRecipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.Map;

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

            for(Map<String, String> recipeMap : recipeList){
                ShapedRecipe newRecipe = new ShapedRecipe(item);

                newRecipe.shape("123","456","789");

                newRecipe.setIngredient('1', Material.valueOf(recipeMap.get("left").toUpperCase()));
                newRecipe.setIngredient('2', Material.valueOf(recipeMap.get("left").toUpperCase()));
                newRecipe.setIngredient('3', Material.valueOf(recipeMap.get("left").toUpperCase()));
                newRecipe.setIngredient('4', Material.valueOf(recipeMap.get("middle").toUpperCase()));
                newRecipe.setIngredient('5', Material.valueOf(recipeMap.get("middle").toUpperCase()));
                newRecipe.setIngredient('6', Material.valueOf(recipeMap.get("middle").toUpperCase()));
                newRecipe.setIngredient('7', Material.valueOf(recipeMap.get("right").toUpperCase()));
                newRecipe.setIngredient('8', Material.valueOf(recipeMap.get("right").toUpperCase()));
                newRecipe.setIngredient('9', Material.valueOf(recipeMap.get("right").toUpperCase()));

                getServer().addRecipe(newRecipe);
            }
        }
    }
}
