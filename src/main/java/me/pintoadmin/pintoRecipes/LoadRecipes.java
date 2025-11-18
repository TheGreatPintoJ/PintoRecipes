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
        configLoader = plugin.getConfigLoader();
    }
    List<String> localRecipes = new ArrayList<>();

    public void loadRecipes(){
        localRecipes.addAll(configLoader.recipes);
        for(String recipeName : localRecipes){
            ItemStack item = configLoader.getResultItem(recipeName);
            List<Map<String, String>> recipeMaps = configLoader.getRecipe(recipeName);
            if(!configLoader.getEnabled(recipeName)) continue;

            if(item == null) continue;
            ShapedRecipe newRecipe = new ShapedRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item);

            newRecipe.shape("123","456","789");

            /*int slot = 1;
            for(Map<String, String> recipeMap : recipeMaps){
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
            }*/// Old recipe loading loop

            int round = 1;
            for (Map<String, String> recipeMap : recipeMaps) {
                for(int i = 0; i < 3; i++) {
                    String value = null;
                    if (round % 3 == 1)
                        value = recipeMap.get("left");
                    if (round % 3 == 2)
                        value = recipeMap.get("middle");
                    if (round % 3 == 0)
                        value = recipeMap.get("right");

                    if(value != null && !value.equalsIgnoreCase("air")){
                        newRecipe.setIngredient(String.valueOf(round).charAt(0), Material.valueOf(value));
                    }
                    round++;
                }
            }

            if(!newRecipe.getIngredientMap().isEmpty()){
                try {
                    getServer().addRecipe(newRecipe);
                    plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + recipeMaps);
                } catch (IllegalStateException ignored){}
            }
        }
    }
}
