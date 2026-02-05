package net.satisfy.meadow.core.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public class FlammableBlockRegistry {
    public static void init() {
        addFlammable(5, 20,
                ObjectRegistry.PINE_BARN_DOOR.get(),
                ObjectRegistry.PINE_BEAM.get(),
                ObjectRegistry.PINE_BARN_TRAPDOOR.get(),
                ObjectRegistry.PINE_DOOR.get(),
                ObjectRegistry.PINE_FENCE_GATE.get(),
                ObjectRegistry.PINE_BUTTON.get(),
                ObjectRegistry.PINE_LOG.get(),
                ObjectRegistry.PINE_PLANKS.get(),
                ObjectRegistry.PINE_FENCE.get(),
                ObjectRegistry.PINE_RAILING.get(),
                ObjectRegistry.PINE_PRESSURE_PLATE.get(),
                ObjectRegistry.PINE_SLAB.get(),
                ObjectRegistry.PINE_STAIRS.get(),
                ObjectRegistry.PINE_WOOD.get(),
                ObjectRegistry.PINE_TRAPDOOR.get(),
                ObjectRegistry.STRIPPED_PINE_LOG.get(),
                ObjectRegistry.STRIPPED_PINE_WOOD.get()
        );

        addFlammable(30, 60,
                ObjectRegistry.PINE_LEAVES.get(),
                ObjectRegistry.YELLOW_PINE_LEAVES.get(),
                ObjectRegistry.SHUTTER_BLOCK.get(),
                ObjectRegistry.SHUTTER_BLOCK_BERRY.get(),
                ObjectRegistry.SHUTTER_BLOCK_FIR.get(),
                ObjectRegistry.SHUTTER_BLOCK_POPPY.get()
        );

        addFlammable(10, 40,
                ObjectRegistry.WOODCUTTER.get(),
                ObjectRegistry.PINE_CHEESE_RACK.get(),
                ObjectRegistry.PINE_CABINET.get(),
                ObjectRegistry.PINE_TABLE.get(),
                ObjectRegistry.PINE_CHAIR.get(),
                ObjectRegistry.WOODEN_CAULDRON.get(),
                ObjectRegistry.WATERING_CAN.get(),
                ObjectRegistry.FRAME.get(),
                ObjectRegistry.FIRE_LOG.get(),
                ObjectRegistry.WOODEN_FLOWER_BOX.get(),
                ObjectRegistry.WOODEN_FLOWER_POT_SMALL.get(),
                ObjectRegistry.WOODEN_FLOWER_POT_BIG.get()
        );

        addFlammable(5, 20,
                ObjectRegistry.PLAID_BED.get(),
                ObjectRegistry.PLAID_CARPET.get(),
                ObjectRegistry.PLAID_WOOL.get(),
                ObjectRegistry.WARPED_BED.get(),
                ObjectRegistry.WARPED_CARPET.get(),
                ObjectRegistry.WARPED_WOOL.get(),
                ObjectRegistry.STRAW_BED.get(),
                ObjectRegistry.RUSTIC_BED.get(),
                ObjectRegistry.RUSTIC_CARPET.get(),
                ObjectRegistry.RUSTIC_WOOL.get(),
                ObjectRegistry.LINEN_BED.get(),
                ObjectRegistry.LINEN_CARPET.get(),
                ObjectRegistry.LINEN.get(),
                ObjectRegistry.JACQUARD_BED.get(),
                ObjectRegistry.JACQUARD_CARPET.get(),
                ObjectRegistry.JACQUARD_WOOL.get(),
                ObjectRegistry.CHAMBRAY_BED.get(),
                ObjectRegistry.CHAMBRAY_CARPET.get(),
                ObjectRegistry.CHAMBRAY_WOOL.get(),
                ObjectRegistry.TWEED_BED.get(),
                ObjectRegistry.TWEED_CARPET.get(),
                ObjectRegistry.TWEED_WOOL.get()
        );
    }

    public static void addFlammable(int burnOdd, int igniteOdd, Block... blocks) {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        for (Block block : blocks) {
            if ("meadow".equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace())) {
                fireBlock.setFlammable(block, burnOdd, igniteOdd);
            }
        }
    }
}
