[1.4.10]

**Added**
* The Cookpot & Cooking Frame got a complete rework! No more GUI. Pour in your liquid, toss in up to 4 ingredients by hand (Shift-click to add the whole stack) and watch them cook. Got 64 ingredients? You'll get 64 results in one go, and the liquid is only used once. When foam rises to the surface, skim it off to speed things up by 15%. Leave it there and cooking slows down. Once everything is ready, grab the result with an empty hand or fill it into a bowl, bottle or bucket if the recipe calls for one. Hoppers still work too, so your automated kitchens aren't going anywhere!
* Cooking liquids are now data-driven. Datapacks and other mods can add their own liquids through `data/<namespace>/meadow/cooking_liquid/`. Recipes can also require a specific liquid and container, opening up plenty of possibilities for future recipes and addons.
* The Fondue Pot got a makeover! Drop in a Cheese Wheel to fill the pot, or add up to 4 individual Cheese pieces. Then grab an empty hand and start stirring! The cheese gradually melts away as the fondue rises, taking on the color of the cheese you used. Bubbles, steam, splashes and stirring sounds all match the cheese color, just like the Bakery Cooking Pot. Because even fondue deserves a little attention to detail.
* The Cheese Press is now fully physical, too! Pour in up to 4 buckets of the same milk, add a Rennet and hold right-click with an empty hand to crank the press down. Watch the plate sink, the whey drip out and the curd slowly turn into cheese over the next 2 minutes. Once it's ready, crank the press back up and collect one Cheese Wheel per bucket of milk.
* The Camera got a complete rework! Load it with up to 16 Paper and start taking pictures. Every shot produces a photo that pops right out of the lens, complete with that lovely old-school sepia look. Hang your favorite shots on the wall with a right-click, where they'll sit in their own Photo Frame. Break the frame whenever you want your photo back. A full-size copy is also saved to your screenshots folder, so your best moments aren't stuck in-game. Want to get in the picture yourself? Shift-right-click starts a self-timer. You can also trigger the camera with redstone!
* Say cheese! The Camera's flash now dazzles monsters in front of the lens, stopping them in their tracks, making them lose their target and causing them to glow briefly. Players who look directly into the lens get a short white flash, too. Range and duration can be adjusted in the config.
* The Woodcutter finally looks the part! The log you put in now sits on top of the block, and the axe swings down with every cut. :D
* New info panels! The Cooking Pot, Fondue Pot, Cheese Rack, Cheese Press, Milk Can and Camera now have their own info panels. Each one can be toggled individually in the config.
* The creative tab got a little tidier. Content is now organized into three side tabs: Cheese & Kitchen, Pine & Furniture, and Nature & Stone.
* Doormats are now customizable! Right-click with an empty hand to add two lines of text, with up to 12 characters per line. Want some color? Use dye to change the text color (yes, it makes a huge splash). A Glow Ink Sac makes the text glow, and a regular Ink Sac turns the glow off again.
* EMI support is here! Cooking Pot, Cheese Press and Woodcutter recipes are now available in EMI.
* Warped Cows can now spawn naturally! Keep an eye out for them in the Warped Forest. Spawn weight and group size are configurable, so you can adjust how often they appear.
* The Watering Can finally feels like a Watering Can! Using it now splashes water, and it'll actually look empty when there's no water left. Small detail, much better feel.
* More helpful tooltips! The Milk Can, Climbing Rope, Watering Can, Felting Needle, all placeable blocks and all cheeses now explain what they do. No more guessing!
* Item rarities are here! Cheeses, tools, Fur Armor, the Camera, spawn eggs and the Meadow Banner now display their rarity colors in their names.
* Not a fan of limestone cliffs? You can now disable them in the config under the World group. This only affects newly generated chunks, though, so existing cliffs won't disappear. :D 
* The config got a lot more options! You can now tweak cheese press time, fondue melt time, the cooking skim bonus, Milk Can chance and range, rain and snow chances for the Wooden Cauldron, Wooly Cow wool drops, spawn weights for all the cows and buffalos, Felting Needle time, Climbing Rope length, the Fur Armor set bonus, banner effects and plenty of Camera settings. Fabric and NeoForge now offer the same options, too!

