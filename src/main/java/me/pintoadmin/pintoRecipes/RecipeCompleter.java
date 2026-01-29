package me.pintoadmin.pintoRecipes;

import java.util.*;
import java.util.stream.*;

import org.bukkit.command.*;
import org.jetbrains.annotations.*;

public record RecipeCompleter(PintoRecipes plugin) implements TabCompleter {
    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            String[] args) {
        List<String> endArray = new ArrayList<>();
        if (args.length == 1) {
            endArray.addAll(Stream.of("save", "show", "edit", "list", "remove", "reload").filter((string) -> sender.hasPermission("pintorecipes.recipes."+string)).toList());
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("list")) {
            if(!sender.hasPermission("pintorecipes.recipes."+args[0])) return List.of();
            endArray = new ArrayList<>(plugin.getConfigLoader().recipes);
        }
        return endArray.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
