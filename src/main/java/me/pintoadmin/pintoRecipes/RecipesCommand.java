package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public class RecipesCommand implements CommandExecutor {
    private final PintoRecipes plugin;
    public RecipesCommand(PintoRecipes plugin){
        this.plugin = plugin;
        plugin.getCommand("precipes").setExecutor(this);
        plugin.getCommand("precipes").setTabCompleter(new RecipeCompleter(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("This command can only be executed by a player");
            return true;
        }
        if(!sender.hasPermission("pintorecipes.recipes")){
            sender.sendMessage(ChatColor.RED+"You do not have permission to use this command");
            return true;
        }
        if(args.length < 2){
            if(args.length == 0 || args[0].equalsIgnoreCase("list")){
                if(!sender.hasPermission("pintorecipes.recipes.list")){
                    sender.sendMessage(ChatColor.RED+"You do not have permission to use this subcommand");
                    return true;
                }
                plugin.getRecipesGUI().sendToPlayer(player);
            } else {
                player.sendMessage(ChatColor.RED+"You must specify a name for this subcommand");
            }
            return true;
        }
        if(!sender.hasPermission("pintorecipes.recipes."+args[0])){
            sender.sendMessage(ChatColor.RED+"You do not have permission to use this subcommand");
            return true;
        }

        switch(args[0]){
            case "show":
                if(!plugin.getConfigLoader().recipes.contains(args[1]))
                    player.sendMessage(ChatColor.RED+"That recipe doesn't exist");
                else plugin.getCreateRecipeGUI(args[1]).sendToPlayer(player, true);
                break;
            case "save":
                if(plugin.getConfigLoader().recipes.contains(args[1]))
                    player.sendMessage(ChatColor.RED+"That recipe already exists");
                else plugin.getCreateRecipeGUI(args[1]).sendToPlayer(player, false);
                break;
            case "edit":
                if(!plugin.getConfigLoader().recipes.contains(args[1]))
                    player.sendMessage(ChatColor.RED+"That recipe doesn't exist");
                else plugin.getCreateRecipeGUI(args[1]).sendToPlayer(player, false);
                break;
            case "remove":
                if(!plugin.getConfigLoader().recipes.contains(args[1]))
                    player.sendMessage(ChatColor.RED+"That recipe doesn't exist");
                else {
                    plugin.getConfigLoader().removeRecipe(args[1]);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                    player.sendMessage(ChatColor.RED+"Removed recipe "+args[1]+" from config");
                }
                break;
            default:
                player.sendMessage(ChatColor.RED+"Usage: /"+label+" <show|save|edit|remove> [recipe_name]");
        }
        plugin.getConfigLoader().loadConfig();
        return true;
    }
}
