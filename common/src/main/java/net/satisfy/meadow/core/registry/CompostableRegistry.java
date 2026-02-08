package net.satisfy.meadow.core.registry;

import net.minecraft.world.level.block.ComposterBlock;

public class CompostableRegistry {
    public static void registerCompostable() {
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.CHEESE_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.GOAT_CHEESE_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.GRAIN_CHEESE_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.AMETHYST_CHEESE_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.SHEEP_CHEESE_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.WARPED_CHEESE_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.BUFFALO_CHEESE_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.CHEESECAKE_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.CHEESE_TART_SLICE.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.CHEESE_SANDWICH.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.CHEESE_ROLL.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.CHEESE_STICK.get(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.CHEESE_WHEEL.get().asItem(), 1f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.WARPED_CHEESE_WHEEL.get().asItem(), 1f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.AMETHYST_CHEESE_WHEEL.get().asItem(), 1f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.GOAT_CHEESE_WHEEL.get().asItem(), 1f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.SHEEP_CHEESE_WHEEL.get().asItem(), 1f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.GRAIN_CHEESE_WHEEL.get().asItem(), 1f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.BUFFALO_CHEESE_WHEEL.get().asItem(), 1f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.ENZIAN.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.DELPHINIUM.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.ALPINE_POPPY.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.SAXIFRAGE.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.ERIOPHORUM.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.SMALL_FIR.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.PINE_SAPLING.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.YELLOW_PINE_SAPLING.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.ALPINE_BIRCH_SAPLING.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.TALL_ERIOPHORUM.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.FIRE_LILY.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.PINE_LEAVES.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.YELLOW_PINE_LEAVES.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ObjectRegistry.ALPINE_BIRCH_LEAVES.get().asItem(), 0.3f);
    }
}