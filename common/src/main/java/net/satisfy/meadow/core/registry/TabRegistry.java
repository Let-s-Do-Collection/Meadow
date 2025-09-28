package net.satisfy.meadow.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.satisfy.meadow.Meadow;

@SuppressWarnings("unused")
public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Meadow.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> MEADOW_TAB = CREATIVE_MODE_TABS.register("meadow", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(ObjectRegistry.PIECE_OF_CHEESE.get()))
            .title(Component.translatable("creativetab.meadow.tab"))
            .displayItems((parameters, out) -> {
                out.accept(new ItemStack(ObjectRegistry.ALPINE_COAL_ORE.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_IRON_ORE.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_COPPER_ORE.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_GOLD_ORE.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_REDSTONE_ORE.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_LAPIS_ORE.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_EMERALD_ORE.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_DIAMOND_ORE.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_SALT_ORE.get()));

                out.accept(new ItemStack(ObjectRegistry.LIMESTONE.get()));
                out.accept(new ItemStack(ObjectRegistry.COBBLED_LIMESTONE.get()));
                out.accept(new ItemStack(ObjectRegistry.MOSSY_COBBLED_LIMESTONE.get()));
                out.accept(new ItemStack(ObjectRegistry.LIMESTONE_BRICKS.get()));
                out.accept(new ItemStack(ObjectRegistry.MOSSY_LIMESTONE_BRICKS.get()));
                out.accept(new ItemStack(ObjectRegistry.CRACKED_LIMESTONE_BRICKS.get()));
                out.accept(new ItemStack(ObjectRegistry.CHISELED_LIMESTONE_BRICKS.get()));
                out.accept(new ItemStack(ObjectRegistry.POLISHED_LIMESTONE_BRICKS.get()));
                out.accept(new ItemStack(ObjectRegistry.LIMESTONE_STAIRS.get()));
                out.accept(new ItemStack(ObjectRegistry.LIMESTONE_SLAB.get()));
                out.accept(new ItemStack(ObjectRegistry.COBBLED_LIMESTONE_STAIRS.get()));
                out.accept(new ItemStack(ObjectRegistry.COBBLED_LIMESTONE_SLAB.get()));
                out.accept(new ItemStack(ObjectRegistry.LIMESTONE_BRICK_STAIRS.get()));
                out.accept(new ItemStack(ObjectRegistry.LIMESTONE_BRICK_SLAB.get()));
                out.accept(new ItemStack(ObjectRegistry.MOSSY_COBBLED_LIMESTONE_STAIRS.get()));
                out.accept(new ItemStack(ObjectRegistry.MOSSY_COBBLED_LIMESTONE_SLAB.get()));
                out.accept(new ItemStack(ObjectRegistry.MOSSY_LIMESTONE_BRICK_STAIRS.get()));
                out.accept(new ItemStack(ObjectRegistry.MOSSY_LIMESTONE_BRICK_SLAB.get()));
                out.accept(new ItemStack(ObjectRegistry.LIMESTONE_WALL.get()));
                out.accept(new ItemStack(ObjectRegistry.COBBLED_LIMESTONE_WALL.get()));
                out.accept(new ItemStack(ObjectRegistry.LIMESTONE_BRICK_WALL.get()));
                out.accept(new ItemStack(ObjectRegistry.MOSSY_COBBLED_LIMESTONE_WALL.get()));
                out.accept(new ItemStack(ObjectRegistry.MOSSY_LIMESTONE_BRICK_WALL.get()));

                out.accept(new ItemStack(ObjectRegistry.PINE_LOG.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_WOOD.get()));
                out.accept(new ItemStack(ObjectRegistry.STRIPPED_PINE_LOG.get()));
                out.accept(new ItemStack(ObjectRegistry.STRIPPED_PINE_WOOD.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_PLANKS.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_STAIRS.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_SLAB.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_FENCE.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_FENCE_GATE.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_DOOR.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_TRAPDOOR.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_BARN_DOOR.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_BARN_TRAPDOOR.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_PRESSURE_PLATE.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_BUTTON.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_SIGN.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_HANGING_SIGN.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_BOAT.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_CHEST_BOAT.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_BEAM.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_RAILING.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_WINDOW.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_LEAVES.get()));
                out.accept(new ItemStack(ObjectRegistry.YELLOW_PINE_LEAVES.get()));
                out.accept(new ItemStack(ObjectRegistry.PINE_SAPLING.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_BIRCH_LOG.get()));
                out.accept(new ItemStack(ObjectRegistry.ALPINE_BIRCH_LEAVES_HANGING.get()));

                out.accept(new ItemStack(ObjectRegistry.STOVE.get()));
                out.accept(new ItemStack(ObjectRegistry.STOVE_WOOD.get()));
                out.accept(new ItemStack(ObjectRegistry.STOVE_LID.get()));
                out.accept(new ItemStack(ObjectRegistry.STOVE_BENCH.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODCUTTER.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_CAULDRON.get()));
                out.accept(new ItemStack(ObjectRegistry.COOKING_CAULDRON.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESE_RACK.get()));
                out.accept(new ItemStack(ObjectRegistry.SHELF.get()));
                out.accept(new ItemStack(ObjectRegistry.CHAIR.get()));
                out.accept(new ItemStack(ObjectRegistry.TABLE.get()));
                out.accept(new ItemStack(ObjectRegistry.BENCH.get()));
                out.accept(new ItemStack(ObjectRegistry.STONE_TABLE.get()));
                out.accept(new ItemStack(ObjectRegistry.STONE_BENCH.get()));
                out.accept(new ItemStack(ObjectRegistry.SHUTTER_BLOCK.get()));
                out.accept(new ItemStack(ObjectRegistry.HEART_PATTERNED_WINDOW.get()));
                out.accept(new ItemStack(ObjectRegistry.SUN_PATTERNED_WINDOW.get()));
                out.accept(new ItemStack(ObjectRegistry.FRAME.get()));
                out.accept(new ItemStack(ObjectRegistry.WHEELBARROW.get()));
                out.accept(new ItemStack(ObjectRegistry.DOORMAT.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_FLOWER_POT_BIG.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_FLOWER_POT_SMALL.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_FLOWER_BOX.get()));
                out.accept(new ItemStack(ObjectRegistry.OIL_LANTERN.get()));
                out.accept(new ItemStack(ObjectRegistry.FONDUE.get()));
                out.accept(new ItemStack(ObjectRegistry.CAMERA.get()));
                out.accept(new ItemStack(ObjectRegistry.CLIMBING_ROPE_TOPMOUNT.get()));
                out.accept(new ItemStack(ObjectRegistry.FIRE_LOG.get()));
                out.accept(new ItemStack(ObjectRegistry.CAN.get()));
                out.accept(new ItemStack(ObjectRegistry.FELTING_NEEDLE.get()));
                out.accept(new ItemStack(ObjectRegistry.WATERING_CAN.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESE_FORM.get()));

                out.accept(new ItemStack(ObjectRegistry.ALPINE_POPPY.get()));
                out.accept(new ItemStack(ObjectRegistry.DELPHINIUM.get()));
                out.accept(new ItemStack(ObjectRegistry.SAXIFRAGE.get()));
                out.accept(new ItemStack(ObjectRegistry.ENZIAN.get()));
                out.accept(new ItemStack(ObjectRegistry.FIRE_LILY.get()));
                out.accept(new ItemStack(ObjectRegistry.ERIOPHORUM.get()));
                out.accept(new ItemStack(ObjectRegistry.ERIOPHORUM_TALL.get()));
                out.accept(new ItemStack(ObjectRegistry.SMALL_FIR.get()));

                out.accept(new ItemStack(ObjectRegistry.RUSTIC_WOOL.get()));
                out.accept(new ItemStack(ObjectRegistry.RUSTIC_CARPET.get()));
                out.accept(new ItemStack(ObjectRegistry.RUSTIC_BED.get()));
                out.accept(new ItemStack(ObjectRegistry.LINEN_WOOL.get()));
                out.accept(new ItemStack(ObjectRegistry.LINEN_CARPET.get()));
                out.accept(new ItemStack(ObjectRegistry.LINEN_BED.get()));
                out.accept(new ItemStack(ObjectRegistry.JACQUARD_WOOL.get()));
                out.accept(new ItemStack(ObjectRegistry.JACQUARD_CARPET.get()));
                out.accept(new ItemStack(ObjectRegistry.JACQUARD_BED.get()));
                out.accept(new ItemStack(ObjectRegistry.PLAID_WOOL.get()));
                out.accept(new ItemStack(ObjectRegistry.PLAID_CARPET.get()));
                out.accept(new ItemStack(ObjectRegistry.PLAID_BED.get()));
                out.accept(new ItemStack(ObjectRegistry.CHAMBRAY_WOOL.get()));
                out.accept(new ItemStack(ObjectRegistry.CHAMBRAY_CARPET.get()));
                out.accept(new ItemStack(ObjectRegistry.CHAMBRAY_BED.get()));
                out.accept(new ItemStack(ObjectRegistry.TWEED_WOOL.get()));
                out.accept(new ItemStack(ObjectRegistry.TWEED_CARPET.get()));
                out.accept(new ItemStack(ObjectRegistry.TWEED_BED.get()));
                out.accept(new ItemStack(ObjectRegistry.WARPED_WOOL.get()));
                out.accept(new ItemStack(ObjectRegistry.WARPED_CARPET.get()));
                out.accept(new ItemStack(ObjectRegistry.WARPED_BED.get()));
                out.accept(new ItemStack(ObjectRegistry.STRAW_BED.get()));

                out.accept(new ItemStack(ObjectRegistry.FUR_HELMET.get()));
                out.accept(new ItemStack(ObjectRegistry.FUR_CHESTPLATE.get()));
                out.accept(new ItemStack(ObjectRegistry.FUR_LEGGINGS.get()));
                out.accept(new ItemStack(ObjectRegistry.FUR_BOOTS.get()));

                out.accept(new ItemStack(ObjectRegistry.ALPINE_SALT.get()));
                out.accept(new ItemStack(ObjectRegistry.RENNET.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESE_BLOCK.get()));
                out.accept(new ItemStack(ObjectRegistry.SHEEP_CHEESE_BLOCK.get()));
                out.accept(new ItemStack(ObjectRegistry.GRAIN_CHEESE_BLOCK.get()));
                out.accept(new ItemStack(ObjectRegistry.AMETHYST_CHEESE_BLOCK.get()));
                out.accept(new ItemStack(ObjectRegistry.BUFFALO_CHEESE_BLOCK.get()));
                out.accept(new ItemStack(ObjectRegistry.GOAT_CHEESE_BLOCK.get()));
                out.accept(new ItemStack(ObjectRegistry.WARPED_CHEESE_BLOCK.get()));
                out.accept(new ItemStack(ObjectRegistry.PIECE_OF_CHEESE.get()));
                out.accept(new ItemStack(ObjectRegistry.PIECE_OF_SHEEP_CHEESE.get()));
                out.accept(new ItemStack(ObjectRegistry.PIECE_OF_GRAIN_CHEESE.get()));
                out.accept(new ItemStack(ObjectRegistry.PIECE_OF_AMETHYST_CHEESE.get()));
                out.accept(new ItemStack(ObjectRegistry.PIECE_OF_BUFFALO_CHEESE.get()));
                out.accept(new ItemStack(ObjectRegistry.PIECE_OF_GOAT_CHEESE.get()));
                out.accept(new ItemStack(ObjectRegistry.PIECE_OF_WARPED_CHEESE.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESECAKE.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESE_TART.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESECAKE_SLICE.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESE_TART_SLICE.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESE_SANDWICH.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESE_ROLL.get()));
                out.accept(new ItemStack(ObjectRegistry.CHEESE_STICK.get()));

                out.accept(new ItemStack(ObjectRegistry.RAW_BUFFALO_MEAT.get()));
                out.accept(new ItemStack(ObjectRegistry.COOKED_BUFFALO_MEAT.get()));
                out.accept(new ItemStack(ObjectRegistry.ROASTED_HAM.get()));
                out.accept(new ItemStack(ObjectRegistry.SAUSAGE_WITH_CHEESE.get()));

                out.accept(new ItemStack(ObjectRegistry.WOODEN_BUCKET.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_WATER_BUCKET.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_MILK_BUCKET.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_SHEEP_MILK_BUCKET.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_BUFFALO_MILK_BUCKET.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_GOAT_MILK_BUCKET.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_WARPED_MILK_BUCKET.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_GRAIN_MILK_BUCKET.get()));
                out.accept(new ItemStack(ObjectRegistry.WOODEN_AMETHYST_MILK_BUCKET.get()));

                out.accept(new ItemStack(ObjectRegistry.WATER_BUFFALO_SPAWN_EGG_ITEM.get()));
                out.accept(new ItemStack(ObjectRegistry.HIGHLAND_WOOLY_COW_SPAWN_EGG.get()));
                out.accept(new ItemStack(ObjectRegistry.UMBRA_WOOLY_COW_SPAWN_EGG.get()));
                out.accept(new ItemStack(ObjectRegistry.WARPED_WOOLY_COW_SPAWN_EGG.get()));
            })
            .build());

    public static void init() {
        CREATIVE_MODE_TABS.register();
    }
}