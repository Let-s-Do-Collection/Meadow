package net.satisfy.meadow.neoforge.core.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.neoforge.core.registry.MeadowBiomeModifiers;
import net.satisfy.meadow.platform.neoforge.PlatformHelperImpl;
import org.jetbrains.annotations.NotNull;

public class AddAnimalsBiomeModifier implements BiomeModifier {
    @Override
    public void modify(@NotNull Holder<Biome> biome, @NotNull Phase phase, ModifiableBiomeInfo.BiomeInfo.@NotNull Builder builder) {
        if (phase == Phase.ADD) {
            int woolyCowWeight = PlatformHelperImpl.getWoolyCowSpawnWeight();
            int woolyCowMin = PlatformHelperImpl.getWoolyCowMinGroupSize();
            int woolyCowMax = PlatformHelperImpl.getWoolyCowMaxGroupSize();
            addMobSpawn(builder, biome, TagRegistry.IS_MEADOW, EntityTypeRegistry.WOOLY_COW.get(), woolyCowWeight, woolyCowMin, woolyCowMax);
            addMobSpawn(builder, biome, TagRegistry.SPAWNS_UMBRA_COW, EntityTypeRegistry.WOOLY_COW.get(), woolyCowWeight, woolyCowMin, woolyCowMax);
            addMobSpawn(builder, biome, TagRegistry.SPAWNS_WARPED_COW, EntityTypeRegistry.WOOLY_COW.get(), PlatformHelperImpl.getWarpedCowSpawnWeight(), PlatformHelperImpl.getWarpedCowMinGroupSize(), PlatformHelperImpl.getWarpedCowMaxGroupSize());
            addMobSpawn(builder, biome, TagRegistry.SPAWNS_WATER_BUFFALO, EntityTypeRegistry.WATER_BUFFALO.get(), PlatformHelperImpl.getWaterBuffaloSpawnWeight(), PlatformHelperImpl.getWaterBuffaloMinGroupSize(), PlatformHelperImpl.getWaterBuffaloMaxGroupSize());
        }
    }

    private void addMobSpawn(ModifiableBiomeInfo.BiomeInfo.Builder builder, Holder<Biome> biome, TagKey<Biome> tag, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        if (weight > 0 && biome.is(tag)) {
            builder.getMobSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(entityType, weight, minGroupSize, Math.max(minGroupSize, maxGroupSize)));
        }
    }

    @Override
    public @NotNull MapCodec<? extends BiomeModifier> codec() {
        return MeadowBiomeModifiers.ADD_ANIMALS_CODEC.get();
    }
}
