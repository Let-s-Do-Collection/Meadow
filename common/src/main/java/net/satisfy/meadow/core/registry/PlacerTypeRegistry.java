package net.satisfy.meadow.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.core.world.feature.configured.tree.foliage.AlpineBirchFoliagePlacer;
import net.satisfy.meadow.core.world.feature.configured.tree.foliage.PineFoliagePlacer;


public class PlacerTypeRegistry {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES = DeferredRegister.create(Meadow.MOD_ID, Registries.FOLIAGE_PLACER_TYPE);

    public static final RegistrySupplier<FoliagePlacerType<PineFoliagePlacer>> PINE_FOLIAGE_PLACER = FOLIAGE_PLACER_TYPES.register("pine_foliage_placer", () -> new FoliagePlacerType<>(PineFoliagePlacer.CODEC));
    public static final RegistrySupplier<FoliagePlacerType<AlpineBirchFoliagePlacer>> ALPINE_BIRCH_FOLIAGE_PLACER = FOLIAGE_PLACER_TYPES.register("alpine_birch_foliage_placer", () -> new FoliagePlacerType<>(AlpineBirchFoliagePlacer.CODEC));

    public static void init() {
        FOLIAGE_PLACER_TYPES.register();
    }
}