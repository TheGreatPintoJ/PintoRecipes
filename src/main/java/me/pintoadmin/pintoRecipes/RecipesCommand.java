package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.*;

import java.util.*;

public record RecipesCommand(PintoRecipes plugin) implements CommandExecutor {
    public RecipesCommand(PintoRecipes plugin) {
        this.plugin = plugin;
        PluginCommand precipesCommand = plugin.getCommand("precipes");
        if (precipesCommand != null) {
            precipesCommand.setExecutor(this);
            precipesCommand.setTabCompleter(new RecipeCompleter(plugin));
        }
    }

    private static final List<String> consoleSubcommands = List.of("list", "reload", "remove", "show", "debug", "rename");

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            if(args.length == 0) {
                sender.sendMessage("You must specify a subcommand!");
                return true;
            }
            if(consoleSubcommands.contains(args[0].toLowerCase())){
                consoleCommand(sender, args);
            } else sender.sendMessage("This command can only be executed by a player");
            return true;
        }
        if(args.length == 0 && player.hasPermission("pintorecipes.recipes.list"))
            plugin.getRecipesGUI().sendToPlayer(player);

        if (args.length == 0) return true;
        if (!sender.hasPermission("pintorecipes.recipes")
                || !sender.hasPermission("pintorecipes.recipes." + args[0])) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return true;
        }
        if (args.length < 2) {
            if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("pintorecipes.recipes.list")) {
                    sender.sendMessage(
                            ChatColor.RED + "You do not have permission to use this command");
                    return true;
                }
                plugin.getRecipesGUI().sendToPlayer(player);
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("pintorecipes.recipes.reload")) {
                    sender.sendMessage(
                            ChatColor.RED + "You do not have permission to use this command");
                    return true;
                }
                plugin.getLoadRecipes().reloadRecipes();
                player.sendMessage(ChatColor.GREEN + "Reloaded recipes");
            } else if (args[0].equalsIgnoreCase("debug")) {
                if (!sender.hasPermission("pintorecipes.recipes.debug")) {
                    sender.sendMessage(
                            ChatColor.RED + "You do not have permission to use this command");
                    return true;
                }
                plugin.debugEnabled = !plugin.debugEnabled;
                player.sendMessage("Debug mode is " + (plugin.debugEnabled ? "on" : "off"));
            } else {
                player.sendMessage(ChatColor.RED + "You must specify a name for this command");
            }
            return true;
        }

        switch (args[0]) {
            case "show":
                if (!plugin.getConfigLoader().recipes.contains(args[1]))
                    player.sendMessage(ChatColor.RED + "That recipe doesn't exist");
                else plugin.getCreateRecipeGUI(args[1]).sendToPlayer(player, true);
                break;
            case "save":
                if (plugin.getConfigLoader().recipes.contains(args[1]))
                    player.sendMessage(ChatColor.RED + "That recipe already exists");
                else plugin.getCreateRecipeGUI(args[1]).sendToPlayer(player, false);
                break;
            case "edit":
                if (!plugin.getConfigLoader().recipes.contains(args[1]))
                    player.sendMessage(ChatColor.RED + "That recipe doesn't exist");
                else plugin.getCreateRecipeGUI(args[1]).sendToPlayer(player, false);
                break;
            case "remove":
                if (!plugin.getConfigLoader().recipes.contains(args[1]))
                    player.sendMessage(ChatColor.RED + "That recipe doesn't exist");
                else {
                    plugin.getConfigLoader().removeRecipe(args[1]);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                    player.sendMessage(
                            ChatColor.RED + "Removed recipe " + args[1] + " from config");
                }
                break;
            default:
                player.sendMessage(
                        ChatColor.RED
                                + "Usage: /"
                                + label
                                + " <list|show|save|edit|remove|reload> [recipe_name]");
        }
        plugin.getConfigLoader().loadConfig();
        return true;
    }

    private void consoleCommand(CommandSender sender, String[] args){
        String recipeName;
        switch (args[0].toLowerCase()){
            case "list":
                sender.sendMessage("Recipes in 'recipes.yml': ");
                List<String> recipes = plugin.getConfigLoader().recipes;
                for(String recipe : recipes){
                    sender.sendMessage("- "+recipe);
                }
                break;
            case "remove":
                // Take args[1] and remove
                if(args.length != 2){
                    sender.sendMessage("You must specify a recipe for this command");
                    break;
                }
                recipeName = args[1];
                if (!plugin.getConfigLoader().recipes.contains(recipeName))
                    sender.sendMessage("That recipe doesn't exist");
                else {
                    plugin.getConfigLoader().removeRecipe(recipeName);
                    sender.sendMessage("Removed recipe " + recipeName + " from config");
                }
                break;
            case "reload":
                // Reload all recipes
                plugin.getLoadRecipes().reloadRecipes();
                sender.sendMessage("Reloaded recipes");
                break;
            case "debug":
                // Toggle Debug mode
                plugin.debugEnabled = !plugin.debugEnabled;
                sender.sendMessage("Debug is now "+(plugin.debugEnabled ? "on":"off"));
                break;
            case "show":
                // Makeshift 'show' recipe
                if(args.length != 2){
                    sender.sendMessage("You must specify a recipe for this command");
                    break;
                }
                if (!plugin.getConfigLoader().recipes.contains(args[1])) {
                    sender.sendMessage("That recipe doesn't exist");
                    break;
                }

                recipeName = args[1];
                ItemStack result = plugin.getConfigLoader().getResultItem(recipeName);
                Object recipe = plugin.getConfigLoader().getRecipe(recipeName);
                String recipeType = plugin.getConfigLoader().getType(recipeName);

                List<Map<String, String>> shapedRecipe = plugin.toShapedRecipe(recipe);
                List<String> shapelessRecipe = plugin.toStringList(recipe);

                if(recipe instanceof String itemName){ // Case for smelting/stonecutting
                    sender.sendMessage(result.getType()+" is "+recipeType+" with "+itemName+" item");
                } else if(shapedRecipe != null && !shapedRecipe.isEmpty()){
                    List<Map<String, String>> items = plugin.toShapedRecipe(recipe);
                    sender.sendMessage(result.getType()+" is "+recipeType+" with "+items+" items");

                    String pre  = "-------------";
                    String set1 = "| 1 | 2 | 3 |";
                    String set2 = "| 4 | 5 | 6 |";
                    String set3 = "| 7 | 8 | 9 |";
                    String post = "-------------";

                    List<String> sets = List.of(set1, set2, set3);
                    List<String> finalSets = new ArrayList<>();
                    LinkedHashMap<Character, String> abbreviations = new LinkedHashMap<>();

                    int setNum = 0;
                    int round = 1;
                    for(Map<String, String> map : items){
                        String set = sets.get(setNum);
                        for (int i = 0; i < 3; i++) {
                            String value = null;
                            if (round % 3 == 1) value = map.get("left");
                            if (round % 3 == 2) value = map.get("middle");
                            if (round % 3 == 0) value = map.get("right");

                            if (value != null && !value.isEmpty()) {
                                char abbreviation;
                                int j = 0;
                                do {
                                    abbreviation = value.toUpperCase().charAt(j++);
                                    if(abbreviations.containsKey(abbreviation) && abbreviations.get(abbreviation).equals(value)) break;
                                } while(abbreviations.putIfAbsent(abbreviation, value.toUpperCase()) != null);

                                set = set.replace(
                                        String.valueOf(round).charAt(0),
                                        abbreviation);
                            }
                            round++;
                        }
                        setNum++;
                        finalSets.add(set);
                    }

                    sender.sendMessage(pre);
                    sender.sendMessage(finalSets.getFirst());
                    sender.sendMessage(finalSets.get(1));
                    sender.sendMessage(finalSets.getLast());
                    sender.sendMessage(post);
                    sender.sendMessage("");

                    for (Map.Entry<Character, String> abb : abbreviations.entrySet())
                        sender.sendMessage(abb.getKey()+" - "+abb.getValue());

                } else if(shapelessRecipe != null && !shapelessRecipe.isEmpty()){
                    List<String> items = plugin.toStringList(recipe);
                    sender.sendMessage(result.getType()+" is "+recipeType+" with "+items+" items");
                }
                break;
            case "rename":
                // Take 3 args: 0:rename 1:oldname 2:newname
                if(args.length != 3){
                    sender.sendMessage("Usage: /pr rename <oldname> <newname>");
                    break;
                }
                if (!plugin.getConfigLoader().recipes.contains(args[1])) {
                    sender.sendMessage("That recipe doesn't exist");
                    break;
                }
                String oldName = args[1];
                String newName = args[2];

                plugin.getConfigLoader().renameRecipe(oldName, newName);
                sender.sendMessage("Recipe '"+oldName+"' has been renamed to '"+newName+"'");
                break;
        }
    }
}
