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
                    shapedRecipe.setCategory(configLoader.getCraftingCategory(recipeName));

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
                                shapedRecipe.setIngredient(String.valueOf(round).charAt(0), Material.valueOf(value.toUpperCase()));
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
                        shapelessRecipe.addIngredient(Material.valueOf(entry.toUpperCase()));
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
                    int furnaceCookTime = configLoader.getCooktime(recipeName.toLowerCase());
                    int furnaceExperience = configLoader.getExperience(recipeName.toLowerCase());
                    if(!furnaceMaterial.isAir()){
                        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item, furnaceMaterial, furnaceExperience, furnaceCookTime);
                        try {
                            furnaceRecipe.setCategory(configLoader.getCookingCategory(recipeName));
                            getServer().addRecipe(furnaceRecipe);
                            plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + furnaceMaterial);
                        } catch (IllegalStateException ignored){}
                    }
                    break;
                case "blasting":
                    Material blastingMaterial = Material.valueOf((String) configLoader.getRecipe(recipeName.toLowerCase()));
                    int blastingCookTime = configLoader.getCooktime(recipeName.toLowerCase());
                    int blastingExperience = configLoader.getExperience(recipeName.toLowerCase());
                    if(!blastingMaterial.isAir()){
                        BlastingRecipe blastingRecipe = new BlastingRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item, blastingMaterial, blastingExperience, blastingCookTime);
                        try {
                            blastingRecipe.setCategory(configLoader.getCookingCategory(recipeName));
                            getServer().addRecipe(blastingRecipe);
                            plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + blastingMaterial);
                        } catch (IllegalStateException ignored){}
                    }
                    break;
                case "smoking":
                    Material smokingMaterial = Material.valueOf((String) configLoader.getRecipe(recipeName.toLowerCase()));
                    int smokingCookTime = configLoader.getCooktime(recipeName.toLowerCase());
                    int smokingExperience = configLoader.getExperience(recipeName.toLowerCase());
                    if(!smokingMaterial.isAir()){
                        SmokingRecipe smokingRecipe = new SmokingRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item, smokingMaterial, smokingExperience, smokingCookTime);
                        try {
                            smokingRecipe.setCategory(configLoader.getCookingCategory(recipeName));
                            getServer().addRecipe(smokingRecipe);
                            plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + smokingMaterial);
                        } catch (IllegalStateException ignored){}
                    }
                    break;
                case "campfire":
                    Material campfireMaterial = Material.valueOf((String) configLoader.getRecipe(recipeName.toLowerCase()));
                    int campfireCookTime = configLoader.getCooktime(recipeName.toLowerCase());
                    int campfireExperience = configLoader.getExperience(recipeName.toLowerCase());
                    if(!campfireMaterial.isAir()){
                        CampfireRecipe campfireRecipe = new CampfireRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item, campfireMaterial, campfireExperience, campfireCookTime);
                        try {
                            campfireRecipe.setCategory(configLoader.getCookingCategory(recipeName));
                            getServer().addRecipe(campfireRecipe);
                            plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + campfireMaterial);
                        } catch (IllegalStateException ignored){}
                    }
                    break;
                case "stonecutter":
                    Material stonecutterMaterial = Material.valueOf((String) configLoader.getRecipe(recipeName.toLowerCase()));
                    if(!stonecutterMaterial.isAir()){
                        StonecuttingRecipe stonecutterRecipe = new StonecuttingRecipe(new NamespacedKey(plugin, recipeName.toLowerCase()), item, stonecutterMaterial);
                        try {
                            getServer().addRecipe(stonecutterRecipe);
                            plugin.getLogger().info("Loaded Recipe: " + item.getType() + " - " + stonecutterMaterial);
                        } catch (IllegalStateException ignored){}
                    }
                    break;
            }
            plugin.getCreateRecipeGUI(recipeName);
        }
        plugin.getSqLiteManager().addColumns();
    }
}
