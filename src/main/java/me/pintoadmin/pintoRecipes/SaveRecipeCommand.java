package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SaveRecipeCommand implements CommandExecutor {
    private final PintoRecipes plugin;

    public SaveRecipeCommand(PintoRecipes plugin) {
        this.plugin = plugin;
        plugin.getCommand("saverecipe").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }
        if(args.length == 0){
            sender.sendMessage("Usage: /save <recipe name>");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType().isAir()){
            sender.sendMessage(ChatColor.RED+"You must be holding an item to save a recipe.");
            return true;
        }
        plugin.configLoader.saveEmptyRecipe(args[0], item);
        return true;
    }
}
