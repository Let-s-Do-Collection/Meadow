package net.satisfy.meadow.fabric.core.world;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.core.world.CommonSpawnUtil;
import net.satisfy.meadow.platform.PlatformHelper;

import java.util.function.Predicate;

public class FeatureModification {
    public static void init() {
        Predicate<BiomeSelectionContext> buffalo = (ctx -> ctx.hasTag(BiomeTags.IS_RIVER) || ctx.hasTag(BiomeTags.IS_SAVANNA));


        Predicate<BiomeSelectionContext> meadowVarCows = (ctx -> {
            if(BiomeSelectors.spawnsOneOf(EntityType.COW).test(ctx)) return false;
            return ctx.hasTag(TagRegistry.IS_MEADOW) || ctx.hasTag(TagRegistry.SPAWNS_HIGHLAND_COW);
        });
        BiomeModifications.addSpawn(meadowVarCows, MobCategory.CREATURE, EntityType.COW,
                CommonSpawnUtil.cowSpawnWeight, CommonSpawnUtil.cowPackSizeMin, CommonSpawnUtil.cowPackSizeMax);

        Predicate<BiomeSelectionContext> shearableVarCows = (ctx -> ctx.hasTag(TagRegistry.IS_MEADOW) || ctx.hasTag(TagRegistry.SPAWNS_UMBRA_COW));
        addSpawn(shearableVarCows, EntityTypeRegistry.WOOLY_COW.get(),
                PlatformHelper.getWoolyCowSpawnWeight(), PlatformHelper.getWoolyCowMinGroupSize(), PlatformHelper.getWoolyCowMaxGroupSize());

        addSpawn(ctx -> ctx.hasTag(TagRegistry.SPAWNS_WARPED_COW), EntityTypeRegistry.WOOLY_COW.get(),
                PlatformHelper.getWarpedCowSpawnWeight(), PlatformHelper.getWarpedCowMinGroupSize(), PlatformHelper.getWarpedCowMaxGroupSize());

        addSpawn(buffalo, EntityTypeRegistry.WATER_BUFFALO.get(),
                PlatformHelper.getWaterBuffaloSpawnWeight(), PlatformHelper.getWaterBuffaloMinGroupSize(), PlatformHelper.getWaterBuffaloMaxGroupSize());
    }

    private static void addSpawn(Predicate<BiomeSelectionContext> biomes, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        if (weight <= 0) {
            return;
        }
        BiomeModifications.addSpawn(biomes, MobCategory.CREATURE, entityType, weight, minGroupSize, Math.max(minGroupSize, maxGroupSize));
    }
}
