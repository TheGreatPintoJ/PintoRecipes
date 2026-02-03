package me.pintoadmin.pintoRecipes;

import java.util.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class PintoRecipes extends JavaPlugin {
    private static PintoRecipes instance = null;
    private final ConfigLoader configLoader = new ConfigLoader(this);
    private final LoadRecipes loadRecipes = new LoadRecipes(this);
    private final SQLiteManager sqLiteManager = new SQLiteManager(this);
    private final RecipesGUI recipesGUI = new RecipesGUI(this);

    private final Map<String, CreateRecipeGUI> editGUIs = new HashMap<>();

    private final List<UncraftingGUI> uncraftingGUIS = new ArrayList<>();

    public boolean debugEnabled = false;

    public static JavaPlugin thisPlugin() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        new RecipesCommand(this);
        new InventoryEvents(this);
        new UncraftCommand(this);
        loadRecipes.loadRecipes();
    }

    @Override
    public void onDisable() {
        for (Map.Entry<String, CreateRecipeGUI> entry : editGUIs.entrySet())
            entry.getValue().deinit();
        recipesGUI.deinit();

        sqLiteManager.deinit();
    }

    public CreateRecipeGUI getCreateRecipeGUI(String recipeName) {
        if (editGUIs.get(recipeName) == null)
            editGUIs.putIfAbsent(recipeName, new CreateRecipeGUI(this, recipeName));
        return editGUIs.get(recipeName);
    }

    public List<UncraftingGUI> getUncraftingGUIS() {
        return uncraftingGUIS;
    }
    public void addUncraftingGUI(UncraftingGUI gui){
        uncraftingGUIS.add(gui);
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

    public SQLiteManager getSqLiteManager() {
        return sqLiteManager;
    }

    // Helper converters to avoid unchecked casts
    List<Map<String, String>> toShapedRecipe(Object obj) {
        if (!(obj instanceof List<?> outer)) return null;
        List<Map<String, String>> result = new ArrayList<>();
        for (Object o : outer) {
            if (!(o instanceof Map<?, ?> raw)) return null;
            Map<String, String> converted = new HashMap<>();
            for (Map.Entry<?, ?> e : raw.entrySet()) {
                String k = e.getKey() == null ? null : String.valueOf(e.getKey());
                String v = e.getValue() == null ? null : String.valueOf(e.getValue());
                converted.put(k, v);
            }
            result.add(converted);
        }
        return result;
    }

    List<String> toStringList(Object obj) {
        if (!(obj instanceof List)) return null;
        List<String> result = new ArrayList<>();
        for (Object o : (List<?>) obj) result.add(o == null ? null : String.valueOf(o));
        return result;
    }
}
