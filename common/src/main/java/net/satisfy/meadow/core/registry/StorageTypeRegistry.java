package net.satisfy.meadow.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.meadow.Meadow;

import java.util.Set;

public class StorageTypeRegistry {
    public static final ResourceLocation WHEEL_BARROW = Meadow.identifier("wheel_barrow");
    public static final ResourceLocation CHEESE_RACK = Meadow.identifier("cheese_rack");
    public static final ResourceLocation FLOWER_POT_SMALL = Meadow.identifier("flower_pot_small");
    public static final ResourceLocation FLOWER_POT_BIG = Meadow.identifier("flower_pot_big");
    public static final ResourceLocation FLOWER_BOX = Meadow.identifier("flower_box");

    public static Set<Block> registerBlocks(Set<Block> blocks) {
        blocks.add(ObjectRegistry.WHEELBARROW.get());
        blocks.add(ObjectRegistry.WOODEN_FLOWER_POT_SMALL.get());
        blocks.add(ObjectRegistry.WOODEN_FLOWER_POT_BIG.get());
        blocks.add(ObjectRegistry.WOODEN_FLOWER_BOX.get());
        blocks.add(ObjectRegistry.CHEESE_RACK.get());
        blocks.add(ObjectRegistry.SHELF.get());
        return blocks;
    }
}
