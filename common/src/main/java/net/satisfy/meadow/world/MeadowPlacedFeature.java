package net.satisfy.meadow.world;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.satisfy.meadow.Meadow;


public class MeadowPlacedFeature {

    public static final ResourceKey<PlacedFeature> FOREST_TREES_KEY = registerKey("forest_trees");
    public static final ResourceKey<PlacedFeature> FOREST_SMALL_FIR_KEY = registerKey("forest_small_fir");
    public static final ResourceKey<PlacedFeature> MEADOW_ORE_LIMESTONE_KEY = registerKey("ores/ore_limestone");
    public static final ResourceKey<PlacedFeature> MEADOW_ORE_SALT_UPPER_KEY = registerKey("ores/ore_salt_upper");
    public static final ResourceKey<PlacedFeature> MEADOW_ORE_SALT_BURIED_KEY = registerKey("ores/ore_salt_buried");
    public static final ResourceKey<PlacedFeature> MEADOW_BOULDERS_KEY = registerKey("terrain/meadow_boulders");
    public static final ResourceKey<PlacedFeature> MEADOW_LIMESTONE_SLAB_KEY = registerKey("terrain/meadow_limestone_slab");
    public static final ResourceKey<PlacedFeature> MEADOW_MOSSY_LIMESTONE_KEY = registerKey("terrain/meadow_mossy_limestone");
    public static final ResourceKey<PlacedFeature> MEADOW_REPLACE_STONE_WITH_LIMESTONE_KEY = registerKey("terrain/meadow_replace_stone_with_limestone");
    public static final ResourceKey<PlacedFeature> MEADOW_REPLACE_GRASS_WITH_COBBLED_LIMESTONE_KEY = registerKey("terrain/meadow_replace_grass_with_cobbled_limestone");
    public static final ResourceKey<PlacedFeature> MEADOW_REPLACE_GRASS_WITH_COARSE_DIRT_KEY = registerKey("terrain/meadow_replace_grass_with_coarse_dirt");
    public static final ResourceKey<PlacedFeature> MEADOW_REPLACE_GRASS_WITH_MOSSY_COBBLED_LIMESTONE_KEY = registerKey("terrain/meadow_replace_grass_with_mossy_cobbled_limestone");

    public static final ResourceKey<PlacedFeature> MEADOW_GRASS_PATCH_KEY = registerKey("meadow_grass_patch");
    public static final ResourceKey<PlacedFeature> MEADOW_PINE_FALLEN_KEY = registerKey("meadow_pine_fallen");
    public static final ResourceKey<PlacedFeature> MEADOW_FLOWERS_PATCH_KEY = registerKey("meadow_flowers");
    public static final ResourceKey<PlacedFeature> MEADOW_TREES_KEY = registerKey("meadow_trees");
    public static final ResourceKey<PlacedFeature> MEADOW_MC_FLOWERS = registerMCKey("flower_meadow");
    public static final ResourceKey<PlacedFeature> MEADOW_MC_TREES = registerMCKey("trees_meadow");

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Meadow.identifier(name));
    }

    public static ResourceKey<PlacedFeature> registerMCKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation("minecraft", name));
    }
}

