package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.scheduler.*;

import javax.annotation.*;
import java.util.*;

public class CreateRecipeGUI {
    private final PintoRecipes plugin;
    private Inventory inventory;
    private final List<Integer> craftingSlots = new ArrayList<>();
    private final ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private List<BukkitRunnable> tasks = new ArrayList<>();
    private int resultSlot = 24;
    private boolean currentReadOnly = false;
    private String recipeName;

    private ItemStack backNavItem = new ItemStack(Material.FIREWORK_ROCKET);

    private InventoryView invView;

    public CreateRecipeGUI(PintoRecipes plugin) {
        this.plugin = plugin;
        craftingSlots.addAll(List.of(10,11,12,19,20,21,28,29,30));

        ItemMeta backNavMeta = backNavItem.getItemMeta();
        assert backNavMeta != null;
        backNavMeta.setItemName(color("&4&lClose"));
        FireworkMeta fireworkMeta = (FireworkMeta) backNavMeta;
        fireworkMeta.setPower(0);
        backNavItem.setItemMeta(fireworkMeta);

        constructGUI();
    }

    private void constructGUI(){
        inventory = Bukkit.createInventory(null, 5 * 9, color("&e&lCustom Recipe Creator"));

        ItemMeta meta = unused_space.getItemMeta();
        assert meta != null;
        meta.setItemName(color("&f"));
        unused_space.setItemMeta(meta);

        for(int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, unused_space);

        List<Map<String, String>> recipe = plugin.getConfigLoader().getRecipe(recipeName);
        if(recipe == null){
            for(Integer index : craftingSlots)
                inventory.setItem(index, null);
        } else {
            int round = 1;
            for (Map<String, String> recipeMap : recipe) {
                for(int i = 0; i < 3; i++) {
                    int operatingSlot = craftingSlots.get(round-1);

                    String value = null;
                    if (round % 3 == 1)
                        value = recipeMap.get("left");
                    if (round % 3 == 2)
                        value = recipeMap.get("middle");
                    if (round % 3 == 0)
                        value = recipeMap.get("right");

                    setItem(operatingSlot, value);
                    round++;
                }
            }
        }

        inventory.setItem(resultSlot, plugin.getConfigLoader().getResultItem(recipeName));
        inventory.setItem(8, backNavItem);
    }

    public void sendToPlayer(Player player, String recipeName, boolean readOnly){
        this.recipeName = recipeName;
        currentReadOnly = readOnly;
        constructGUI();
        InventoryView invView = player.openInventory(inventory);
        this.invView = invView;
        if(!readOnly) {
            BukkitRunnable task =
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (player.getOpenInventory() != invView) {
                                if (saveRecipe(recipeName)) {
                                    player.sendMessage(color("&aSaved recipe to recipes.yml"));
                                } else {
                                    player.sendMessage(color("&cCanceled saving recipe"));
                                }
                                this.cancel();
                            }
                        }
                    };
            task.runTaskTimer(plugin, 0L, 2L);
            tasks.add(task);
        }
    }
    public void onClick(InventoryClickEvent event){
        if(event.getClickedInventory() != inventory) return;
        if(currentReadOnly) event.setCancelled(true);

        if(event.getCurrentItem() != null) {
            if(event.getCurrentItem().isSimilar(backNavItem)) event.getWhoClicked().closeInventory();
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
        resultIsAir = inventory.getItem(resultSlot) == null;
        if(!air && !resultIsAir){
            plugin.getConfigLoader().saveRecipe(name, inventory.getItem(resultSlot), materials);
            return true;
        }
        return false;
    }

    private void setItem(int index, @Nullable String value){
        if (value == null || value.equalsIgnoreCase("AIR")) {
            inventory.setItem(index, null);
        } else {
            Material material = Material.valueOf(value.toUpperCase());
            inventory.setItem(index, new ItemStack(material));
        }
    }
    public InventoryView getInvView(){
        return invView;
    }
    private String color(String input){
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
