package net.satisfy.meadow;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;
import java.util.List;
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
        FoundationRarities.register(ObjectRegistry.MEADOW_BANNER.get(), FoundationRarity.LEGENDARY);
        for (RegistrySupplier<Item> piece : List.of(ObjectRegistry.FUR_HELMET, ObjectRegistry.FUR_CHESTPLATE, ObjectRegistry.FUR_LEGGINGS, ObjectRegistry.FUR_BOOTS)) {
            FoundationRarities.register(piece.get(), FoundationRarity.RARE);
        }
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
