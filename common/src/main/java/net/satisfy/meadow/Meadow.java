package net.satisfy.meadow;

import dev.architectury.hooks.item.tool.AxeItemHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.satisfy.meadow.core.registry.*;
import net.satisfy.meadow.core.util.WoodenCauldronBehavior;

public class Meadow {
    public static final String MOD_ID = "meadow";

    public static void init() {
        EntityTypeRegistry.init();
        ObjectRegistry.init();
        TabRegistry.init();
        RecipeRegistry.init();
        SoundEventRegistry.init();
        ScreenHandlerRegistry.init();
    }

    public static void commonSetup() {
        FlammableBlockRegistry.init();
        WoodenCauldronBehavior.bootStrap();
        AxeItemHooks.addStrippable(ObjectRegistry.PINE_LOG.get(), ObjectRegistry.STRIPPED_PINE_LOG.get());
        AxeItemHooks.addStrippable(ObjectRegistry.PINE_WOOD.get(), ObjectRegistry.STRIPPED_PINE_WOOD.get());
        AxeItemHooks.addStrippable(ObjectRegistry.ALPINE_BIRCH_LOG.get(), Blocks.STRIPPED_BIRCH_LOG);
        AxeItemHooks.addStrippable(ObjectRegistry.ALPINE_OAK_LOG.get(), Blocks.STRIPPED_OAK_LOG);
    }

    public static ResourceLocation identifier(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}


