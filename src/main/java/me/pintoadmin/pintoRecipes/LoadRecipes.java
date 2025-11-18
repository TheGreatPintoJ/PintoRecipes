package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.inventory.*;
import org.checkerframework.checker.units.qual.*;

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
            if(recipeName == null || recipeName.isEmpty()) continue;
            String type = configLoader.getType(recipeName);
            ItemStack item = configLoader.getResultItem(recipeName);
            if(item == null) continue;
            if(!configLoader.getEnabled(recipeName)) continue;
            switch(type.toLowerCase()){
                case "shaped":
                    List<Map<String, String>> recipeMaps = (List<Map<String, String>>) configLoader.getRecipe(recipeName);
                    ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item);

                    shapedRecipe.shape("123","456","789");
                    shapedRecipe.setCategory(configLoader.getCategory(recipeName));

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
                                shapedRecipe.setIngredient(String.valueOf(round).charAt(0), Material.valueOf(value));
                            }
                            round++;
                        }
                    }

                    if(!shapedRecipe.getIngredientMap().isEmpty()){
                        try {
                            getServer().addRecipe(shapedRecipe);
                            plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + recipeMaps);
                        } catch (IllegalStateException ignored){}
                    }
                    break;
                case "shapeless":
                    ShapelessRecipe shapelessRecipe = new ShapelessRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item);
                    List<String> materialsList = (List<String>) configLoader.getRecipe(recipeName);
                    for(String entry : materialsList){
                        shapelessRecipe.addIngredient(Material.valueOf(entry));
                    }
                    if(!shapelessRecipe.getIngredientList().isEmpty()){
                        try {
                            getServer().addRecipe(shapelessRecipe);
                            plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + materialsList);
                        } catch (IllegalStateException ignored){}
                    }
                    break;
                case "furnace":
                    Material furnaceMaterial = Material.valueOf((String) configLoader.getRecipe(recipeName.toLowerCase()));
                    if(!furnaceMaterial.isAir()){
                        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item, furnaceMaterial, 0, 100); // TODO: add configurable cooking time and exp
                        try {
                            getServer().addRecipe(furnaceRecipe);
                            plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + furnaceMaterial);
                        } catch (IllegalStateException ignored){}
                    }
                    break;
                // TODO: add more types
            }
            plugin.getCreateRecipeGUI(recipeName);
        }
    }
}
