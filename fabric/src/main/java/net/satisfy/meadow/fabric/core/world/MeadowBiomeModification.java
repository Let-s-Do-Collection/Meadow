package net.satisfy.meadow.fabric.core.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.world.MeadowPlacedFeature;

import java.util.function.Predicate;

public class MeadowBiomeModification {

    public static void init() {
        BiomeModifications.create(Meadow.identifier("world_features"))
                .add(ModificationPhase.ADDITIONS, getMeadowSelector(), ctx -> {
                    var gen = ctx.getGenerationSettings();
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.ERIOPHORUM_FIELD_PATCH_PLACED);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.FALLEN_PINE_TREE_CHECKED);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.FLOWER_FIELD_PATCH_PLACED);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.FOREST_TREES_PLACED);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MEADOW_TREES_PLACED);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.SMALL_FIR_PATCH_PLACED);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.ORE_LIMESTONE_KEY);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.ORE_SALT_UPPER_KEY);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.ORE_SALT_BURIED_KEY);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.BOULDERS_KEY);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.LIMESTONE_SLAB_KEY);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.REPLACE_STONE_WITH_LIMESTONE_KEY);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.REPLACE_GRASS_WITH_COBBLED_LIMESTONE_KEY);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.REPLACE_GRASS_WITH_COARSE_DIRT_KEY);
                    gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.REPLACE_GRASS_WITH_MOSSY_COBBLED_LIMESTONE_KEY);
                })
                .add(ModificationPhase.ADDITIONS, getMeadowSelector(), ctx -> {
                    var effects = ctx.getEffects();
                    effects.setGrassColor(9286496);
                    effects.setFoliageColor(5866311);
                })
                .add(ModificationPhase.REMOVALS, getMeadowSelector(), ctx -> {
                    var gen = ctx.getGenerationSettings();
                    gen.removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MC_FLOWERS);
                    gen.removeFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MeadowPlacedFeature.MC_TREES);
                });

        FabricEntitySpawn.registerEntitySpawn();
    }

    private static Predicate<BiomeSelectionContext> getMeadowSelector() {
        return BiomeSelectors.tag(TagKey.create(Registries.BIOME, Meadow.identifier("is_meadow")));
    }

    public static class FabricEntitySpawn {
        public static void registerEntitySpawn() {
            SpawnPlacements.register(EntityTypeRegistry.WATER_BUFFALO.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);
            SpawnPlacements.register(EntityTypeRegistry.WOOLY_COW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        }
    }
}
