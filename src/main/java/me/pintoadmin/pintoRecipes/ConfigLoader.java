package me.pintoadmin.pintoRecipes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class ConfigLoader {
    private final JavaPlugin plugin;
    private FileConfiguration recipeConfig;
    private FileConfiguration config;
    public List<String> recipes = new ArrayList<>();

    private final Map<String, String> defaultRecipe = Map.of(
            "left", "air",
            "middle", "air",
            "right", "air"
    );

    public ConfigLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        saveDefaultRecipes();
        loadConfig();
    }

    private void saveDefaultRecipes(){
        plugin.saveResource("recipes.yml", false);
    }
    public void loadConfig() {
        recipeConfig = YamlConfiguration.loadConfiguration(
                new File(plugin.getDataFolder(), "recipes.yml")
        );
        recipes.addAll(recipeConfig.getKeys(false).stream().toList());
    }
    public void saveEmptyRecipe(String name, ItemStack resultingItem){
        loadConfig();
        try {
            recipeConfig.set(name + ".result", resultingItem);
            recipeConfig.set(name + ".recipe", List.of(
                    new HashMap<>(defaultRecipe),
                    new HashMap<>(defaultRecipe),
                    new HashMap<>(defaultRecipe)
            ));
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save empty recipe: ");
            e.printStackTrace();
        }
    }
    public void removeRecipe(String name){
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
    public List<Map<String, String>> getRecipe(String name){
        return (List<Map<String, String>>) recipeConfig.getList(name+".recipe");
    }
    public ItemStack getResultItem(String name){
        return recipeConfig.getItemStack(name + ".result");
    }
}
