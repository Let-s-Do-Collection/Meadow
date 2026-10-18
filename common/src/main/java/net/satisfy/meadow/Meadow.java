package net.satisfy.meadow;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.ItemLike;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;
import net.minecraft.server.packs.PackType;
import net.satisfy.meadow.core.cooking.CookingLiquids;
import dev.architectury.hooks.item.tool.AxeItemHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.satisfy.meadow.core.network.MeadowNetwork;
import net.satisfy.meadow.core.registry.*;
import net.satisfy.meadow.core.util.WoodenCauldronBehavior;
import net.satisfy.meadow.core.util.datafixer.DataFixerEntries;

public class Meadow {
    public static final String MOD_ID = "meadow";

    public static void init() {
        CompostableRegistry.registerCompostable();
        DataFixerEntries.init();
        ObjectRegistry.init();
        EntityTypeRegistry.init();
        TabRegistry.init();
        RecipeRegistry.init();
        PlacerTypeRegistry.init();
        SoundEventRegistry.init();
        ScreenHandlerRegistry.init();
        FlammableBlockRegistry.init();
        ArmorSetRegistry.init();
        MeadowNetwork.init();
        ReloadListenerRegistry.register(PackType.SERVER_DATA, CookingLiquids.INSTANCE);
        LifecycleEvent.SETUP.register(Meadow::setupSerial);
        LifecycleEvent.SETUP.register(Meadow::registerRarities);
    }

    public static void commonSetup() {
    }

    private static void setupSerial() {
        WoodenCauldronBehavior.bootStrap();
        AxeItemHooks.addStrippable(ObjectRegistry.PINE_LOG.get(), ObjectRegistry.STRIPPED_PINE_LOG.get());
        AxeItemHooks.addStrippable(ObjectRegistry.PINE_WOOD.get(), ObjectRegistry.STRIPPED_PINE_WOOD.get());
        AxeItemHooks.addStrippable(ObjectRegistry.ALPINE_BIRCH_LOG.get(), Blocks.STRIPPED_BIRCH_LOG);
    }

    private static void registerRarities() {
        registerRarity(FoundationRarity.COMMON,
                ObjectRegistry.ALPINE_SALT, ObjectRegistry.RENNET, ObjectRegistry.RAW_BUFFALO_MEAT, ObjectRegistry.COOKED_BUFFALO_MEAT,
                ObjectRegistry.WOODEN_BUCKET, ObjectRegistry.WOODEN_WATER_BUCKET, ObjectRegistry.WOODEN_MILK_BUCKET, ObjectRegistry.WOODEN_SHEEP_MILK_BUCKET,
                ObjectRegistry.WOODEN_BUFFALO_MILK_BUCKET, ObjectRegistry.WOODEN_GOAT_MILK_BUCKET, ObjectRegistry.WOODEN_GRAIN_MILK_BUCKET,
                ObjectRegistry.PINE_DOOR, ObjectRegistry.RECLAIMED_PINE_DOOR,
                ObjectRegistry.CHEESE_FORM, ObjectRegistry.COOKING_FRAME, ObjectRegistry.COOKING_CAULDRON, ObjectRegistry.FONDUE_POT, ObjectRegistry.WOODEN_CAULDRON,
                ObjectRegistry.WOODCUTTER, ObjectRegistry.WHEELBARROW, ObjectRegistry.MILK_CAN, ObjectRegistry.OIL_LANTERN, ObjectRegistry.FIREWOOD, ObjectRegistry.DOORMAT,
                ObjectRegistry.WOODEN_FLOWER_BOX, ObjectRegistry.WOODEN_FLOWER_POT_BIG, ObjectRegistry.WOODEN_FLOWER_POT_SMALL);
        registerRarity(FoundationRarity.UNCOMMON,
                ObjectRegistry.CHEESE_SANDWICH, ObjectRegistry.CHEESE_ROLL, ObjectRegistry.CHEESE_STICK, ObjectRegistry.SAUSAGE_WITH_CHEESE, ObjectRegistry.ROASTED_BUFFALO_HAM,
                ObjectRegistry.CHEESECAKE, ObjectRegistry.CHEESECAKE_SLICE, ObjectRegistry.CHEESE_TART, ObjectRegistry.CHEESE_TART_SLICE,
                ObjectRegistry.CHEESE_WHEEL, ObjectRegistry.CHEESE_SLICE, ObjectRegistry.SHEEP_CHEESE_WHEEL, ObjectRegistry.SHEEP_CHEESE_SLICE,
                ObjectRegistry.GOAT_CHEESE_WHEEL, ObjectRegistry.GOAT_CHEESE_SLICE, ObjectRegistry.BUFFALO_CHEESE_WHEEL, ObjectRegistry.BUFFALO_CHEESE_SLICE,
                ObjectRegistry.GRAIN_CHEESE_WHEEL, ObjectRegistry.GRAIN_CHEESE_SLICE,
                ObjectRegistry.WATERING_CAN_ITEM, ObjectRegistry.FELTING_NEEDLE,
                ObjectRegistry.PINE_SAPLING, ObjectRegistry.YELLOW_PINE_SAPLING, ObjectRegistry.ALPINE_BIRCH_SAPLING, ObjectRegistry.SMALL_FIR,
                ObjectRegistry.ALPINE_POPPY, ObjectRegistry.DELPHINIUM, ObjectRegistry.SAXIFRAGE, ObjectRegistry.ENZIAN, ObjectRegistry.FIRE_LILY,
                ObjectRegistry.ERIOPHORUM, ObjectRegistry.TALL_ERIOPHORUM);
        registerRarity(FoundationRarity.RARE,
                ObjectRegistry.FUR_HELMET, ObjectRegistry.FUR_CHESTPLATE, ObjectRegistry.FUR_LEGGINGS, ObjectRegistry.FUR_BOOTS, ObjectRegistry.CLIMBING_ROPE,
                ObjectRegistry.AMETHYST_CHEESE_WHEEL, ObjectRegistry.AMETHYST_CHEESE_SLICE, ObjectRegistry.WARPED_CHEESE_WHEEL, ObjectRegistry.WARPED_CHEESE_SLICE,
                ObjectRegistry.WOODEN_AMETHYST_MILK_BUCKET, ObjectRegistry.WOODEN_WARPED_MILK_BUCKET, ObjectRegistry.PHOTO);
        registerRarity(FoundationRarity.EPIC, ObjectRegistry.CAMERA,
                ObjectRegistry.WATER_BUFFALO_SPAWN_EGG_ITEM, ObjectRegistry.HIGHLAND_WOOLY_COW_SPAWN_EGG, ObjectRegistry.UMBRA_WOOLY_COW_SPAWN_EGG, ObjectRegistry.WARPED_WOOLY_COW_SPAWN_EGG);
        registerRarity(FoundationRarity.LEGENDARY, ObjectRegistry.MEADOW_BANNER);
    }

    @SafeVarargs
    private static void registerRarity(FoundationRarity rarity, RegistrySupplier<? extends ItemLike>... entries) {
        for (RegistrySupplier<? extends ItemLike> entry : entries) {
            FoundationRarities.register(entry.get(), rarity);
        }
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
