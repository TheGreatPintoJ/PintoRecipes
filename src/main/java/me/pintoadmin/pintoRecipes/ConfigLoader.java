package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.*;

import java.io.*;
import java.util.*;

public class ConfigLoader {
    private final PintoRecipes plugin;
    private FileConfiguration recipeConfig;
    private FileConfiguration config;
    public List<String> recipes = new ArrayList<>();

    public ConfigLoader(PintoRecipes plugin) {
        this.plugin = plugin;
        saveDefaultRecipes();
        loadConfig();
    }

    private void saveDefaultRecipes(){
        if(!new File(plugin.getDataFolder()+"/recipes.yml").exists())
            plugin.saveResource("recipes.yml", false);
    }
    public void loadConfig() {
        recipes.clear();
        recipeConfig = YamlConfiguration.loadConfiguration(
                new File(plugin.getDataFolder(), "recipes.yml")
        );
        recipes.addAll(recipeConfig.getKeys(false).stream().toList());
    }
    public void removeRecipe(String name){
        loadConfig();
        try {
            recipeConfig.set(name, null);
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            recipes.remove(name);
        } catch (IOException e){
            plugin.getLogger().severe("Failed to remove recipe: ");
            e.printStackTrace();
        }
        loadConfig();
    }

    public void saveShapedRecipe(String name, ItemStack resultingItem, Material[] materials){
        loadConfig();
        try {
            if(recipeConfig.get(name+".enabled") == null)
                recipeConfig.set(name+".enabled", true);
            if(recipeConfig.get(name+".result") == null)
                recipeConfig.set(name+".result", resultingItem);
            if(recipeConfig.get(name+".category") == null)
                recipeConfig.set(name+".category", "MISC");

            recipeConfig.set(name+".type", "shaped");
            recipeConfig.set(name+".recipe", List.of(
                    Map.of("left", materials[0].toString(), "middle", materials[1].toString(), "right", materials[2].toString()),
                    Map.of("left", materials[3].toString(), "middle", materials[4].toString(), "right", materials[5].toString()),
                    Map.of("left", materials[6].toString(), "middle", materials[7].toString(), "right", materials[8].toString())
            ));
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            plugin.getLogger().info("Saved shaped recipe: "+resultingItem.getType()+" - "+ Arrays.toString(materials));
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save recipe: ");
            e.printStackTrace();
        }
        plugin.getLoadRecipes().loadRecipes();
    }
    public void saveShapelessRecipe(String name, ItemStack resultingItem, List<String> materials){
        loadConfig();
        try {
            if(recipeConfig.get(name+".enabled") == null)
                recipeConfig.set(name+".enabled", true);
            if(recipeConfig.get(name+".result") == null)
                recipeConfig.set(name+".result", resultingItem);
            if(recipeConfig.get(name+".category") == null)
                recipeConfig.set(name+".category", "MISC");

            recipeConfig.set(name+".type", "shapeless");
            recipeConfig.set(name+".recipe", materials);
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            plugin.getLogger().info("Saved shapeless recipe: "+resultingItem.getType()+" - "+materials);
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save recipe: ");
            e.printStackTrace();
        }
        plugin.getLoadRecipes().loadRecipes();
    }
    public void saveFurnaceRecipe(String name, ItemStack resultingItem, Material material){
        loadConfig();
        try {
            if(recipeConfig.get(name+".enabled") == null)
                recipeConfig.set(name+".enabled", true);
            if(recipeConfig.get(name+".result") == null)
                recipeConfig.set(name+".result", resultingItem);
            if(recipeConfig.get(name+".category") == null)
                recipeConfig.set(name+".category", "MISC");

            recipeConfig.set(name+".type", "furnace");
            recipeConfig.set(name+".recipe", material.toString());
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            plugin.getLogger().info("Saved furnace recipe: "+resultingItem.getType()+" - "+material.name());
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save recipe: ");
            e.printStackTrace();
        }
        plugin.getLoadRecipes().loadRecipes();
    }
    public void saveBlastingRecipe(String name, ItemStack resultingItem, Material material){
        loadConfig();
        try {
            if(recipeConfig.get(name+".enabled") == null)
                recipeConfig.set(name+".enabled", true);
            if(recipeConfig.get(name+".result") == null)
                recipeConfig.set(name+".result", resultingItem);
            if(recipeConfig.get(name+".category") == null)
                recipeConfig.set(name+".category", "MISC");

            recipeConfig.set(name+".type", "blasting");
            recipeConfig.set(name+".recipe", material.toString());
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            plugin.getLogger().info("Saved blasting recipe: "+resultingItem.getType()+" - "+material.name());
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save recipe: ");
            e.printStackTrace();
        }
        plugin.getLoadRecipes().loadRecipes();
    }
    public void saveSmokingRecipe(String name, ItemStack resultingItem, Material material){
        loadConfig();
        try {
            if(recipeConfig.get(name+".enabled") == null)
                recipeConfig.set(name+".enabled", true);
            if(recipeConfig.get(name+".result") == null)
                recipeConfig.set(name+".result", resultingItem);
            if(recipeConfig.get(name+".category") == null)
                recipeConfig.set(name+".category", "MISC");

            recipeConfig.set(name+".type", "smoking");
            recipeConfig.set(name+".recipe", material.toString());
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            plugin.getLogger().info("Saved smoking recipe: "+resultingItem.getType()+" - "+material.name());
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save recipe: ");
            e.printStackTrace();
        }
        plugin.getLoadRecipes().loadRecipes();
    }
    public void saveCampfireRecipe(String name, ItemStack resultingItem, Material material){
        loadConfig();
        try {
            if(recipeConfig.get(name+".enabled") == null)
                recipeConfig.set(name+".enabled", true);
            if(recipeConfig.get(name+".result") == null)
                recipeConfig.set(name+".result", resultingItem);
            if(recipeConfig.get(name+".category") == null)
                recipeConfig.set(name+".category", "MISC");

            recipeConfig.set(name+".type", "campfire");
            recipeConfig.set(name+".recipe", material.toString());
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            plugin.getLogger().info("Saved campfire recipe: "+resultingItem.getType()+" - "+material.name());
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save recipe: ");
            e.printStackTrace();
        }
        plugin.getLoadRecipes().loadRecipes();
    }
    public void saveStonecutterRecipe(String name, ItemStack resultingItem, Material material){
        loadConfig();
        try {
            if(recipeConfig.get(name+".enabled") == null)
                recipeConfig.set(name+".enabled", true);
            if(recipeConfig.get(name+".result") == null)
                recipeConfig.set(name+".result", resultingItem);
            if(recipeConfig.get(name+".category") == null)
                recipeConfig.set(name+".category", "MISC");
            if(recipeConfig.get(name+".limit-type") == null)
                recipeConfig.set(name+".limit-type", "PLAYER");
            if(recipeConfig.get(name+".limit") == null)
                recipeConfig.set(name+".limit", -1);

            recipeConfig.set(name+".type", "stonecutter");
            recipeConfig.set(name+".recipe", material.toString());
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            plugin.getLogger().info("Saved stonecutter recipe: "+resultingItem.getType()+" - "+material.name());
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save recipe: ");
            e.printStackTrace();
        }
        plugin.getLoadRecipes().loadRecipes();
    }

