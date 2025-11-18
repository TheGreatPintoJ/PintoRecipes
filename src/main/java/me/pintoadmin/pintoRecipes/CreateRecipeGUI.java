package me.pintoadmin.pintoRecipes;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.*;

import javax.annotation.*;
import java.util.*;

public class CreateRecipeGUI {
    private final PintoRecipes plugin;
    private Inventory inventory;

    private final List<UUID> playersViewing = new ArrayList<>();

    private final List<Integer> craftingSlots = new ArrayList<>(List.of(10,11,12,19,20,21,28,29,30));
    private final int furnaceSlot = 20;
    private final int resultSlot = 24;

    private boolean currentReadOnly = false;
    private final String recipeName;

    private int selectedTypeIndex;
    private final List<String> typeList = List.of("shaped", "shapeless", "furnace");

    private final ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private final NamespacedKey unusedSpaceKey = new NamespacedKey(PintoRecipes.thisPlugin(), "unusedSpaceID");

    private final ItemStack backNavItem = new ItemStack(Material.FIREWORK_ROCKET);

    private final ItemStack typeSelectItem = new ItemStack(Material.PAPER);
    private final NamespacedKey typeSelectIDKey = new NamespacedKey(PintoRecipes.thisPlugin(), "typeSelectID");


    public CreateRecipeGUI(PintoRecipes plugin, String recipeName){
        this.plugin = plugin;
        this.recipeName = recipeName;

        selectedTypeIndex = typeList.indexOf(plugin.getConfigLoader().getType(recipeName));

        ItemMeta unusedMeta = unused_space.getItemMeta();
        unusedMeta.setItemName(color("&f"));
        unusedMeta.getPersistentDataContainer().set(unusedSpaceKey, PersistentDataType.STRING, "unused_space");
        unused_space.setItemMeta(unusedMeta);

        inventory = Bukkit.createInventory(null, 5 * 9, color("&eRecipe - "+recipeName));
        loadGUI();
    }

