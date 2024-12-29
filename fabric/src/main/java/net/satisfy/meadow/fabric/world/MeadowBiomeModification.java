package net.satisfy.meadow.fabric.world;

import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.world.MeadowPlacedFeature;

import java.util.function.Predicate;



public class MeadowBiomeModification {

    public static void init() {
        BiomeModification world = BiomeModifications.create(Meadow.identifier("world_features"));
        Predicate<BiomeSelectionContext> meadowBiomes = getMeadowSelector();

        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.FOREST_TREES_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.FOREST_SMALL_FIR_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_ORE_LIMESTONE_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_ORE_SALT_UPPER_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_ORE_SALT_BURIED_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_TREES_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_GRASS_PATCH_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_PINE_FALLEN_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_FLOWERS_PATCH_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_BOULDERS_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_LIMESTONE_SLAB_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_MOSSY_LIMESTONE_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_REPLACE_STONE_WITH_LIMESTONE_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_REPLACE_GRASS_WITH_COBBLED_LIMESTONE_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_REPLACE_GRASS_WITH_COARSE_DIRT_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_REPLACE_GRASS_WITH_MOSSY_COBBLED_LIMESTONE_KEY));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getEffects().setGrassColor(9286496));
        world.add(ModificationPhase.ADDITIONS, meadowBiomes, ctx -> ctx.getEffects().setFoliageColor(5866311));
        world.add(ModificationPhase.REMOVALS, meadowBiomes, ctx -> ctx.getGenerationSettings().removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_MC_FLOWERS));
        world.add(ModificationPhase.REMOVALS, meadowBiomes, ctx -> ctx.getGenerationSettings().removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_MC_TREES));
    }

    private static Predicate<BiomeSelectionContext> getMeadowSelector() {
        return BiomeSelectors.tag(TagKey.create(Registries.BIOME, Meadow.identifier("meadow_biomes")));
    }
}