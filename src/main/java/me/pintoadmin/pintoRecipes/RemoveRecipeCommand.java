package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.command.*;

public class RemoveRecipeCommand implements CommandExecutor {
    private final PintoRecipes plugin;

    public RemoveRecipeCommand(PintoRecipes plugin) {
        this.plugin = plugin;
        plugin.getCommand("removerecipe").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length != 1){
            sender.sendMessage(ChatColor.RED+"Usage: /removerecipe <recipe name>");
            return true;
        }
        if(!plugin.getConfigLoader().recipes.contains(args[0])){
            sender.sendMessage(ChatColor.RED+"Recipe "+args[0]+" does not exist.");
            return true;
        }
        plugin.getConfigLoader().removeRecipe(args[0]);
        sender.sendMessage(ChatColor.GREEN+"Removed a recipe in recipes.yml with name "+args[0]);
        return true;
    }
}
