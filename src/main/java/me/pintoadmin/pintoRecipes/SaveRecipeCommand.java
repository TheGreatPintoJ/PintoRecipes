package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveRecipeCommand implements CommandExecutor {
    private final PintoRecipes plugin;

    public SaveRecipeCommand(PintoRecipes plugin) {
        this.plugin = plugin;
        plugin.getCommand("saverecipe").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage(ChatColor.RED+"This command can only be executed by players.");
            return true;
        }
        if(args.length == 0 || args[0].isEmpty()){
            sender.sendMessage(ChatColor.RED+"Usage: /saverecipe <recipe name>");
            return true;
        }
        if(plugin.configLoader.recipes.contains(args[0])){
            player.sendMessage(ChatColor.RED+"There is already a recipe with this name in the config");
            return true;
        }
        plugin.getRecipeGUI().sendToPlayer(player, args[0]);
        return true;
    }
}
