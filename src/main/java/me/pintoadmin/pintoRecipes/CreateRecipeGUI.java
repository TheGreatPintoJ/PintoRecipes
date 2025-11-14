package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.scheduler.*;

import java.util.*;

public class CreateRecipeGUI {
    private final PintoRecipes plugin;
    private Inventory inventory;
    private List<Integer> craftingSlots = new ArrayList<>();
    private ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private List<BukkitRunnable> tasks = new ArrayList<>();

    public CreateRecipeGUI(PintoRecipes plugin) {
        this.plugin = plugin;
        craftingSlots.addAll(List.of(10,11,12,19,20,21,28,29,30));
        constructGUI();
    }

    private void constructGUI(){
        inventory = Bukkit.createInventory(null, 5 * 9, color("&e&lCustom Recipe Creator"));

        ItemMeta meta = unused_space.getItemMeta();
        meta.setItemName("");
        unused_space.setItemMeta(meta);

        for(int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, unused_space);

        for(Integer index : craftingSlots)
            inventory.setItem(index, null);

        inventory.setItem(24, null);
    }

    public void sendToPlayer(Player player, String recipeName){
        InventoryView invView = player.openInventory(inventory);
        BukkitRunnable task =
            new BukkitRunnable(){
                @Override
                public void run(){
                    if(player.getOpenInventory() != invView){
                        if(saveRecipe(recipeName)){
                            player.sendMessage(color("&aSaved recipe to recipes.yml"));
                        } else {
                            player.sendMessage(color("&cCanceled saving recipe"));
                        }
                        this.cancel();
                    }
                }
            };
        task.runTaskLater(plugin, 0L);
        tasks.add(task);
    }
    public void onClick(InventoryClickEvent event){
        if(event.getClickedInventory() != inventory) return;
        if(event.getCurrentItem() != null) {
            if (event.getCurrentItem().isSimilar(unused_space)) {
                event.setCancelled(true);
            }
        }
    }
    public void deinit(){
        for(BukkitRunnable task : tasks){
            task.cancel();
        }
    }

    private boolean saveRecipe(String name){
        Material[] materials = new Material[9];
        boolean air = true;
        boolean resultIsAir;
        for(Integer index : craftingSlots){
            materials[craftingSlots.indexOf(index)] = inventory.getItem(index) == null ? Material.AIR : inventory.getItem(index).getType();
            if(inventory.getItem(index) != null) air = false;
        }
        resultIsAir = inventory.getItem(24) == null;
        if(!air && !resultIsAir){
            plugin.configLoader.saveRecipe(name, inventory.getItem(24), materials);
            return true;
        }
        return false;
    }

    private String color(String input){
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
