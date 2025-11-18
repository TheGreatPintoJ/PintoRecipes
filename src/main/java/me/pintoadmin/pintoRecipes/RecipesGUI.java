package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.scheduler.*;

import java.util.*;

public class RecipesGUI {
    private final PintoRecipes plugin;
    private Inventory inventory;
    private final ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private List<String> recipes;
    private final int size = 6*9;
    private int currentPage = 0;

    private List<BukkitRunnable> tasks = new ArrayList<>();

    private ItemStack leftNavItem = new ItemStack(Material.ARROW);
    private ItemStack pageNavItem = new ItemStack(Material.PAPER);
    private ItemStack rightNavItem = new ItemStack(Material.ARROW);
    private ItemStack newNavItem = new ItemStack(Material.RED_DYE);

    public RecipesGUI(PintoRecipes plugin) {
        this.plugin = plugin;

        ItemMeta leftNavMeta = leftNavItem.getItemMeta();
        leftNavMeta.setItemName(color("&lPrevious Page"));
        leftNavItem.setItemMeta(leftNavMeta);

        ItemMeta rightNavMeta = rightNavItem.getItemMeta();
        rightNavMeta.setItemName(color("&lNext Page"));
        rightNavItem.setItemMeta(rightNavMeta);

        ItemMeta newNavMeta = newNavItem.getItemMeta();
        newNavMeta.setItemName(color("&c&lCreate new recipe"));
        newNavItem.setItemMeta(newNavMeta);
    }

    private void constructGUI(){
        plugin.getConfigLoader().loadConfig();
        recipes = plugin.getConfigLoader().recipes;
        inventory = Bukkit.createInventory(null, size, color("&8&l&oCustom Recipes"));

        ItemMeta unused_meta = unused_space.getItemMeta();
        unused_meta.setItemName(color("&f"));
        unused_space.setItemMeta(unused_meta);

        for(int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, unused_space);

        for(int i = 0; i < size-18; i++){
            try {
                String recipeName = recipes.get(
                        currentPage * (size - 18) + i // e.g. 0 * (54 - 18) + 1 = 1 OR 1 * (54 - 18) + 2 = 38
                );
                ItemStack item = plugin.getConfigLoader().getResultItem(recipeName).clone();
                ItemMeta meta = item.getItemMeta();
                meta.setLore(List.of("", color("&8&oCustom Recipe ID: "+recipeName)));
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            } catch (IndexOutOfBoundsException ignored){
                inventory.setItem(i, null);
            }
        }

        if(currentPage != 0)
            inventory.setItem(size-6, leftNavItem);

        ItemMeta pageNavMeta = pageNavItem.getItemMeta();
        pageNavMeta.setItemName(color("&lPage: "+(currentPage+1)));
        pageNavItem.setItemMeta(pageNavMeta);
        inventory.setItem(size-5, pageNavItem);

        try {
            if (plugin.getConfigLoader().getResultItem(recipes.get(currentPage * (size - 18))) != null)
                inventory.setItem(size - 4, rightNavItem);
        } catch (IndexOutOfBoundsException ignored){}

        inventory.setItem(size-1, newNavItem);
    }

    public void sendToPlayer(Player player){
        constructGUI();
        InventoryView invView = player.openInventory(inventory);
    }
    public void onClick(InventoryClickEvent event){
        if(event.getClickedInventory() != inventory) return;
        if(event.getCurrentItem() != null) {
            event.setCancelled(true);
            if(event.getCurrentItem().isSimilar(rightNavItem)){
                currentPage++;
                sendToPlayer((Player) event.getWhoClicked());
            } else if(event.getCurrentItem().isSimilar(leftNavItem)){
                currentPage--;
                sendToPlayer((Player) event.getWhoClicked());
            } else if (event.getCurrentItem().isSimilar(newNavItem)) {
                plugin.getCreateRecipeGUI().sendToPlayer((Player) event.getWhoClicked(), "new_recipe", false);
                backOnClose((Player) event.getWhoClicked(), plugin.getCreateRecipeGUI().getInvView());
            } else {
                for(int i = 0; i < size-18; i++){
                    try {
                        ItemStack item = plugin.getConfigLoader().getResultItem(
                                recipes.get(
                                        currentPage * (size - 18) + i
                                ));
                        if(event.getCurrentItem().getType().equals(item.getType()) && event.getCurrentItem().getAmount() == item.getAmount()){
                            plugin.getCreateRecipeGUI().sendToPlayer((Player) event.getWhoClicked(),
                                    recipes.get(
                                            currentPage * (size-18) + i
                                    ), true);
                            backOnClose((Player) event.getWhoClicked(), plugin.getCreateRecipeGUI().getInvView());
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored){}
                }
            }
        }
    }
    public void deinit(){
        for(BukkitRunnable task : tasks){
            task.cancel();
        }
    }

    private void backOnClose(Player player, InventoryView invView){
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getOpenInventory() != invView) {
                    sendToPlayer(player);
                    this.cancel();
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 2L);
        tasks.add(task);
    }

    private String color(String input){
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
