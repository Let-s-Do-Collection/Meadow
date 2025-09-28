package net.satisfy.meadow.neoforge.core.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;
import net.satisfy.meadow.core.registry.TagRegistry;
import net.satisfy.meadow.neoforge.core.registry.MeadowBiomeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class AddAnimalsBiomeModifier implements BiomeModifier {
    private static final Set<EntityType<?>> registeredEntities = new HashSet<>();

    private static <T extends Mob> void registerEntity(EntityType<T> type, SpawnPlacements.SpawnPredicate<T> predicate) {
        if (registeredEntities.add(type)) {
            SpawnPlacements.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate);
        }
    }

    public static void registerEntities() {
        registerEntity(EntityTypeRegistry.WATER_BUFFALO.get(), Animal::checkAnimalSpawnRules);
        registerEntity(EntityTypeRegistry.WOOLY_COW.get(), Animal::checkAnimalSpawnRules);
    }

    @Override
    public void modify(@NotNull Holder<Biome> biome, @NotNull Phase phase, ModifiableBiomeInfo.BiomeInfo.@NotNull Builder builder) {
        if (phase == Phase.ADD) {
            registerEntities();
            addMobSpawn(builder, biome, TagRegistry.IS_MEADOW, EntityTypeRegistry.WOOLY_COW.get(), 14, 3, 6);
            addMobSpawn(builder, biome, TagRegistry.SPAWNS_UMBRA_COW, EntityTypeRegistry.WOOLY_COW.get(), 10, 3, 5);
            addMobSpawn(builder, biome, TagRegistry.SPAWNS_WARPED_COW, EntityTypeRegistry.WOOLY_COW.get(), 14, 4, 6);
            addMobSpawn(builder, biome, TagRegistry.SPAWNS_WATER_BUFFALO, EntityTypeRegistry.WATER_BUFFALO.get(), 8, 2, 3);
        }
    }

    private void addMobSpawn(ModifiableBiomeInfo.BiomeInfo.Builder builder, Holder<Biome> biome, TagKey<Biome> tag, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        if (biome.is(tag)) {
            builder.getMobSpawnSettings().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(entityType, weight, minGroupSize, maxGroupSize));
        }
    }

    @Override
    public @NotNull MapCodec<? extends BiomeModifier> codec() {
        return MeadowBiomeModifiers.ADD_ANIMALS_CODEC.get();
    }
}