**Changed**
* Fur Armor's full-set tooltip now uses the shared armor set display.
* Cooking Pot recipes have been updated for the new cooking system. All existing recipes are still there, so nothing got lost in the process!
* Meadow worldgen got a fresh coat of paint. Trees now grow in natural-looking groves with open meadows in between. Flowers bloom in colorful drifts, with grass and ferns filling the gaps. And where the terrain gets steep, you'll find limestone cliffs with calcite streaks and cobbled patches. The meadows should feel a lot more natural now.
* Sofas now seat you at the right height. No more awkward floating or sinking into the cushions!
* Updated the Italian translation (thanks to Serena!).

**Fixed**
* Fur Armor's full-set bonus now actually grants Knockback Resistance +2. It was supposed to do that all along!
* Pine Wall Cabinet hitbox now matches its model, so what you see is finally what you hit.

***

[1.4.9]

**Fixed**
* Alpine Birch Sapling not being pottable
* Potted Yellow Pine Sapling and Potted Alpine Birch Sapling not dropping anything when broken
* Cooking Pot now prefers the most specific matching recipe and no longer crafts anything while unused ingredients remain in the pot.
* Fixed a rare crash during mod initialization caused by an incorrect registry load order.
* Pine Railing Post missing particle texture.
* Wardrobe, Wheelbarrow/Flower Pot/Flower Box/Dresser and Cheese Rack now properly expose their inventory as a vanilla container, fixing incompatibilities with other mods.

***

[1.4.8]

**Fixed**
* Fur Armor now has proper durability scaling and can no longer be stacked.
* Fixed incorrect translation for Reclaimed Pine Trapdoor.
* Added missing Pine Dresser and all Pine Sofa variants to the correct wooden block tags.
* Wardrobe now correctly drops when the upper half is broken.
* Stored armor inside Wardrobes no longer disappears when the top part is destroyed.
* Cooking Frame now properly drops all stored items when broken.
* Fixed model and rendering alignment issues affecting Sofa variants and the Pine Cabinet.

**Changed**
* Updated ru_ru translation (thanks to tefnya).
* Removed duplicate wooden_cauldron entry from block tags.

***

[1.4.7]

**Fixed**
* Corrected stonecutting recipes where the count value was defined at the top level instead of inside the result object
* Fixed Alpine Birch dropping Birch Logs instead of Alpine Birch Logs
* Fixed tag typo: `c:egg` instead of `c:eggs` (thanks to awoolanche)

**Added** 
* zh_tw translation (thanks to cherrypuff)

***

[1.4.6]

**Fixed**
* Containers such as bottles, bowls and buckets not being returned after cooking
* `spawns_buffalo` tag referencing an invalid entry preventing buffalos from spawning

**Changed**
* Added crafting remainder to Rennet
* Updated pack.png

***

[1.4.5]

This update implements a wide range of community feedback. Several long-standing gaps have been addressed, features refined, and older content revisited to improve clarity, consistency, and overall quality.

**Fixed**
* Various minor issues and inconsistencies across blocks and models.
* Pine Signs (Hanging, Wall, etc.) rendering twice
* Items not being compostable on Neoforge
* CheeseForm not changing states 
* Rebuilt all Meadow Structures - this should finally resolve the "meadow:bench - unknown propert for..." error

**Changed**
* Many textures have been completely reworked. Some were oversaturated, simple vanilla recolors, or not clearly identifiable in-game. The goal was to stay as close as possible to the original feel while improving clarity and consistency.
* Several models have been refined, including the Fondue, Pine Chair, Pine Table and Milk Can.
* Hanging Birch Leaves now correctly generate hanging leaves as originally intended when first introduced.
* The Cooking Frame is now a proper 2-block-tall block. It always includes a Cooking Pot and is immediately ready for use after placement. You might have to replace existing Frames!
* Remade Pine & Alpine Birch tree shapes

**Added**
* New furniture pieces: Pine Sofa, Pine Wardrobe, Pine Dresser, Pine Wall Cabinet.
* Window Blocks for Pine Window Panes, Artisan Window Panes and Ornate Window Panes.
* Saplings for Yellow Pines and Alpine Birch Trees.
* Cooking particles for the Cooking Pot and Fondue while preparing food.
* CTM and BetterLeaves Texture Packs are now built-in for Neoforge and Fabric.

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