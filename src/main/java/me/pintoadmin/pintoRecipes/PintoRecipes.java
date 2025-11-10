package me.pintoadmin.pintoRecipes;

import org.bukkit.plugin.java.JavaPlugin;

public final class PintoRecipes extends JavaPlugin {
    public final ConfigLoader configLoader = new ConfigLoader(this);
    public final LoadRecipes loadRecipes = new LoadRecipes(this);

    @Override
    public void onEnable() {
        new SaveRecipeCommand(this);
        loadRecipes.loadRecipes();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
