package me.pintoadmin.pintoRecipes;

import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UncraftCommand implements CommandExecutor {
    private final PintoRecipes plugin;

    public UncraftCommand(PintoRecipes plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("uncraft")).setExecutor(this);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player");
            return true;
        }
        plugin.addUncraftingGUI(new UncraftingGUI(plugin, player));
        return true;
    }
}
