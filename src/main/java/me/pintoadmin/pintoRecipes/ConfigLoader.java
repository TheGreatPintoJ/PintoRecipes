package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.*;
import java.io.*;
import java.util.*;

public class ConfigLoader {
    private final PintoRecipes plugin;
    private FileConfiguration recipeConfig;
    private FileConfiguration config;
    public List<String> recipes = new ArrayList<>();

    private final Map<String, String> defaultRecipe = Map.of(
            "left", "air",
            "middle", "air",
            "right", "air"
    );

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
    public void saveRecipe(String name, ItemStack resultingItem, Material[] materials){
        loadConfig();
        try {
            recipeConfig.set(name + ".result", resultingItem);
            recipeConfig.set(name + ".recipe", List.of(
                    Map.of("left", materials[0].toString(), "middle", materials[1].toString(), "right", materials[2].toString()),
                    Map.of("left", materials[3].toString(), "middle", materials[4].toString(), "right", materials[5].toString()),
                    Map.of("left", materials[6].toString(), "middle", materials[7].toString(), "right", materials[8].toString())
            ));
            recipeConfig.save(new File(plugin.getDataFolder() + "/recipes.yml"));
        } catch (IOException e){
            plugin.getLogger().severe("Failed to save empty recipe: ");
            e.printStackTrace();
        }
        plugin.getLoadRecipes().loadRecipes();
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

    public List<Map<String, String>> getRecipe(String name){
        return (List<Map<String, String>>) recipeConfig.getList(name+".recipe");
    }
    public ItemStack getResultItem(String name){
        return recipeConfig.getItemStack(name + ".result");
    }
}
