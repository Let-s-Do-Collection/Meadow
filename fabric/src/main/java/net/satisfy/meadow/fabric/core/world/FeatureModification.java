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

import java.util.function.Predicate;

public class FeatureModification {
    public static void init() {
        int meadowSpawnWeight = 3;
        int meadowPackSizeMin = 2;
        int meadowPackSizeMax = 3;

        Predicate<BiomeSelectionContext> buffalo = (ctx -> ctx.hasTag(BiomeTags.IS_RIVER) || ctx.hasTag(BiomeTags.IS_SAVANNA));


        Predicate<BiomeSelectionContext> meadowVarCows = (ctx -> {
            if(BiomeSelectors.spawnsOneOf(EntityType.COW).test(ctx)) return false;
            return ctx.hasTag(TagRegistry.IS_MEADOW) || ctx.hasTag(TagRegistry.SPAWNS_HIGHLAND_COW);
        });
        BiomeModifications.addSpawn(meadowVarCows, MobCategory.CREATURE, EntityType.COW,
                CommonSpawnUtil.cowSpawnWeight, CommonSpawnUtil.cowPackSizeMin, CommonSpawnUtil.cowPackSizeMax);

        Predicate<BiomeSelectionContext> shearableVarCows = (ctx -> ctx.hasTag(TagRegistry.IS_MEADOW) || ctx.hasTag(TagRegistry.SPAWNS_UMBRA_COW));
        BiomeModifications.addSpawn(shearableVarCows, MobCategory.CREATURE, EntityTypeRegistry.WOOLY_COW.get(),
                10, meadowPackSizeMin, meadowPackSizeMax);

        BiomeModifications.addSpawn(buffalo, MobCategory.CREATURE, EntityTypeRegistry.WATER_BUFFALO.get(),
                meadowSpawnWeight, meadowPackSizeMin, meadowPackSizeMax);
    }
}
