package me.pintoadmin.pintoRecipes;

import net.wesjd.anvilgui.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

public class RecipesGUI {
    private final PintoRecipes plugin;
    private Inventory inventory;
    private final ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private List<String> recipes;
    private final int size = 6*9;
    private int currentPage = 0;

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
                String recipeType = plugin.getConfigLoader().getType(recipeName);
                ItemStack itemOG = plugin.getConfigLoader().getResultItem(recipeName);
                if(itemOG == null) throw new IndexOutOfBoundsException();
                ItemStack item = itemOG.clone();
                ItemMeta meta = item.getItemMeta();
                meta.setLore(List.of("",
                        color("&r&8ID: "+recipeName),
                        color("&r&8Type: "+recipeType),
                        color("&r&6Right click to edit recipe"),
                        color("&r&cShift-right click to remove recipe"),
                        color("&r&aShift-left click to rename recipe")));
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
                plugin.getCreateRecipeGUI("new_recipe").sendToPlayer((Player) event.getWhoClicked(), false);
            } else {
                for(int i = 0; i < size-18; i++){
                    try {
                        ItemStack item = plugin.getConfigLoader().getResultItem(
                                recipes.get(
                                        currentPage * (size - 18) + i
                                ));
                        ItemStack clickedItem = event.getCurrentItem();
                        if(clickedItem.getItemMeta() == null) continue;
                        if(clickedItem.getType().equals(item.getType()) && clickedItem.getAmount() == item.getAmount() && clickedItem.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())){
                            String recipeName = recipes.get( currentPage * (size - 18) + i );
                            if(event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                plugin.getConfigLoader().removeRecipe(recipeName);
                                Player player = (Player) event.getWhoClicked();
                                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                                player.sendMessage(ChatColor.RED + "Removed recipe " + recipeName + " from config");
                                sendToPlayer(player);
                            } else if(event.getClick().equals(ClickType.RIGHT)) {
                                Player player = (Player) event.getWhoClicked();
                                plugin.getCreateRecipeGUI(recipeName).sendToPlayer(player, false);
                            } else if(event.getClick().equals(ClickType.SHIFT_LEFT)){
                                Player player = (Player) event.getWhoClicked();
                                AnvilGUI.Builder renameGUI = new AnvilGUI.Builder()
                                        .onClose(stateSnapshot -> {
                                            if(!stateSnapshot.getText().equals(recipeName))
                                                stateSnapshot.getPlayer().sendMessage(color("&cCancelled renaming"));
                                            else player.sendMessage(ChatColor.GREEN+"Renamed "+recipeName+" to "+stateSnapshot.getText());
                                        })
                                        .onClick((slot, stateSnapshot) -> {
                                            if(slot != AnvilGUI.Slot.OUTPUT) {
                                                return Collections.emptyList();
                                            }

                                            plugin.getConfigLoader().renameRecipe(recipeName, stateSnapshot.getText());
                                            plugin.getSqLiteManager().renameColumn(recipeName, stateSnapshot.getText());
                                            sendToPlayer(player);
                                            return Collections.emptyList();
                                        })
                                        .text(recipeName)
                                        .title("Enter the new name")
                                        .plugin(PintoRecipes.thisPlugin());
                                renameGUI.open(player);

                            } else {
                                plugin.getCreateRecipeGUI(recipeName).sendToPlayer((Player) event.getWhoClicked(), true);
                            }
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored){}
                }
            }
        }
    }
    public void deinit(){}

    private String color(String input){
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
