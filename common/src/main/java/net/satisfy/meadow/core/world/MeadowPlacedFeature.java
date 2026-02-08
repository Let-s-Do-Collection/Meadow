package net.satisfy.meadow.core.world;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.satisfy.meadow.Meadow;


public class MeadowPlacedFeature {

    public static final ResourceKey<PlacedFeature> ERIOPHORUM_FIELD_PATCH_PLACED = registerKey("eriophorum_field_patch_placed");
    public static final ResourceKey<PlacedFeature> FALLEN_PINE_TREE_CHECKED = registerKey("fallen_pine_tree_checked");
    public static final ResourceKey<PlacedFeature> FLOWER_FIELD_PATCH_PLACED = registerKey("flower_field_patch_placed");
    public static final ResourceKey<PlacedFeature> FOREST_TREES_PLACED = registerKey("forest_trees");
    public static final ResourceKey<PlacedFeature> MEADOW_TREES_PLACED = registerKey("meadow_trees");
    public static final ResourceKey<PlacedFeature> SMALL_FIR_PATCH_PLACED = registerKey("small_fir_patch_placed");

    public static final ResourceKey<PlacedFeature> ORE_LIMESTONE_KEY = registerKey("ores/ore_limestone");
    public static final ResourceKey<PlacedFeature> ORE_SALT_UPPER_KEY = registerKey("ores/ore_salt_upper");
    public static final ResourceKey<PlacedFeature> ORE_SALT_BURIED_KEY = registerKey("ores/ore_salt_buried");
    public static final ResourceKey<PlacedFeature> BOULDERS_KEY = registerKey("terrain/limestone_boulder_placed");
    public static final ResourceKey<PlacedFeature> LIMESTONE_SLAB_KEY = registerKey("terrain/limestone_slab_placed");
    public static final ResourceKey<PlacedFeature> REPLACE_STONE_WITH_LIMESTONE_KEY = registerKey("terrain/replace_stone_with_limestone");
    public static final ResourceKey<PlacedFeature> REPLACE_GRASS_WITH_COBBLED_LIMESTONE_KEY = registerKey("terrain/replace_grass_with_cobbled_limestone");
    public static final ResourceKey<PlacedFeature> REPLACE_GRASS_WITH_COARSE_DIRT_KEY = registerKey("terrain/replace_grass_with_coarse_dirt");
    public static final ResourceKey<PlacedFeature> REPLACE_GRASS_WITH_MOSSY_COBBLED_LIMESTONE_KEY = registerKey("terrain/replace_grass_with_mossy_cobbled_limestone");


    public static final ResourceKey<PlacedFeature> MC_FLOWERS = registerMCKey("flower_meadow");
    public static final ResourceKey<PlacedFeature> MC_TREES = registerMCKey("trees_meadow");

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Meadow.identifier(name));
    }

    public static ResourceKey<PlacedFeature> registerMCKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.withDefaultNamespace(name));
    }
}

