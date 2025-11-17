package me.pintoadmin.pintoRecipes;

import org.bukkit.command.*;

import java.util.*;

public class RecipeCompleter implements TabCompleter {
    private final PintoRecipes plugin;

    public RecipeCompleter(PintoRecipes plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> endArray = new ArrayList<>();
        if(args.length == 1){
            endArray.addAll(List.of("save", "show", "edit", "list", "remove"));
        } else if(args.length == 2 && !args[0].equalsIgnoreCase("list")){
            endArray = new ArrayList<>(plugin.getConfigLoader().recipes);
        }
        return endArray
                .stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
