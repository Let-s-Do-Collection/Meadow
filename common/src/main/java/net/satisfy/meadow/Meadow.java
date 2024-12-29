package net.satisfy.meadow;

import de.cristelknight.doapi.DoApiEP;
import dev.architectury.hooks.item.tool.AxeItemHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.satisfy.meadow.config.MeadowConfig;
import net.satisfy.meadow.registry.*;
import net.satisfy.meadow.util.WoodenCauldronBehavior;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Meadow {
    public static final String MOD_ID = "meadow";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        MeadowConfig.get();
        DataFixerRegistry.init();
        TabRegistry.init();
        ObjectRegistry.init();
        BoatsAndSignsRegistry.init();
        EntityRegistry.init();
        BlockEntityRegistry.init();
        RecipeRegistry.init();
        SoundRegistry.init();
        ScreenHandlerRegistry.init();
        DoApiEP.registerBuiltInPack(Meadow.MOD_ID, identifier("better_leaves"), false);
        DoApiEP.registerBuiltInPack(Meadow.MOD_ID, identifier("green_stove"), false);
        DoApiEP.registerBuiltInPack(Meadow.MOD_ID, identifier("optifine_support"), false);
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


