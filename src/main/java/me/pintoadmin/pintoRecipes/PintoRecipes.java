package me.pintoadmin.pintoRecipes;

import org.bukkit.plugin.java.JavaPlugin;

public final class PintoRecipes extends JavaPlugin {
    public final ConfigLoader configLoader = new ConfigLoader(this);
    public final LoadRecipes loadRecipes = new LoadRecipes(this);

    @Override
    public void onEnable() {
        new SaveRecipeCommand(this);
        new RemoveRecipeCommand(this);
        loadRecipes.loadRecipes();

        getCommand("removerecipe").setTabCompleter(new RecipeCompleter(this));
        getCommand("saverecipe").setTabCompleter(new RecipeCompleter(this));
    }

    @Override
    public void onDisable() {}
}
