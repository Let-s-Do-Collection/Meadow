package net.satisfy.meadow.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.satisfy.meadow.Meadow;
import net.satisfy.meadow.entity.ShearableVarCow;
import net.satisfy.meadow.entity.WaterBuffalo;

import java.util.function.Supplier;

public class EntityRegistry {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Meadow.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<WaterBuffalo>> WATER_BUFFALO = create("water_buffalo",
            () -> EntityType.Builder.of(WaterBuffalo::new, MobCategory.CREATURE).sized(0.9f, 1.4f).build(Meadow.identifier("water_buffalo").toString())
    );

    public static final RegistrySupplier<EntityType<ShearableVarCow>> SHEARABLE_MEADOW_VAR_COW = create("wooly_cow",
            () -> EntityType.Builder.of(ShearableVarCow::new, MobCategory.CREATURE).sized(0.9f, 1.4f).build(Meadow.identifier("wooly_cow").toString())
    );

    public static void registerCow(Supplier<? extends EntityType<? extends Animal>> typeSupplier) {
        EntityAttributeRegistry.register(typeSupplier, Cow::createAttributes);
    }

    public static <T extends EntityType<?>> RegistrySupplier<T> create(final String path, final Supplier<T> type) {
        return ENTITY_TYPES.register(Meadow.identifier(path), type);
    }

    public static void init() {
        Meadow.LOGGER.debug("Registering Mod Entities for " + Meadow.MOD_ID);
        ENTITY_TYPES.register();
        registerCow(SHEARABLE_MEADOW_VAR_COW);
        registerCow(WATER_BUFFALO);
    }
}
