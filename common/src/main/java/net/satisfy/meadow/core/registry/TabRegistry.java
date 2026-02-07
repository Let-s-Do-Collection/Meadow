package net.satisfy.meadow.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.satisfy.meadow.Meadow;

public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Meadow.MOD_ID, Registries.CREATIVE_MODE_TAB);

    @SuppressWarnings("unused")
    public static final RegistrySupplier<CreativeModeTab> MEADOW_TAB = CREATIVE_MODE_TABS.register("meadow", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(ObjectRegistry.PIECE_OF_CHEESE.get()))
            .title(Component.translatable("creativetab.meadow.tab"))
            .displayItems((parameters, output) -> {
                output.accept(ObjectRegistry.ALPINE_COAL_ORE.get());
                output.accept(ObjectRegistry.ALPINE_IRON_ORE.get());
                output.accept(ObjectRegistry.ALPINE_COPPER_ORE.get());
                output.accept(ObjectRegistry.ALPINE_GOLD_ORE.get());
                output.accept(ObjectRegistry.ALPINE_REDSTONE_ORE.get());
                output.accept(ObjectRegistry.ALPINE_LAPIS_ORE.get());
                output.accept(ObjectRegistry.ALPINE_EMERALD_ORE.get());
                output.accept(ObjectRegistry.ALPINE_DIAMOND_ORE.get());
                output.accept(ObjectRegistry.ALPINE_SALT_ORE.get());

                output.accept(ObjectRegistry.LIMESTONE.get());
                output.accept(ObjectRegistry.COBBLED_LIMESTONE.get());
                output.accept(ObjectRegistry.MOSSY_COBBLED_LIMESTONE.get());
                output.accept(ObjectRegistry.LIMESTONE_BRICKS.get());
                output.accept(ObjectRegistry.MOSSY_LIMESTONE_BRICKS.get());
                output.accept(ObjectRegistry.CRACKED_LIMESTONE_BRICKS.get());
                output.accept(ObjectRegistry.CHISELED_LIMESTONE.get());
                output.accept(ObjectRegistry.POLISHED_LIMESTONE.get());
                output.accept(ObjectRegistry.LIMESTONE_STAIRS.get());
                output.accept(ObjectRegistry.LIMESTONE_SLAB.get());
                output.accept(ObjectRegistry.COBBLED_LIMESTONE_STAIRS.get());
                output.accept(ObjectRegistry.COBBLED_LIMESTONE_SLAB.get());
                output.accept(ObjectRegistry.LIMESTONE_BRICK_STAIRS.get());
                output.accept(ObjectRegistry.LIMESTONE_BRICK_SLAB.get());
                output.accept(ObjectRegistry.MOSSY_COBBLED_LIMESTONE_STAIRS.get());
                output.accept(ObjectRegistry.MOSSY_COBBLED_LIMESTONE_SLAB.get());
                output.accept(ObjectRegistry.MOSSY_LIMESTONE_BRICK_STAIRS.get());
                output.accept(ObjectRegistry.MOSSY_LIMESTONE_BRICK_SLAB.get());
                output.accept(ObjectRegistry.LIMESTONE_WALL.get());
                output.accept(ObjectRegistry.COBBLED_LIMESTONE_WALL.get());
                output.accept(ObjectRegistry.LIMESTONE_BRICK_WALL.get());
                output.accept(ObjectRegistry.MOSSY_COBBLED_LIMESTONE_WALL.get());
                output.accept(ObjectRegistry.MOSSY_LIMESTONE_BRICK_WALL.get());

                output.accept(ObjectRegistry.PINE_LOG.get());
                output.accept(ObjectRegistry.STRIPPED_PINE_LOG.get());
                output.accept(ObjectRegistry.PINE_WOOD.get());
                output.accept(ObjectRegistry.STRIPPED_PINE_WOOD.get());
                output.accept(ObjectRegistry.PINE_PLANKS.get());
                output.accept(ObjectRegistry.PINE_STAIRS.get());
                output.accept(ObjectRegistry.PINE_SLAB.get());
                output.accept(ObjectRegistry.PINE_FENCE.get());
                output.accept(ObjectRegistry.PINE_FENCE_GATE.get());
                output.accept(ObjectRegistry.PINE_DOOR.get());
                output.accept(ObjectRegistry.PINE_TRAPDOOR.get());
                output.accept(ObjectRegistry.PINE_BARN_DOOR.get());
                output.accept(ObjectRegistry.PINE_BARN_TRAPDOOR.get());
                output.accept(ObjectRegistry.PINE_PRESSURE_PLATE.get());
                output.accept(ObjectRegistry.PINE_BUTTON.get());
                output.accept(ObjectRegistry.PINE_SIGN_ITEM.get());
                output.accept(ObjectRegistry.PINE_HANGING_SIGN_ITEM.get());
                output.accept(ObjectRegistry.PINE_BOAT.get());
                output.accept(ObjectRegistry.PINE_CHEST_BOAT.get());
                output.accept(ObjectRegistry.PINE_BEAM.get());
                output.accept(ObjectRegistry.PINE_RAILING.get());
                output.accept(ObjectRegistry.PINE_WINDOW.get());
                output.accept(ObjectRegistry.PINE_WINDOW_PANE.get());
                output.accept(ObjectRegistry.PINE_SAPLING.get());
                output.accept(ObjectRegistry.PINE_LEAVES.get());
                output.accept(ObjectRegistry.YELLOW_PINE_SAPLING.get());
                output.accept(ObjectRegistry.YELLOW_PINE_LEAVES.get());
                output.accept(ObjectRegistry.ALPINE_BIRCH_SAPLING.get());
                output.accept(ObjectRegistry.ALPINE_BIRCH_LOG.get());
                output.accept(ObjectRegistry.ALPINE_BIRCH_LEAVES.get());

                output.accept(ObjectRegistry.TILED_STOVE.get());
                output.accept(ObjectRegistry.TILED_STOVE_FIREPLACE.get());
                output.accept(ObjectRegistry.TILED_STOVE_SMOKER.get());
                output.accept(ObjectRegistry.TILED_STOVE_BENCH.get());
                output.accept(ObjectRegistry.WOODCUTTER.get());
                output.accept(ObjectRegistry.WOODEN_CAULDRON.get());
                output.accept(ObjectRegistry.COOKING_CAULDRON.get());
                output.accept(ObjectRegistry.PINE_CHEESE_RACK.get());
                output.accept(ObjectRegistry.PINE_SOFA.get());
                output.accept(ObjectRegistry.PINE_DRESSER.get());
                output.accept(ObjectRegistry.PINE_CABINET.get());
                output.accept(ObjectRegistry.PINE_WALL_CABINET.get());
                output.accept(ObjectRegistry.PINE_WARDROBE.get());
                output.accept(ObjectRegistry.PINE_CHAIR.get());
                output.accept(ObjectRegistry.PINE_TABLE.get());
                output.accept(ObjectRegistry.PINE_BENCH.get());
                output.accept(ObjectRegistry.STONE_TABLE.get());
                output.accept(ObjectRegistry.STONE_BENCH.get());
                output.accept(ObjectRegistry.SHUTTER_BLOCK.get());
                output.accept(ObjectRegistry.ARTISAN_GLASS_WINDOW.get());
                output.accept(ObjectRegistry.ARTISAN_GLASS_WINDOW_PANE.get());
                output.accept(ObjectRegistry.ORNATE_GLASS_WINDOW.get());
                output.accept(ObjectRegistry.ORNATE_GLASS_WINDOW_PANE.get());
                output.accept(ObjectRegistry.FRAME.get());
                output.accept(ObjectRegistry.WHEELBARROW.get());
                output.accept(ObjectRegistry.DOORMAT.get());
                output.accept(ObjectRegistry.WOODEN_FLOWER_POT_BIG.get());
                output.accept(ObjectRegistry.WOODEN_FLOWER_POT_SMALL.get());
                output.accept(ObjectRegistry.WOODEN_FLOWER_BOX.get());
                output.accept(ObjectRegistry.OIL_LANTERN.get());
                output.accept(ObjectRegistry.FONDUE_POT.get());
                output.accept(ObjectRegistry.CAMERA.get());
                output.accept(ObjectRegistry.CLIMBING_ROPE_TOPMOUNT.get());
                output.accept(ObjectRegistry.FIREWOOD.get());
                output.accept(ObjectRegistry.MILK_CAN.get());
                output.accept(ObjectRegistry.FELTING_NEEDLE.get());
                output.accept(ObjectRegistry.WATERING_CAN_ITEM.get());
                output.accept(ObjectRegistry.CHEESE_FORM.get());

                output.accept(ObjectRegistry.ALPINE_POPPY.get());
                output.accept(ObjectRegistry.DELPHINIUM.get());
                output.accept(ObjectRegistry.SAXIFRAGE.get());
                output.accept(ObjectRegistry.ENZIAN.get());
                output.accept(ObjectRegistry.FIRE_LILY.get());
                output.accept(ObjectRegistry.ERIOPHORUM.get());
                output.accept(ObjectRegistry.TALL_ERIOPHORUM.get());
                output.accept(ObjectRegistry.SMALL_FIR.get());

                output.accept(ObjectRegistry.RUSTIC_WOOL.get());
                output.accept(ObjectRegistry.RUSTIC_CARPET.get());
                output.accept(ObjectRegistry.RUSTIC_BED.get());
                output.accept(ObjectRegistry.LINEN.get());
                output.accept(ObjectRegistry.LINEN_CARPET.get());
                output.accept(ObjectRegistry.LINEN_BED.get());
                output.accept(ObjectRegistry.JACQUARD_WOOL.get());
                output.accept(ObjectRegistry.JACQUARD_CARPET.get());
                output.accept(ObjectRegistry.JACQUARD_BED.get());
                output.accept(ObjectRegistry.PLAID_WOOL.get());
                output.accept(ObjectRegistry.PLAID_CARPET.get());
                output.accept(ObjectRegistry.PLAID_BED.get());
                output.accept(ObjectRegistry.CHAMBRAY_WOOL.get());
                output.accept(ObjectRegistry.CHAMBRAY_CARPET.get());
                output.accept(ObjectRegistry.CHAMBRAY_BED.get());
                output.accept(ObjectRegistry.TWEED_WOOL.get());
                output.accept(ObjectRegistry.TWEED_CARPET.get());
                output.accept(ObjectRegistry.TWEED_BED.get());
                output.accept(ObjectRegistry.WARPED_WOOL.get());
                output.accept(ObjectRegistry.WARPED_CARPET.get());
                output.accept(ObjectRegistry.WARPED_BED.get());
                output.accept(ObjectRegistry.STRAW_BED.get());

                output.accept(ObjectRegistry.FUR_HELMET.get());
                output.accept(ObjectRegistry.FUR_CHESTPLATE.get());
                output.accept(ObjectRegistry.FUR_LEGGINGS.get());
                output.accept(ObjectRegistry.FUR_BOOTS.get());

                output.accept(ObjectRegistry.ALPINE_SALT.get());
                output.accept(ObjectRegistry.RENNET.get());

                output.accept(ObjectRegistry.CHEESE_BLOCK.get());
                output.accept(ObjectRegistry.SHEEP_CHEESE_BLOCK.get());
                output.accept(ObjectRegistry.GRAIN_CHEESE_BLOCK.get());
                output.accept(ObjectRegistry.AMETHYST_CHEESE_BLOCK.get());
                output.accept(ObjectRegistry.BUFFALO_CHEESE_BLOCK.get());
                output.accept(ObjectRegistry.GOAT_CHEESE_BLOCK.get());
                output.accept(ObjectRegistry.WARPED_CHEESE_BLOCK.get());

                output.accept(ObjectRegistry.PIECE_OF_CHEESE.get());
                output.accept(ObjectRegistry.PIECE_OF_SHEEP_CHEESE.get());
                output.accept(ObjectRegistry.PIECE_OF_GRAIN_CHEESE.get());
                output.accept(ObjectRegistry.PIECE_OF_AMETHYST_CHEESE.get());
                output.accept(ObjectRegistry.PIECE_OF_BUFFALO_CHEESE.get());
                output.accept(ObjectRegistry.PIECE_OF_GOAT_CHEESE.get());
                output.accept(ObjectRegistry.PIECE_OF_WARPED_CHEESE.get());

                output.accept(ObjectRegistry.CHEESECAKE.get());
                output.accept(ObjectRegistry.CHEESE_TART.get());
                output.accept(ObjectRegistry.CHEESECAKE_SLICE.get());
                output.accept(ObjectRegistry.CHEESE_TART_SLICE.get());
                output.accept(ObjectRegistry.CHEESE_SANDWICH.get());
                output.accept(ObjectRegistry.CHEESE_ROLL.get());
                output.accept(ObjectRegistry.CHEESE_STICK.get());

                output.accept(ObjectRegistry.RAW_BUFFALO_MEAT.get());
                output.accept(ObjectRegistry.COOKED_BUFFALO_MEAT.get());
                output.accept(ObjectRegistry.ROASTED_HAM.get());
                output.accept(ObjectRegistry.SAUSAGE_WITH_CHEESE.get());

                output.accept(ObjectRegistry.WOODEN_BUCKET.get());
                output.accept(ObjectRegistry.WOODEN_WATER_BUCKET.get());
                output.accept(ObjectRegistry.WOODEN_MILK_BUCKET.get());
                output.accept(ObjectRegistry.WOODEN_SHEEP_MILK_BUCKET.get());
                output.accept(ObjectRegistry.WOODEN_BUFFALO_MILK_BUCKET.get());
                output.accept(ObjectRegistry.WOODEN_GOAT_MILK_BUCKET.get());
                output.accept(ObjectRegistry.WOODEN_WARPED_MILK_BUCKET.get());
                output.accept(ObjectRegistry.WOODEN_GRAIN_MILK_BUCKET.get());
                output.accept(ObjectRegistry.WOODEN_AMETHYST_MILK_BUCKET.get());
                output.accept(ObjectRegistry.MEADOW_BANNER.get());

                output.accept(ObjectRegistry.WATER_BUFFALO_SPAWN_EGG_ITEM.get());
                output.accept(ObjectRegistry.HIGHLAND_WOOLY_COW_SPAWN_EGG.get());
                output.accept(ObjectRegistry.UMBRA_WOOLY_COW_SPAWN_EGG.get());
                output.accept(ObjectRegistry.WARPED_WOOLY_COW_SPAWN_EGG.get());
            })
            .build());

    public static void init() {
        CREATIVE_MODE_TABS.register();
    }
}