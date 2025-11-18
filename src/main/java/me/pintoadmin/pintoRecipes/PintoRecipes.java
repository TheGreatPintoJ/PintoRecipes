package me.pintoadmin.pintoRecipes;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PintoRecipes extends JavaPlugin {
    private static PintoRecipes instance = null;
    private final ConfigLoader configLoader = new ConfigLoader(this);
    private final LoadRecipes loadRecipes = new LoadRecipes(this);
    private final RecipesGUI recipesGUI = new RecipesGUI(this);

    private final Map<String, CreateRecipeGUI> editGUIs = new HashMap<>();

    public static JavaPlugin thisPlugin(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        new RecipesCommand(this);
        new InventoryEvents(this);
        loadRecipes.loadRecipes();
    }

    @Override
    public void onDisable() {
        for(Map.Entry<String, CreateRecipeGUI> entry : editGUIs.entrySet())
            entry.getValue().deinit();
        recipesGUI.deinit();
    }

    public CreateRecipeGUI getCreateRecipeGUI(String recipeName){
        if(editGUIs.get(recipeName) == null)
            editGUIs.putIfAbsent(recipeName, new CreateRecipeGUI(this, recipeName));
        return editGUIs.get(recipeName);
    }
    public Map<String, CreateRecipeGUI> getEditGUIs() {
        return editGUIs;
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
