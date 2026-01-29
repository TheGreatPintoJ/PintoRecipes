package me.pintoadmin.pintoRecipes;

import java.util.*;
import net.wesjd.anvilgui.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.scheduler.*;

public class RecipesGUI {
    private final PintoRecipes plugin;
    private Inventory inventory;
    private final ItemStack unused_space = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private List<String> recipes;
    private final int size = 6 * 9;
    private int currentPage = 0;

    private final List<ItemStack[]> pages = new ArrayList<>();

    public boolean somethingChanged = false;

    private final ItemStack leftNavItem = new ItemStack(Material.ARROW);
    private final ItemStack pageNavItem = new ItemStack(Material.PAPER);
    private final ItemStack rightNavItem = new ItemStack(Material.ARROW);
    private final ItemStack newNavItem = new ItemStack(Material.RED_DYE);

    private final List<String> constantItemLore = List.of(
            color("&r&dLeft click to view recipe"),
            color("&r&6Right click to edit recipe"),
            color("&r&cShift-right click to remove recipe"),
            color("&r&aShift-left click to rename recipe"));

    public RecipesGUI(PintoRecipes plugin) {
        this.plugin = plugin;

        ItemMeta leftNavMeta = leftNavItem.getItemMeta();
        assert leftNavMeta != null;
        leftNavMeta.setItemName(color("&lPrevious Page"));
        leftNavItem.setItemMeta(leftNavMeta);

        ItemMeta rightNavMeta = rightNavItem.getItemMeta();
        assert rightNavMeta != null;
        rightNavMeta.setItemName(color("&lNext Page"));
        rightNavItem.setItemMeta(rightNavMeta);

        ItemMeta newNavMeta = newNavItem.getItemMeta();
        assert newNavMeta != null;
        newNavMeta.setItemName(color("&c&lCreate new recipe"));
        newNavItem.setItemMeta(newNavMeta);

        ItemMeta unused_meta = unused_space.getItemMeta();
        assert unused_meta != null;
        unused_meta.setItemName(color("&f"));
        unused_space.setItemMeta(unused_meta);

        constructGUI();
    }

    private void constructGUI() {
        plugin.getConfigLoader().loadConfig();
        recipes = plugin.getConfigLoader().recipes;
        inventory = Bukkit.createInventory(null, size, color("&8&l&oCustom Recipes"));

        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, unused_space);

        updateItems();

