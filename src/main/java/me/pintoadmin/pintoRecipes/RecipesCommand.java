package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public record RecipesCommand(PintoRecipes plugin) implements CommandExecutor {
    public RecipesCommand(PintoRecipes plugin) {
        this.plugin = plugin;
        PluginCommand precipesCommand = plugin.getCommand("precipes");
        if (precipesCommand != null) {
            precipesCommand.setExecutor(this);
            precipesCommand.setTabCompleter(new RecipeCompleter(plugin));
        }
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player");
            return true;
        }
        if(args.length == 0) return true;
        if (!sender.hasPermission("pintorecipes.recipes") || !sender.hasPermission("pintorecipes.recipes." + args[0])) {
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
            } else if(args[0].equalsIgnoreCase("reload")) {
                if(!sender.hasPermission("pintorecipes.recipes.reload")){
                    sender.sendMessage(ChatColor.RED+"You do not have permission to use this command");
                    return true;
                }
                plugin.getLoadRecipes().reloadRecipes();
                player.sendMessage(ChatColor.GREEN + "Reloaded recipes");
            } else if(args[0].equalsIgnoreCase("debug")){
                if(!sender.hasPermission("pintorecipes.recipes.debug")){
                    sender.sendMessage(ChatColor.RED+"You do not have permission to use this command");
                    return true;
                }
                plugin.debugEnabled = !plugin.debugEnabled;
                player.sendMessage("Debug mode is "+ (plugin.debugEnabled ? "on":"off"));
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
}
