## PintoRecipes
A spigot plugin to add custom recipes to your server

### Features
* Save, show, edit, rename, remove recipes from a GUI
* Options for shaped, shapeless, furnace, blasting, smoking, campfire, and stonecutter recipes
* Any item is craftable/smeltable/etc. (including nbt)
* Option to limit how many of a custom item is crafted (including vanilla recipes if you create a 'custom' recipe for them)
  * Permission to bypass crafting limitations (`pintorecipes.craftbypass`)
* Permissions to limit who can craft any item (default to anybody)


### Commands
* `/pintorecipes [show|save|edit|remove|list] [recipe_name]`
    * `show <recipe_name>` - Opens a read-only GUI showing the recipe
    * `save <recipe_name>` - Opens a recipe creating GUI. Closing without any items in either grid or result will result in saving being cancelled.
    * `edit <recipe_name>` - Opens a writable GUI showing the recipe, saves on close with the same conditions as above (`save`)
    * `remove <recipe_name>` - Removes specified recipe from config
    * `list` - Opens a GUI showing all available recipes

### Permissions
* `pintorecipes.recipes # Permission to use '/pintorecipes'`
* `pintorecipes.recipes.show # Permission to use '/pr show'`
* `pintorecipes.recipes.save # Permission to use '/pr save'`
* `pintorecipes.recipes.edit # Permission to use '/pr edit'`
* `pintorecipes.recipes.remove # Permission to use '/pr remove'`
* `pintorecipes.recipes.list # Permission to use '/pr list' or '/pr' (without args)`
* `pintorecipes.craftbypass # Permission to bypass crafting permissions and limits`
* `pintorecipes.craft.<recipe_name> # Permission to craft specified recipe`

### Config
The `recipes.yml` file is where all custom recipes are stored.
* The first key is whatever the name of the recipe is (defaults are endermite_spawnegg, netherite_sword, etc.)
* `result` - This is the raw data of the item that spigot stores. I highly recommend leaving this be and using the commands to generate it.
* `recipe` - This is where the recipe is stored. The first set of entries is the first row of the crafting grid, the second is the second row, etc. All entries beyond three will be ignored.
* `enabled` - Whether to load the recipe when the server starts
* `type` - The type of the recipe
* `category` - The category of whatever book to put the recipe in
* `cooktime` - The time it will take to finish cooking in ticks (only available for suitable recipe types)
* `experience` - The amount of exp to give the player when item is taken out of container (only available for suitable recipe types)
* More information [here](https://docs.pintocraft.com/books/pintorecipes/page/config)

### Notes
* All changes need a server restart to take effect
    * I have not yet found a problem with reloading instead, but you might run into problems so it's better to restart

### Errors/Bugs
Make an issue in this project's GitHub repository