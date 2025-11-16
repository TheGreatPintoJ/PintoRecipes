package me.pintoadmin.pintoRecipes;

import org.bukkit.plugin.java.JavaPlugin;

public final class PintoRecipes extends JavaPlugin {
    private final ConfigLoader configLoader = new ConfigLoader(this);
    private final LoadRecipes loadRecipes = new LoadRecipes(this);
    private final CreateRecipeGUI customRecipeGUI = new CreateRecipeGUI(this);

    @Override
    public void onEnable() {
        new SaveRecipeCommand(this);
        new RemoveRecipeCommand(this);
        new InventoryEvents(this);
        loadRecipes.loadRecipes();

        getCommand("removerecipe").setTabCompleter(new RecipeCompleter(this));
        getCommand("saverecipe").setTabCompleter(new RecipeCompleter(this));
    }

    @Override
    public void onDisable() {
        customRecipeGUI.deinit();
    }

    public CreateRecipeGUI getCreateRecipeGUI(){
        return customRecipeGUI;
    }
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public LoadRecipes getLoadRecipes() {
        return loadRecipes;
    }

    public RecipesGUI getRecipesGUI() {
        return recipesGUI;
    }
}
