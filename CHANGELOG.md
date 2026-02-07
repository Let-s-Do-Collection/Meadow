[1.4.5]

This update brings a wide range of community wishes and improvements, directly implementing your feedback. Gaps have been closed, features have been made more accessible and refined, and familiar content has been carefully revisited and improved.

**Fixed**
* Alpine Salt loot table
* Hanging Leaves not rendering properly when used with FastLeaves

**Changed**
* Many textures have been completely reworked. Some were oversaturated, simple vanilla recolors, or not clearly identifiable in game. The goal was to stay as close as possible to the original feel while improving clarity and consistency.
* The same applies to a few blocks such as the Fondue, Pine Chair, Pine Table and Milk Can models.
* Hanging Birch Leaves now generate hanging leaves as originally intended when they were first introduced three years ago.

**Added**
* A whole variety of Furniture: Pine Sofa, Pine Wardrobe, Pine Dresser, Pine Wall Cabinet
* Window Blocks for Pine Window Panes, Artisan Window Panes, Ornate Window Panes
* Saplings for Yellow Pines and Alpine Birch Trees
* Particles for Cooking Pot & Fondue that show while you're cooking

***

[1.4.4]

**Fixed**
* Alpine Salt loottable
* Hanging Leaves not rendering properly when used with FastLeaves
* Variant ID not being applied correctly on Cattle Spawn Egg

**Changed**
* Biome Grass and Foliage Modifier on NeoForge
* FlowerPatch distribution

***

[1.4.3]

**Fixed**
* Several loot table predicates fixed
* "Find Meadow" advancement no longer triggers immediately after entering a new world

**Changed**
* Decreased "Find Meadow" advancement experience reward from `50` to `10`

***

[1.4.2]

**Fixed**
* Windows now render and connect correctly
* Structures no longer spawn without windows or with outdated block properties

**Changed**
* Slightly adjusted the Meadow Completionist Banner Texture for the Block & Item
* Reshaded Warped Cheese Block & Warped Cheese Item
* Reworked Tiled Stove blocks with new connection, lit-state, and top-design properties

***

[1.4.1]

**Fixed**
* Crash up during initialization
* Crash during initialization when Meadow and Candlelight were loaded together (flammability registration is now thread-safe)
* Worldgen features not generating correctly on Fabric
* Wooden Cauldron now properly transforms back when removing Water or Powder Snow

***

[1.4.0]

**Ported to 1.21.1**

***

[1.3.26]

**Added**
* Japanese translation _(Thanks to PExPE3)_

**Changed**

**Fixed**
* Cooking Pot recipes now work correctly when the result item is already present in the output slot. 
* Removed the unnecessary subclass of `ResourceLocation`, which caused compatibility issues with some mods (e.g., Xaero's Minimap not displaying sheep faces). 
* `yellow_pine_leaves` now correctly belongs to the `#minecraft:leaves` tag
* Removed unintended `"shade": false` properties from `pine_leaves_1.json` and `yellow_pine_leaves.json`
* Pine leaves and hanging birch leaves are now correctly broken by pistons instead of being pushed
* Wooly Cows & Sheep now correctly dropping meat

***

[1.3.25]

***

[1.3.24] 

**Added**

**Changed**

**Fixed**
- Cooking `roasted_buffalo_ham` or buffalo_sausage_with_cheese` now correctly results in the intended items instead of always producing `cooked_buffalo_meat`.
- The JEI Woodcutter background GUI now displays the correct texture.

***

[1.3.24]

**Added**

**Changed**

**Fixed**
- `CompletionistBanner` giving `Regeneration` instead of `DigSpeed`.