    public void loadGUI(){
        for(int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, unused_space);

        if(recipeName != null) {
            switch (plugin.getConfigLoader().getType(recipeName)) {
                case "shaped":
                    List<Map<String, String>> shapedRecipe = (List<Map<String, String>>) plugin.getConfigLoader().getRecipe(recipeName);
                    if (shapedRecipe == null) {
                        for (Integer index : craftingSlots)
                            inventory.setItem(index, null);
                    } else {
                        int round = 1;
                        for (Map<String, String> recipeMap : shapedRecipe) {
                            for (int i = 0; i < 3; i++) {
                                int operatingSlot = craftingSlots.get(round - 1);

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
                    break;
                case "shapeless":
                    List<String> shapelessRecipe = (List<String>) plugin.getConfigLoader().getRecipe(recipeName);
                    for(int i = 0; i < 9; i++){
                        try {
                            setItem(craftingSlots.get(i), shapelessRecipe.get(i));
                        } catch (IndexOutOfBoundsException ignored){
                            inventory.setItem(craftingSlots.get(i), null);
                        }
                    }
                    break;
                case "furnace":
                    String furnaceRecipe = (String) plugin.getConfigLoader().getRecipe(recipeName);
                    setItem(furnaceSlot, furnaceRecipe);
                    break;
                // TODO: add other types
            }
        }

        ItemMeta backNavMeta = backNavItem.getItemMeta();
        assert backNavMeta != null;
        backNavMeta.setItemName(color("&4&lClose"));
        FireworkMeta fireworkMeta = (FireworkMeta) backNavMeta;
        fireworkMeta.setPower(0);
        backNavItem.setItemMeta(fireworkMeta);

        ItemMeta typeSelectMeta = typeSelectItem.getItemMeta();
        assert typeSelectMeta != null;
        typeSelectMeta.setItemName(color("&lRecipe type: "+getCurrentType()));
        typeSelectMeta.setLore(List.of(color("&d&lLeft click to select next"), color("&d&lRight click to select previous")));

        typeSelectMeta.getPersistentDataContainer().set(typeSelectIDKey, PersistentDataType.STRING, "typeSelectItem");

        typeSelectItem.setItemMeta(typeSelectMeta);

        inventory.setItem(resultSlot, plugin.getConfigLoader().getResultItem(recipeName));
        inventory.setItem(8, backNavItem);
        inventory.setItem(0, typeSelectItem);

    }
    public void sendToPlayer(Player player, boolean readOnly){
        currentReadOnly = readOnly;
        if(!playersViewing.contains(player.getUniqueId()))
            playersViewing.add(player.getUniqueId());
        player.openInventory(inventory);
    }
    public void save(){
        String type = typeList.get(selectedTypeIndex);
        if(inventory.getItem(resultSlot) == null) return;
        switch(type) {
            case "shaped":
                Material[] shapedMaterials = new Material[9];
                boolean resultIsAir;
                for (Integer index : craftingSlots) {
                    ItemStack item = inventory.getItem(index);
                    if(item != null) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null) {
                            PersistentDataContainer container = meta.getPersistentDataContainer();
                            String value = container.get(unusedSpaceKey, PersistentDataType.STRING);
                            if (value != null) {
                                if (value.equals("unused_space")) {
                                    shapedMaterials[craftingSlots.indexOf(index)] = Material.AIR;
                                    continue;
                                }
                            }
                        }
                    }
                    shapedMaterials[craftingSlots.indexOf(index)] = item == null ? Material.AIR : item.getType();
                }
                resultIsAir = inventory.getItem(resultSlot) == null;
                if (!resultIsAir) {
                    plugin.getConfigLoader().saveShapedRecipe(recipeName, inventory.getItem(resultSlot), shapedMaterials);
                    return;
                }
                break;
            case "shapeless":
                List<String> shapelessMaterials = new ArrayList<>();
                for(Integer index : craftingSlots){
                    ItemStack item = inventory.getItem(index);
                    if(item == null) continue;
                    Material material = item.getType();
                    ItemMeta meta = item.getItemMeta();
                    if(meta != null) {
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        String value = container.get(unusedSpaceKey, PersistentDataType.STRING);
                        if(value != null) {
                            if (value.equals("unused_space"))
                                continue;
                        }
                    }
                    if(material.isAir()) continue;
                    shapelessMaterials.add(material.toString());
                }
                if(shapelessMaterials.isEmpty()) return;
                plugin.getConfigLoader().saveShapelessRecipe(recipeName, inventory.getItem(resultSlot), shapelessMaterials);
                break;
            case "furnace":
                ItemStack furnaceItem = inventory.getItem(furnaceSlot);
                if(furnaceItem == null) return;
                Material furnaceMaterial = furnaceItem.getType();
                plugin.getConfigLoader().saveFurnaceRecipe(recipeName, inventory.getItem(resultSlot), furnaceMaterial);
                break;
        }
    }
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(!playersViewing.contains(player.getUniqueId())) return;

        if(currentReadOnly) event.setCancelled(true);

        if(event.getCurrentItem() != null) {

            if(event.getCurrentItem().isSimilar(backNavItem)) {
                plugin.getRecipesGUI().sendToPlayer(player);
                event.setCancelled(true);
                onClose(player);
            }

            if(currentReadOnly) return;

            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if(meta == null) return;

            String idKey = meta.getPersistentDataContainer().get(typeSelectIDKey, PersistentDataType.STRING);

            if(idKey != null && idKey.equals("typeSelectItem")){

                event.setCancelled(true);
                if(event.getClick() == ClickType.LEFT){
                    selectedTypeIndex++;
                    if(selectedTypeIndex > typeList.size()-1){
                        selectedTypeIndex = 0;
                    }
                } else if(event.getClick() == ClickType.RIGHT){
                    selectedTypeIndex--;
                    if(selectedTypeIndex < 0){
                        selectedTypeIndex = typeList.size()-1;
                    }
                }

                save();
                loadGUI();
                sendToPlayer(player, currentReadOnly);
            }

            if (event.getCurrentItem().isSimilar(unused_space))
                event.setCancelled(true);
        }
    }
    public void onClose(Player player){
        save();
        playersViewing.remove(player.getUniqueId());
    }

    public void deinit(){}

    public String getCurrentType(){
        return typeList.get(selectedTypeIndex);
    }

    private void setItem(int index, @Nullable String value){
        if (value == null || value.equalsIgnoreCase("AIR")) {
            inventory.setItem(index, null);
        } else {
            Material material = Material.valueOf(value.toUpperCase());
            inventory.setItem(index, new ItemStack(material));
        }
    }
    private String color(String input){
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
