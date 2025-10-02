package net.satisfy.meadow.neoforge.core.registry;

import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.satisfy.meadow.core.registry.EntityTypeRegistry;

import java.util.function.Supplier;

public final class MeadowSpawns {
    public static void init() {
        registerAnimal(EntityTypeRegistry.WOOLY_COW, Animal::checkAnimalSpawnRules);
        registerAnimal(EntityTypeRegistry.WATER_BUFFALO, Animal::checkAnimalSpawnRules);
    }

    private static <T extends Animal> void registerAnimal(Supplier<? extends EntityType<T>> type, SpawnPlacements.SpawnPredicate<T> predicate) {
        SpawnPlacementsRegistry.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, predicate);
    }
}
