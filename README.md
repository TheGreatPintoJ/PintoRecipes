## PintoRecipes
A simple spigot plugin to add custom recipes to your server

### Features
* Save, show, and edit recipes from a GUI
* Remove blank recipes from config
* All recipes are shaped
* Any item is craftable (including nbt)

### Commands
* `/pintorecipes <show|save|edit|list> [recipe_name]`
  * `show <recipe_name>` - Opens a read-only GUI showing the recipe
  * `save <recipe_name>` - Opens a recipe creating GUI. Closing without any items in either grid or result will result in saving being cancelled.
  * `edit <recipe_name>` - Opens a writable GUI showing the recipe, saves on close with the same conditions as above (`save`)
  * `list` - Opens a GUI showing all available recipes

### Permissions
* `pintorecipes.recipes # Permission to use /pintorecipes (more coming soon)`

### Config
The `recipes.yml` file is where all cusom recipes are stored.
* The first key is whatever the name of the recipe is (defaults are endermite_spawnegg and netherite_sword)
* `result` - This is the raw data of the item that spigot stores. I highly recommend leaving this be and using the commands to generate it.
* `recipe` - This is where the recipe is stored. The first set of entries is the first row of the crafting grid, the second is the second row, etc. All entries beyond three will be ignored.

### Notes
* All changes need a server restart to take effect
  * I have not yet found a problem with reloading instead, but you might run into problems so it's better to restart
* Recipies might be shaped, but that does not mean they will be in that specific slot of the crafting grid. For example, the default `netherite_sword` recipe can be crafted anywhere on the grid, as long as the sword and ingot are in that configuration relative to each other.
 
### Errors/Bugs
Make an issue in this project's repository
