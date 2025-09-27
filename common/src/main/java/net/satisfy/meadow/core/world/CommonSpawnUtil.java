package net.satisfy.meadow.core.world;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.Set;

public class CommonSpawnUtil {
    public static final int cowSpawnWeight = 6;
    public static final int cowPackSizeMin = 2;
    public static final int cowPackSizeMax = 3;

    public static boolean spawnsInBiome(Holder<Biome> biome, boolean checkForMeadowSpawn, EntityType<?>... entityTypes) {
        return spawnsInBiome(biome, checkForMeadowSpawn, ImmutableSet.copyOf(entityTypes));
    }

    public static boolean spawnsInBiome(Holder<Biome> biome, boolean checkForMeadowSpawn, Set<EntityType<?>> entityTypes) {
        MobSpawnSettings spawnSettings = biome.value().getMobSettings();
        for (MobCategory spawnGroup : MobCategory.values()) {
            for (MobSpawnSettings.SpawnerData spawnEntry : spawnSettings.getMobs(spawnGroup).unwrap()) {
                if (checkForMeadowSpawn && isMeadowSpawn(spawnEntry)) return false;
                if (entityTypes.contains(spawnEntry.type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMeadowSpawn(MobSpawnSettings.SpawnerData spawnEntry) {
        EntityType<?> type = spawnEntry.type;

        if (type.equals(EntityType.COW)) {
            return spawnEntry.maxCount == cowPackSizeMax && spawnEntry.minCount == cowPackSizeMin;
        }

        return false;
    }
}
