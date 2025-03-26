package net.satisfy.meadow.core.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public class FlammableBlockRegistry {
    public static void init() {
        addFlammable(5, 20, ObjectRegistry.PINE_BARN_DOOR.get(), ObjectRegistry.PINE_BEAM.get(), ObjectRegistry.PINE_BARN_TRAPDOOR.get(), ObjectRegistry.PINE_DOOR.get(), ObjectRegistry.PINE_FENCE_GATE.get(), ObjectRegistry.PINE_BUTTON.get(), ObjectRegistry.PINE_LOG.get(), ObjectRegistry.PINE_PLANKS.get(), ObjectRegistry.PINE_FENCE.get(), ObjectRegistry.PINE_RAILING.get(), ObjectRegistry.PINE_PRESSURE_PLATE.get(), ObjectRegistry.PINE_SLAB.get(), ObjectRegistry.PINE_STAIRS.get(), ObjectRegistry.PINE_WOOD.get(), ObjectRegistry.PINE_TRAPDOOR.get(), ObjectRegistry.STRIPPED_PINE_LOG.get(), ObjectRegistry.STRIPPED_PINE_WOOD.get());

        addFlammable(30, 60, ObjectRegistry.PINE_LEAVES.get(), ObjectRegistry.PINE_LEAVES_2.get(), ObjectRegistry.SHUTTER_BLOCK.get(), ObjectRegistry.SHUTTER_BLOCK_BERRY.get(), ObjectRegistry.SHUTTER_BLOCK_FIR.get(), ObjectRegistry.SHUTTER_BLOCK_POPPY.get());

        addFlammable(10, 40, ObjectRegistry.WOODCUTTER.get(), ObjectRegistry.CHEESE_RACK.get(), ObjectRegistry.WOODEN_CAULDRON.get(), ObjectRegistry.WATERING_CAN.get(), ObjectRegistry.FRAME.get(), ObjectRegistry.FIRE_LOG.get(), ObjectRegistry.WOODEN_FLOWER_BOX.get(), ObjectRegistry.WOODEN_FLOWER_POT_SMALL.get(), ObjectRegistry.WOODEN_FLOWER_POT_BIG.get());

        addFlammable(5, 20, ObjectRegistry.ROCKY_BED.get(), ObjectRegistry.ROCKY_CARPET.get(), ObjectRegistry.ROCKY_WOOL.get(), ObjectRegistry.WARPED_BED.get(), ObjectRegistry.WARPED_CARPET.get(), ObjectRegistry.WARPED_WOOL.get(), ObjectRegistry.STRAW_BED.get(), ObjectRegistry.FLECKED_BED.get(), ObjectRegistry.FLECKED_CARPET.get(), ObjectRegistry.FLECKED_WOOL.get(), ObjectRegistry.HIGHLAND_BED.get(), ObjectRegistry.HIGHLAND_CARPET.get(), ObjectRegistry.HIGHLAND_WOOL.get(), ObjectRegistry.PATCHED_BED.get(), ObjectRegistry.PATCHED_CARPET.get(), ObjectRegistry.PATCHED_WOOL.get(), ObjectRegistry.UMBRA_BED.get(), ObjectRegistry.UMBRA_CARPET.get(), ObjectRegistry.UMBRA_WOOL.get(), ObjectRegistry.INKY_BED.get(), ObjectRegistry.INKY_CARPET.get(), ObjectRegistry.INKY_WOOL.get());
    }

    public static void addFlammable(int burnOdd, int igniteOdd, Block... blocks) {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        for (Block block : blocks) {
            fireBlock.setFlammable(block, burnOdd, igniteOdd);
        }
    }
}