        updateNav();
    }

    private void updateItems() {
        if(!somethingChanged && pages.size() > currentPage){
            inventory.setContents(pages.get(currentPage));
            return;
        }
        somethingChanged = false;
        var cfg = plugin.getConfigLoader();
        var db = plugin.getSqLiteManager();
        if (recipes == null) {
            cfg.loadConfig();
            recipes = cfg.recipes;
            if (recipes == null) return;
        }

        ItemStack[] pageItems;

        final int pageSize = size - 18;
        final int baseIndex = currentPage * pageSize;
        final int recipesCount = recipes.size();

        for (int i = 0; i < pageSize; i++) {
            int idx = baseIndex + i;
            if (idx >= recipesCount) {
                inventory.setItem(i, null);
                continue;
            }

            String recipeName = recipes.get(idx);

            String recipeType = cfg.getType(recipeName);
            String limitType = cfg.getLimitType(recipeName);
            int limitNum = cfg.getLimit(recipeName);

            ItemStack itemOG = cfg.getResultItem(recipeName);
            if (itemOG == null || itemOG.getType().isAir()) {
                cfg.removeRecipe(recipeName);
                inventory.setItem(i, null);
                continue;
            }

            int limitAmnt = limitType.equalsIgnoreCase("SERVER") ? 0 : db.getServerCrafts(recipeName);

            ItemStack item = itemOG.clone();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                inventory.setItem(i, item);
                continue;
            }

            List<String> dynamicLore = new ArrayList<>(List.of(
                    "",
                    color("&r&8ID: " + recipeName),
                    color("&r&8Type: " + recipeType),
                    color("&r&8Limit Type: " + limitType),
                    color("&r&8Limit amount: " + (limitType.equalsIgnoreCase("SERVER") && limitNum > -1 ? limitAmnt + "/" + limitNum : limitNum))
            ));
            dynamicLore.addAll(constantItemLore);

            meta.setLore(dynamicLore);
            item.setItemMeta(meta);

            ItemStack current = inventory.getItem(i);
            if (current == null || !current.isSimilar(item)) {
                inventory.setItem(i, item);
            }
        }
        pageItems = inventory.getContents();
        try {
            pages.set(currentPage, pageItems);
        } catch (IndexOutOfBoundsException ignored){
            pages.add(currentPage, pageItems);
        }

    }

    private void updateNav(){
        if (currentPage != 0) inventory.setItem(size - 6, leftNavItem);

        ItemMeta pageNavMeta = pageNavItem.getItemMeta();
        assert pageNavMeta != null;
        pageNavMeta.setItemName(color("&lPage: " + (currentPage + 1)));
        pageNavMeta.setLore(List.of(color("&r&6Click to reload")));
        pageNavItem.setItemMeta(pageNavMeta);
        inventory.setItem(size - 5, pageNavItem);

        inventory.setItem(size - 1, newNavItem);

        try {
            int recipe = (currentPage +1)*(size - 19);
            //noinspection ResultOfMethodCallIgnored
            recipes.get(recipe);
            inventory.setItem(size - 4, rightNavItem);
        } catch (IndexOutOfBoundsException ignored) {
            inventory.setItem(size - 4, unused_space);
        }
    }

    public void sendToPlayer(Player player) {
        player.openInventory(inventory);
        updateItems();
        updateNav();
    }

    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != inventory) return;
        if (event.getCurrentItem() != null) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            if (event.getCurrentItem().isSimilar(rightNavItem)) {
                currentPage++;
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, 1f, 1f);
                sendToPlayer(player);
            } else if (event.getCurrentItem().isSimilar(leftNavItem)) {
                currentPage--;
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_SNARE, 1f, 1f);
                sendToPlayer(player);
            } else if (event.getCurrentItem().isSimilar(pageNavItem)){
                somethingChanged = true;
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 0.7f);
                sendToPlayer(player);
            } else if (event.getCurrentItem().isSimilar(newNavItem)) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1.5f);
                plugin.getCreateRecipeGUI("new_recipe")
                        .sendToPlayer(player, false);
            } else {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 0.6f);
                for (int i = 0; i < size - 18; i++) {
                    try {
                        ItemStack item =
                                plugin.getConfigLoader()
                                        .getResultItem(recipes.get(currentPage * (size - 18) + i));
                        ItemStack clickedItem = event.getCurrentItem();
                        if (clickedItem.getItemMeta() == null || item == null) continue;
                        if (clickedItem.getType().equals(item.getType())
                                && clickedItem.getAmount() == item.getAmount()
                                && clickedItem
                                        .getItemMeta()
                                        .getDisplayName()
                                        .equals(
                                                Objects.requireNonNull(item.getItemMeta())
                                                        .getDisplayName())) {
                            String recipeName = recipes.get(currentPage * (size - 18) + i);
                            if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                                plugin.getConfigLoader().removeRecipe(recipeName);
                                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                                player.sendMessage(
                                        ChatColor.RED
                                                + "Removed recipe "
                                                + recipeName
                                                + " from config");
                                sendToPlayer(player);
                            } else if (event.getClick().equals(ClickType.RIGHT)) {
                                plugin.getCreateRecipeGUI(recipeName).sendToPlayer(player, false);
                            } else if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
                                AnvilGUI.Builder renameGUI =
                                        new AnvilGUI.Builder()
                                                .onClose(
                                                        stateSnapshot -> {
                                                            if (stateSnapshot
                                                                    .getText()
                                                                    .equals(recipeName))
                                                                stateSnapshot
                                                                        .getPlayer()
                                                                        .sendMessage(
                                                                                color(
                                                                                        "&cCancelled renaming"));
                                                            else
                                                                player.sendMessage(
                                                                        ChatColor.GREEN
                                                                                + "Renamed "
                                                                                + recipeName
                                                                                + " to "
                                                                                + stateSnapshot
                                                                                        .getText());
                                                        })
                                                .onClick(
                                                        (slot, stateSnapshot) -> {
                                                            if (slot != AnvilGUI.Slot.OUTPUT) {
                                                                return Collections.emptyList();
                                                            }
                                                            if (stateSnapshot
                                                                    .getText()
                                                                    .equals(recipeName))
                                                                return Collections.emptyList();

                                                            plugin.getConfigLoader()
                                                                    .renameRecipe(
                                                                            recipeName,
                                                                            stateSnapshot
                                                                                    .getText());
                                                            plugin.getSqLiteManager()
                                                                    .renameColumn(
                                                                            recipeName,
                                                                            stateSnapshot
                                                                                    .getText());
                                                            sendToPlayer(player);
                                                            return Collections.emptyList();
                                                        })
                                                .text(recipeName)
                                                .title("Enter the new name")
                                                .plugin(PintoRecipes.thisPlugin());
                                renameGUI.open(player);
                            } else {
                                plugin.getCreateRecipeGUI(recipeName)
                                        .sendToPlayer((Player) event.getWhoClicked(), true);
                            }
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
        }
    }

    public void deinit() {}

    private String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
