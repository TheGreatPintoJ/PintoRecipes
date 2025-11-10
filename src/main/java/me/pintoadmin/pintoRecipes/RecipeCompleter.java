package me.pintoadmin.pintoRecipes;

import org.bukkit.command.*;

public class RecipeCompleter implements TabCompleter {
    private final PintoRecipes plugin;

    public RecipeCompleter(PintoRecipes plugin) {
        this.plugin = plugin;
        plugin.getCommand("removerecipe").setTabCompleter(this);
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1){
            return new java.util.ArrayList<>(plugin.configLoader.recipes).stream()
                    .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    .toList();
        }
        return java.util.Collections.emptyList();
    }
}
