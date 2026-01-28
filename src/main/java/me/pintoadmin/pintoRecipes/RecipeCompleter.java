package me.pintoadmin.pintoRecipes;

import java.util.*;
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
            endArray.addAll(List.of("save", "show", "edit", "list", "remove", "reload"));
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("list")) {
            endArray = new ArrayList<>(plugin.getConfigLoader().recipes);
        }
        return endArray.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
