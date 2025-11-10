package me.pintoadmin.pintoRecipes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ConfigLoader {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    public List<String> recipes;

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
    private void loadConfig() {
        config = YamlConfiguration.loadConfiguration(
                new File(plugin.getDataFolder(), "rules.yml")
        );
        recipes = config.getKeys(true).stream().toList();
    }
    public void saveEmptyRecipe(String name, ItemStack resultingItem){
        config.set(name+".result", resultingItem);
        config.set(name+".recipe", List.of(defaultRecipe, defaultRecipe,  defaultRecipe));
    }
    public List<Map<String, String>> getRecipe(String name){
        return (List<Map<String, String>>) config.getList(name+".recipe");
    }
    public ItemStack getResultItem(String name){
        return config.getItemStack(name + ".result");
    }
}
