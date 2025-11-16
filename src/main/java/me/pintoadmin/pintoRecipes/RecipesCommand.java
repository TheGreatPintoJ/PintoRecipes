package me.pintoadmin.pintoRecipes;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;

public class RecipesCommand implements CommandExecutor {
    private final PintoRecipes plugin;
    public RecipesCommand(PintoRecipes plugin){
        this.plugin = plugin;
        plugin.getCommand("precipes").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("This command can only be executed by a player");
            return true;
        }
        plugin.getRecipesGUI().sendToPlayer(player);
        return true;
    }
}