    public Object getRecipe(String name){
        return recipeConfig.get(name+".recipe");
    }
    public ItemStack getResultItem(String name){
        return recipeConfig.getItemStack(name + ".result");
    }
    public boolean getEnabled(String name){
        loadConfig();
        boolean enabled = recipeConfig.getBoolean(name+".enabled", true);
        if(enabled){
            try {
                recipeConfig.set(name + ".enabled", true);
                recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
                loadConfig();
            } catch (IOException ignored) {}
        }
        return enabled;
    }
    public String getType(String name){
        loadConfig();
        String type = recipeConfig.getString(name+".type", "shaped");
        if(type.equalsIgnoreCase("shaped")){
            try {
                recipeConfig.set(name + ".type", "shaped");
                recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
                loadConfig();
            } catch (IOException ignored) {}
        }
        return type;
    }
    public CraftingBookCategory getCraftingCategory(String name){
        loadConfig();
        String string = recipeConfig.getString(name+".category", "MISC");
        if(string.equalsIgnoreCase("MISC")){
            try {
                recipeConfig.set(name + ".category", "MISC");
                recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
                loadConfig();
            } catch (IOException ignored) {}
        }
        try {
            return CraftingBookCategory.valueOf(string);
        } catch (IllegalArgumentException e){
            plugin.getLogger().severe("Invalid crafting category: "+string);
            return CraftingBookCategory.MISC;
        }
    }
    public CookingBookCategory getCookingCategory(String name){
        loadConfig();
        String string = recipeConfig.getString(name+".category", "MISC");
        if(string.equalsIgnoreCase("MISC")){
            try {
                recipeConfig.set(name + ".category", "MISC");
                recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
                loadConfig();
            } catch (IOException ignored) {}
        }
        try {
            return CookingBookCategory.valueOf(string);
        } catch (IllegalArgumentException e){
            plugin.getLogger().severe("Invalid cooking category: "+string);
            return CookingBookCategory.MISC;
        }
    }
    public int getCooktime(String name){
        loadConfig();
        int time = recipeConfig.getInt(name+".cooktime", 1);
        if(time == 1){
            try {
                recipeConfig.set(name + ".cooktime", 1);
                recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
                loadConfig();
            } catch (IOException ignored) {}
        }
        try {
            return time;
        } catch (IllegalArgumentException e){
            plugin.getLogger().severe("Invalid cooktime: "+ time);
            return 1;
        }
    }
    public int getExperience(String name){
        loadConfig();
        int exp = recipeConfig.getInt(name+".experience", 1);
        if(exp == 1){
            try {
                recipeConfig.set(name + ".experience", 1);
                recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
                loadConfig();
            } catch (IOException ignored) {}
        }
        try {
            return exp;
        } catch (IllegalArgumentException e){
            plugin.getLogger().severe("Invalid experience: "+ exp);
            return 1;
        }
    }
    public int getLimit(String name){
        loadConfig();
        int limit = recipeConfig.getInt(name+".limit", -1);
        if(limit == -1){
            try {
                recipeConfig.set(name + ".limit", -1);
                recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
                loadConfig();
            } catch (IOException ignored) {}
        }
        try {
            return limit;
        } catch (IllegalArgumentException e){
            plugin.getLogger().severe("Invalid limit: "+ limit);
            return -1;
        }
    }
    public void setLimit(String name, int newValue){
        loadConfig();
        try {
            recipeConfig.set(name+".limit", newValue);
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            loadConfig();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to set limit for recipe"+name);
        }
    }
    public String getLimitType(String name){
        loadConfig();
        String type = recipeConfig.getString(name+".limit-type", "SERVER");
        if(type.equalsIgnoreCase("SERVER")){
            try {
                recipeConfig.set(name + ".limit-type", "SERVER");
                recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
                loadConfig();
            } catch (IOException ignored) {}
        }
        return type;
    }
    public void setLimitType(String name, String newValue){
        loadConfig();
        try {
            recipeConfig.set(name+".limit-type", newValue);
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
            loadConfig();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to set limit-type for recipe"+name);
        }
    }
}
